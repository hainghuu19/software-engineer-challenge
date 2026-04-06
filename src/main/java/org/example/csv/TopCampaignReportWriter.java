package org.example.csv;

import org.example.model.CampaignStats;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Writes ranked subsets of {@link CampaignStats} to CSV files under an output directory.
 */
public final class TopCampaignReportWriter {

    private static final String HEADER =
            "campaign_id,total_impressions,total_clicks,total_spend,total_conversions,ctr,cpa";

    private final Path outputDir;

    public TopCampaignReportWriter(Path outputDir) {
        this.outputDir = Objects.requireNonNull(outputDir);
    }

    public void writeTopByCtr(List<CampaignStats> all, int limit) throws IOException {
        Path file = outputDir.resolve("top10_ctr.csv");
        List<CampaignStats> top = all.stream()
                .sorted(Comparator.comparingDouble(CampaignStats::getCtr).reversed()
                        .thenComparing(CampaignStats::getCampaignId))
                .limit(limit)
                .toList();
        writeReport(file, top);
    }

    public void writeTopByLowestCpa(List<CampaignStats> all, int limit) throws IOException {
        Path file = outputDir.resolve("top10_cpa.csv");
        List<CampaignStats> eligible = all.stream()
                .filter(s -> s.getTotalConversions() > 0L)
                .sorted(Comparator.comparingDouble(CampaignStats::getCpa)
                        .thenComparing(CampaignStats::getCampaignId))
                .limit(limit)
                .toList();
        writeReport(file, eligible);
    }

    private void writeReport(Path file, List<CampaignStats> rows) throws IOException {
        Files.createDirectories(outputDir);
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(Files.newOutputStream(file), StandardCharsets.UTF_8))) {
            writer.write(HEADER);
            writer.newLine();
            for (CampaignStats s : rows) {
                writer.write(formatRow(s));
                writer.newLine();
            }
        }
    }

    private static String formatRow(CampaignStats s) {
        Double cpa = s.getCpa();
        String cpaField = cpa == null ? "" : formatDouble(cpa);
        return String.join(",",
                escapeCsv(s.getCampaignId()),
                Long.toString(s.getTotalImpressions()),
                Long.toString(s.getTotalClicks()),
                formatDouble(s.getTotalSpend()),
                Long.toString(s.getTotalConversions()),
                formatDouble(s.getCtr()),
                cpaField);
    }

    private static String formatDouble(double value) {
        return String.format(Locale.ROOT, "%.6f", value);
    }

    private static String escapeCsv(String value) {
        boolean needsQuotes = value.indexOf(',') >= 0 || value.indexOf('"') >= 0 || value.contains("\n")
                || value.contains("\r");
        if (!needsQuotes) {
            return value;
        }
        String escaped = value.replace("\"", "\"\"");
        return "\"" + escaped + "\"";
    }
}
