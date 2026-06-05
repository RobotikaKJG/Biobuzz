#!/usr/bin/env bash
# Barebones deploy: build APK -> switch to hub WiFi -> adb install -> disconnect -> restore WiFi.
set -uo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
[ -f "$SCRIPT_DIR/.env" ] && set -a && source "$SCRIPT_DIR/.env" && set +a

HUB_IP="192.168.43.1"
HUB="$HUB_IP:5555"
APK="TeamCode/build/outputs/apk/debug/TeamCode-debug.apk"

FTC_SSID="${FTC_SSID:-24500-RC}"
FTC_PASS="${FTC_PASS:-LITBOT100}"
HOME_PASS="${HOME_PASS:-}"

OS="$(uname -s)"
case "$OS" in
    Darwin)              WIFI_IF="${WIFI_IF:-en0}" ;;
    Linux)               WIFI_IF="${WIFI_IF:-wlan0}" ;;
    MINGW*|MSYS*|CYGWIN*) OS="Windows"; WIFI_IF="${WIFI_IF:-Wi-Fi}" ;;
    *)                   echo "Unsupported OS: $OS"; exit 1 ;;
esac

TIMEOUT=60

ts()  { date "+%H:%M:%S"; }
log() { echo "[$(ts)] $*"; }
logn(){ printf "\r[$(ts)] %s" "$*"; }

# ── WiFi helpers ─────────────────────────────────────────────────────────────

wifi_connect() {
    local ssid="$1" pass="${2:-}"
    case "$OS" in
        Darwin)
            if [[ -n "$pass" ]]; then
                networksetup -setairportnetwork "$WIFI_IF" "$ssid" "$pass" 2>&1
            else
                networksetup -setairportnetwork "$WIFI_IF" "$ssid" 2>&1
            fi ;;
        Linux)
            if [[ -n "$pass" ]]; then
                nmcli device wifi connect "$ssid" password "$pass" ifname "$WIFI_IF" 2>&1
            else
                nmcli device wifi connect "$ssid" ifname "$WIFI_IF" 2>&1
            fi ;;
        Windows)
            netsh.exe wlan connect name="$ssid" interface="$WIFI_IF" 2>&1 ;;
    esac
}

wifi_current_ssid() {
    case "$OS" in
        Darwin)  networksetup -getairportnetwork "$WIFI_IF" 2>&1 | sed 's/.*: //' ;;
        Linux)   nmcli -t -f active,ssid dev wifi 2>/dev/null | grep '^yes:' | cut -d: -f2 ;;
        Windows) netsh.exe wlan show interfaces 2>/dev/null | grep '^ *SSID' | head -1 | sed 's/.*: //' ;;
    esac
}

get_ip() {
    case "$OS" in
        Darwin)  ipconfig getifaddr "$WIFI_IF" 2>/dev/null ;;
        Linux)   ip -4 addr show "$WIFI_IF" 2>/dev/null | grep -oP 'inet \K[^/]+' ;;
        Windows) ipconfig.exe 2>/dev/null | sed -n "/adapter $WIFI_IF/,/^[^ ]/p" | grep 'IPv4' | sed 's/.*: //' | tr -d '\r' ;;
    esac
}

wifi_power() {
    local state="$1"
    case "$OS" in
        Darwin)  networksetup -setairportpower "$WIFI_IF" "$state" &>/dev/null ;;
        Linux)   nmcli radio wifi "$state" &>/dev/null ;;
        Windows)
            if [[ "$state" == "off" ]]; then
                netsh.exe interface set interface "$WIFI_IF" disabled &>/dev/null
            else
                netsh.exe interface set interface "$WIFI_IF" enabled &>/dev/null
            fi ;;
    esac
}

switch_wifi() {
    local ssid="$1" pass="${2:-}" max="$3" result
    logn "WiFi -> $ssid "
    for _ in $(seq 1 "$max"); do
        result=$(wifi_connect "$ssid" "$pass")
        if [[ "$result" != *"Could not find"* && "$result" != *"Failed"* && "$result" != *"Error"* ]]; then
            echo ""; log "WiFi -> $ssid OK"; return 0
        fi
        printf "."; sleep 2
    done
    echo ""; return 1
}

SAVED_NETWORK=""
SAVED_IP=""

save_current_network() {
    SAVED_IP=$(get_ip || true)
    SAVED_NETWORK=$(wifi_current_ssid)
    if [[ "$SAVED_NETWORK" == *"not associated"* ]]; then
        # iPhone Instant Hotspot isn't visible as a regular SSID; detect by IP range
        if [[ "$SAVED_IP" == 172.20.10.* ]]; then
            SAVED_NETWORK="__hotspot__"
        else
            SAVED_NETWORK=""
        fi
    fi
}

restore_wifi() {
    echo ""
    adb disconnect "$HUB" &>/dev/null
    sleep 1
    if [[ "$SAVED_NETWORK" == "__hotspot__" ]]; then
        # iPhone Instant Hotspot reconnects automatically once FTC is dropped
        log "Reconnecting to iPhone hotspot..."
        wifi_power off
        sleep 1
        wifi_power on
        local elapsed=0
        while [ $elapsed -lt 30 ]; do
            local ip
            ip=$(get_ip || true)
            if [[ -n "$ip" && "$ip" == 172.20.10.* ]]; then
                log "iPhone hotspot reconnected ($ip)"
                return
            fi
            sleep 1
            elapsed=$((elapsed + 1))
        done
        log "Could not reconnect to iPhone hotspot - reconnect manually"
    elif [[ -n "$SAVED_NETWORK" ]]; then
        switch_wifi "$SAVED_NETWORK" "$HOME_PASS" 15 || \
            log "Could not reconnect to $SAVED_NETWORK - reconnect manually"
    else
        log "No previous network to restore"
    fi
}

# ── Connect to hub ───────────────────────────────────────────────────────────

connect_hub() {
    save_current_network

    if ! switch_wifi "$FTC_SSID" "$FTC_PASS" 30; then
        log "Could not find $FTC_SSID - is the Control Hub powered on?"
        exit 1
    fi
    trap restore_wifi EXIT

    logn "Pinging hub "
    local elapsed=0
    while [ $elapsed -lt $TIMEOUT ]; do
        case "$OS" in
            Windows) ping.exe -n 1 -w 1000 "$HUB_IP" &>/dev/null && break ;;
            *)       ping -c 1 -W 1 "$HUB_IP" &>/dev/null && break ;;
        esac
        printf "."; sleep 1; elapsed=$((elapsed + 1))
    done
    if [ $elapsed -ge $TIMEOUT ]; then
        echo ""; log "Hub not reachable after ${TIMEOUT}s"; exit 1
    fi
    echo ""; log "Hub reachable (${elapsed}s)"

    logn "ADB connect "
    adb disconnect "$HUB" &>/dev/null
    adb connect "$HUB" &>/dev/null
    for _ in $(seq 1 10); do
        if [[ "$(adb -s "$HUB" get-state 2>/dev/null)" == "device" ]]; then
            echo ""; log "ADB connected"; return
        fi
        printf "."; sleep 1
    done
    echo ""
    log "ADB could not connect - unplug battery, wait 5s, replug"
    exit 1
}

# ── Main ─────────────────────────────────────────────────────────────────────

log "Building APK..."
./gradlew assembleDebug -q
log "Build complete"

connect_hub

log "Installing APK..."
install_output=$(adb -s "$HUB" install -r -g "$APK" 2>&1)
install_exit=$?
echo "$install_output" | while IFS= read -r line; do log "  $line"; done

if [[ $install_exit -eq 0 && "$install_output" == *"Success"* ]]; then
    log "Deploy complete!"
else
    log "Install failed (exit $install_exit)"
    exit 1
fi
