package org.firstinspires.ftc.teamcode.Main;

import android.content.Context;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class LoopTimeLogger {
    private static final double SPIKE_THRESHOLD_MS = 30.0;

    private final Context context;
    private final String opModeName;
    private final List<LoopSample> loopSamples = new ArrayList<>();
    private final List<LoopSection> currentSections = new ArrayList<>();
    private long startNs;
    private long previousNs;
    private long loopStartNs;
    private long sectionStartNs;
    private File lastSavedFile;
    private boolean loopActive = false;

    public LoopTimeLogger(Context context, String opModeName) {
        this.context = context;
        this.opModeName = opModeName;
    }

    public void start() {
        startNs = System.nanoTime();
        previousNs = startNs;
        loopSamples.clear();
        currentSections.clear();
        lastSavedFile = null;
    }

    public void startLoop() {
        loopStartNs = System.nanoTime();
        sectionStartNs = loopStartNs;
        currentSections.clear();
        loopActive = true;
    }

    public void recordSection(String sectionName) {
        if (!loopActive) {
            return;
        }

        long currentNs = System.nanoTime();
        currentSections.add(new LoopSection(sectionName, nsToMs(currentNs - sectionStartNs)));
        sectionStartNs = currentNs;
    }

    public void recordDurationNs(String sectionName, long durationNs) {
        if (!loopActive) {
            return;
        }

        currentSections.add(new LoopSection(sectionName, nsToMs(durationNs)));
    }

    public double getCurrentLoopMs() {
        return nsToMs(System.nanoTime() - loopStartNs);
    }

    public double finishLoop() {
        long currentNs = System.nanoTime();
        double elapsedMs = nsToMs(currentNs - startNs);
        double loopMs = nsToMs(currentNs - loopStartNs);

        double untrackedMs = loopMs - getTrackedSectionMs();
        if (untrackedMs > 0.01) {
            currentSections.add(new LoopSection("untracked", untrackedMs));
        }

        loopSamples.add(new LoopSample(
                loopSamples.size() + 1,
                elapsedMs,
                loopMs,
                new ArrayList<>(currentSections),
                loopMs >= SPIKE_THRESHOLD_MS ? getStackTrace() : new ArrayList<String>()
        ));
        previousNs = currentNs;
        loopActive = false;

        return loopMs;
    }

    public double recordLoop() {
        long currentNs = System.nanoTime();
        double elapsedMs = nsToMs(currentNs - startNs);
        double loopMs = nsToMs(currentNs - previousNs);

        loopSamples.add(new LoopSample(
                loopSamples.size() + 1,
                elapsedMs,
                loopMs,
                new ArrayList<LoopSection>(),
                loopMs >= SPIKE_THRESHOLD_MS ? getStackTrace() : new ArrayList<String>()
        ));
        previousNs = currentNs;

        return loopMs;
    }

    public File save() throws IOException {
        File baseDirectory = context.getExternalFilesDir(null);
        if (baseDirectory == null) {
            baseDirectory = context.getFilesDir();
        }

        File directory = new File(baseDirectory, "loop-times");
        if (!directory.exists() && !directory.mkdirs()) {
            throw new IOException("Could not create loop-times directory: " + directory.getAbsolutePath());
        }

        String timestamp = new SimpleDateFormat("yyyyMMdd-HHmmss", Locale.US).format(new Date());
        String fileName = sanitizeFileName(opModeName) + "-" + timestamp + ".txt";
        File file = new File(directory, fileName);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("opMode=" + opModeName);
            writer.newLine();
            writer.write("sampleCount=" + loopSamples.size());
            writer.newLine();
            writer.write(String.format(Locale.US, "averageLoopMs=%.3f", getAverageLoopMs()));
            writer.newLine();
            writer.write(String.format(Locale.US, "maxLoopMs=%.3f", getMaxLoopMs()));
            writer.newLine();
            writer.write(String.format(Locale.US, "spikeThresholdMs=%.3f", SPIKE_THRESHOLD_MS));
            writer.newLine();
            writer.newLine();
            writer.write("loop,elapsedMs,loopMs,spike,sections");
            writer.newLine();

            for (LoopSample sample : loopSamples) {
                writer.write(String.format(
                        Locale.US,
                        "%d,%.3f,%.3f,%s,%s",
                        sample.loopNumber,
                        sample.elapsedMs,
                        sample.loopMs,
                        sample.loopMs >= SPIKE_THRESHOLD_MS ? "YES" : "NO",
                        formatSections(sample.sections)
                ));
                writer.newLine();
            }

            writer.newLine();
            writer.write("Spike details");
            writer.newLine();
            for (LoopSample sample : loopSamples) {
                if (sample.loopMs < SPIKE_THRESHOLD_MS) {
                    continue;
                }

                writer.write(String.format(
                        Locale.US,
                        "loop=%d elapsedMs=%.3f loopMs=%.3f sections=%s",
                        sample.loopNumber,
                        sample.elapsedMs,
                        sample.loopMs,
                        formatSections(sample.sections)
                ));
                writer.newLine();
                writer.write("stackAtLoopEnd=");
                writer.newLine();
                for (String stackLine : sample.stackTrace) {
                    writer.write("  " + stackLine);
                    writer.newLine();
                }
                writer.newLine();
            }
        }

        lastSavedFile = file;
        return file;
    }

    public File getLastSavedFile() {
        return lastSavedFile;
    }

    public void saveToTelemetry(Telemetry telemetry) {
        try {
            File file = save();
            telemetry.addData("Loop log saved", file.getAbsolutePath());
        } catch (IOException e) {
            telemetry.addData("Loop log save failed", e.getMessage());
        }
        telemetry.update();
    }

    private double getAverageLoopMs() {
        if (loopSamples.isEmpty()) {
            return 0.0;
        }

        double totalMs = 0.0;
        for (LoopSample sample : loopSamples) {
            totalMs += sample.loopMs;
        }
        return totalMs / loopSamples.size();
    }

    private double getMaxLoopMs() {
        double maxMs = 0.0;
        for (LoopSample sample : loopSamples) {
            maxMs = Math.max(maxMs, sample.loopMs);
        }
        return maxMs;
    }

    private double getTrackedSectionMs() {
        double trackedMs = 0.0;
        for (LoopSection section : currentSections) {
            trackedMs += section.durationMs;
        }
        return trackedMs;
    }

    private double nsToMs(long ns) {
        return ns / 1_000_000.0;
    }

    private String sanitizeFileName(String value) {
        return value.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    private String formatSections(List<LoopSection> sections) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < sections.size(); i++) {
            LoopSection section = sections.get(i);
            if (i > 0) {
                builder.append(';');
            }
            builder.append(section.name)
                    .append('=')
                    .append(String.format(Locale.US, "%.3f", section.durationMs));
        }
        return builder.toString();
    }

    private List<String> getStackTrace() {
        List<String> stackLines = new ArrayList<>();
        for (StackTraceElement element : Thread.currentThread().getStackTrace()) {
            stackLines.add(element.toString());
        }
        return stackLines;
    }

    private static class LoopSection {
        final String name;
        final double durationMs;

        LoopSection(String name, double durationMs) {
            this.name = name.replaceAll("[,;=]", "_");
            this.durationMs = durationMs;
        }
    }

    private static class LoopSample {
        final int loopNumber;
        final double elapsedMs;
        final double loopMs;
        final List<LoopSection> sections;
        final List<String> stackTrace;

        LoopSample(
                int loopNumber,
                double elapsedMs,
                double loopMs,
                List<LoopSection> sections,
                List<String> stackTrace
        ) {
            this.loopNumber = loopNumber;
            this.elapsedMs = elapsedMs;
            this.loopMs = loopMs;
            this.sections = sections;
            this.stackTrace = stackTrace;
        }
    }
}
