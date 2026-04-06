package org.example.csv;

import org.example.model.CampaignStats;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StreamingCsvAggregatorTest {

    @Test
    void aggregateAll_skips_header_and_groups_campaigns(@TempDir Path dir) throws IOException {
        Path csv = dir.resolve("sample.csv");
        Files.writeString(csv, String.join(System.lineSeparator(),
                "campaign_id,date,impressions,clicks,spend,conversions",
                "CMP001,2025-01-01,1000,100,50.0,10",
                "CMP002,2025-01-01,2000,50,30.0,0",
                "CMP001,2025-01-02,1000,100,50.0,10",
                "not,a,valid,line",
                ""), StandardCharsets.UTF_8);

        StreamingCsvAggregator agg = new StreamingCsvAggregator(csv);
        StreamingCsvAggregator.AggregatedResult result = agg.aggregateAll();

        Map<String, CampaignStats> map = result.byCampaignId();
        assertEquals(2, map.size());
        assertTrue(result.rowsSkipped() >= 1L);

        CampaignStats c1 = map.get("CMP001");
        assertEquals(2000L, c1.getTotalImpressions());
        assertEquals(200L, c1.getTotalClicks());
        assertEquals(100.0, c1.getTotalSpend(), 1e-9);
        assertEquals(20L, c1.getTotalConversions());

        CampaignStats c2 = map.get("CMP002");
        assertEquals(0.025, c2.getCtr(), 1e-12);
        assertNull(c2.getCpa());
    }

    @Test
    void aggregateAll_first_line_data_when_no_header(@TempDir Path dir) throws IOException {
        Path csv = dir.resolve("no_header.csv");
        Files.writeString(csv, "CMP001,2025-01-01,10,1,2.0,1", StandardCharsets.UTF_8);

        StreamingCsvAggregator.AggregatedResult result = new StreamingCsvAggregator(csv).aggregateAll();
        CampaignStats s = result.byCampaignId().get("CMP001");
        assertEquals(10L, s.getTotalImpressions());
    }
}
