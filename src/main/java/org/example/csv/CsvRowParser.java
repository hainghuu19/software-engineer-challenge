package org.example.csv;

import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Parses one CSV data line into numeric fields. Invalid lines yield {@link Optional#empty()}.
 * Assumes no commas inside fields (matches the provided sample format).
 */
public final class CsvRowParser {

    private static final Pattern DELIMITER = Pattern.compile(",");

    private CsvRowParser() {
    }

    /** First line is treated as header when it starts with {@code campaign_id} (case-insensitive). */
    public static boolean looksLikeHeaderRow(String line) {
        if (line == null) {
            return false;
        }
        String trimmed = line.trim();
        return trimmed.regionMatches(true, 0, "campaign_id", 0, "campaign_id".length());
    }

    public record ParsedRow(String campaignId, long impressions, long clicks, double spend, long conversions) {
    }

    /**
     * @param line raw line from the reader (not a header row)
     */
    public static Optional<ParsedRow> parseLine(String line) {
        if (line == null) {
            return Optional.empty();
        }
        String trimmed = line.trim();
        if (trimmed.isEmpty()) {
            return Optional.empty();
        }

        String[] parts = DELIMITER.split(trimmed, -1);
        if (parts.length != 6) {
            return Optional.empty();
        }

        String campaignId = parts[0].trim();
        if (campaignId.isEmpty()) {
            return Optional.empty();
        }

        try {
            // date (parts[1]) not used in aggregation; column count is the main guard
            long impressions = Long.parseLong(parts[2].trim());
            long clicks = Long.parseLong(parts[3].trim());
            double spend = Double.parseDouble(parts[4].trim());
            long conversions = Long.parseLong(parts[5].trim());

            if (impressions < 0 || clicks < 0 || spend < 0 || conversions < 0) {
                return Optional.empty();
            }

            return Optional.of(new ParsedRow(campaignId, impressions, clicks, spend, conversions));
        } catch (NumberFormatException ex) {
            return Optional.empty();
        }
    }
}
