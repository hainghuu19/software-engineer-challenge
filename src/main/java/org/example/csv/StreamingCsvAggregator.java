package org.example.csv;

import org.example.model.CampaignStats;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Streams a large CSV through a {@link BufferedReader} and aggregates rows into a {@link CampaignStats} map.
 */
public final class StreamingCsvAggregator {

    private final Path inputFile;

    public StreamingCsvAggregator(Path inputFile) {
        this.inputFile = Objects.requireNonNull(inputFile);
    }

    /**
     * @return map of campaign id to aggregated stats, never null
     */
    public AggregatedResult aggregateAll() throws IOException {
        Map<String, CampaignStats> byCampaign = new HashMap<>();
        long linesRead = 0L;
        long rowsSkipped = 0L;

        try (BufferedReader reader = newBufferedReader(inputFile)) {
            String line;
            boolean firstLine = true;
            while ((line = reader.readLine()) != null) {
                linesRead++;

                if (firstLine) {
                    firstLine = false;
                    if (CsvRowParser.looksLikeHeaderRow(line)) {
                        continue;
                    }
                }

                var parsed = CsvRowParser.parseLine(line);
                if (parsed.isPresent()) {
                    CsvRowParser.ParsedRow row = parsed.get();
                    CampaignStats stats = byCampaign.computeIfAbsent(row.campaignId(), CampaignStats::new);
                    stats.accumulate(row.impressions(), row.clicks(), row.spend(), row.conversions());
                } else {
                    rowsSkipped++;
                }
            }
        }

        return new AggregatedResult(byCampaign, linesRead, rowsSkipped);
    }

    private static BufferedReader newBufferedReader(Path path) throws IOException {
        return new BufferedReader(
                new InputStreamReader(Files.newInputStream(path), StandardCharsets.UTF_8));
    }

    public record AggregatedResult(
            Map<String, CampaignStats> byCampaignId,
            long linesRead,
            long rowsSkipped) {
    }
}
