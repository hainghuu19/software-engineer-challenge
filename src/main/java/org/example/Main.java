package org.example;

import org.example.cli.CliArgs;
import org.example.csv.StreamingCsvAggregator;
import org.example.csv.TopCampaignReportWriter;
import org.example.model.CampaignStats;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * CLI entry: stream-aggregates {@code ad_data.csv} and writes top-10 CTR and CPA reports.
 */
public final class Main {

    private static final int TOP_N = 10;

    public static void main(String[] args) {
        try {
            run(args);
        } catch (IllegalArgumentException ex) {
            System.err.println(ex.getMessage());
            System.exit(2);
        } catch (IOException ex) {
            System.err.println("I/O error: " + ex.getMessage());
            System.exit(1);
        }
    }

    private static void run(String[] args) throws IOException {
        CliArgs cli = CliArgs.parse(args);
        Path input = cli.inputFile();
        Path outputDir = cli.outputDir();

        if (!Files.isRegularFile(input)) {
            throw new IllegalArgumentException("Input file does not exist or is not a file: " + input.toAbsolutePath());
        }
        if (!Files.isReadable(input)) {
            throw new IllegalArgumentException("Input file is not readable: " + input.toAbsolutePath());
        }

        StreamingCsvAggregator aggregator = new StreamingCsvAggregator(input);
        StreamingCsvAggregator.AggregatedResult aggregated = aggregator.aggregateAll();

        Map<String, CampaignStats> map = aggregated.byCampaignId();
        if (map.isEmpty()) {
            System.err.println("No data rows aggregated (file may be empty or all rows invalid).");
        }

        List<CampaignStats> all = new ArrayList<>(map.values());
        TopCampaignReportWriter writer = new TopCampaignReportWriter(outputDir);
        writer.writeTopByCtr(all, TOP_N);
        writer.writeTopByLowestCpa(all, TOP_N);

        System.out.printf(Locale.US,
                "Done. Lines read: %d, data rows skipped: %d, unique campaigns: %d%n",
                aggregated.linesRead(),
                aggregated.rowsSkipped(),
                map.size());
        System.out.println("Written: " + outputDir.resolve("top10_ctr.csv").toAbsolutePath());
        System.out.println("Written: " + outputDir.resolve("top10_cpa.csv").toAbsolutePath());
    }
}
