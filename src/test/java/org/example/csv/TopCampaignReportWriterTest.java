package org.example.csv;

import org.example.model.CampaignStats;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TopCampaignReportWriterTest {

    @Test
    void writeTopByCtr_orders_by_ctr_desc_then_campaign_id(@TempDir Path dir) throws IOException {
        CampaignStats low = new CampaignStats("CMP_B");
        low.accumulate(100L, 5L, 10.0, 1L);

        CampaignStats high = new CampaignStats("CMP_A");
        high.accumulate(100L, 10L, 10.0, 1L);

        CampaignStats tieHigherId = new CampaignStats("CMP_C");
        tieHigherId.accumulate(100L, 10L, 20.0, 1L);

        List<CampaignStats> all = List.of(low, high, tieHigherId);

        TopCampaignReportWriter writer = new TopCampaignReportWriter(dir);
        writer.writeTopByCtr(all, 10);

        Path file = dir.resolve("top10_ctr.csv");
        List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
        assertEquals(4, lines.size());
        assertEquals("campaign_id,total_impressions,total_clicks,total_spend,total_conversions,ctr,cpa", lines.get(0));
        assertTrue(lines.get(1).startsWith("CMP_A,"));
        assertTrue(lines.get(2).startsWith("CMP_C,"));
        assertTrue(lines.get(3).startsWith("CMP_B,"));
    }

    @Test
    void writeTopByLowestCpa_excludes_zero_conversions(@TempDir Path dir) throws IOException {
        CampaignStats noConv = new CampaignStats("CMP_Z");
        noConv.accumulate(100L, 10L, 50.0, 0L);

        CampaignStats cheap = new CampaignStats("CMP_Y");
        cheap.accumulate(100L, 10L, 20.0, 10L);

        CampaignStats pricey = new CampaignStats("CMP_X");
        pricey.accumulate(100L, 10L, 100.0, 10L);

        TopCampaignReportWriter writer = new TopCampaignReportWriter(dir);
        writer.writeTopByLowestCpa(List.of(noConv, cheap, pricey), 10);

        List<String> lines = Files.readAllLines(dir.resolve("top10_cpa.csv"), StandardCharsets.UTF_8);
        assertEquals(3, lines.size());
        assertTrue(lines.get(1).startsWith("CMP_Y,"));
        assertTrue(lines.get(2).startsWith("CMP_X,"));
    }
}
