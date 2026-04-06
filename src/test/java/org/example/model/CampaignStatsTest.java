package org.example.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class CampaignStatsTest {

    @Test
    void accumulate_sums_metrics() {
        CampaignStats s = new CampaignStats("CMP001");
        s.accumulate(10_000L, 250L, 100.0, 20L);
        s.accumulate(5_000L, 50L, 40.0, 5L);

        assertEquals(15_000L, s.getTotalImpressions());
        assertEquals(300L, s.getTotalClicks());
        assertEquals(140.0, s.getTotalSpend(), 1e-9);
        assertEquals(25L, s.getTotalConversions());
    }

    @Test
    void ctr_is_clicks_over_impressions() {
        CampaignStats s = new CampaignStats("CMP001");
        s.accumulate(100L, 25L, 0.0, 0L);
        assertEquals(0.25, s.getCtr(), 1e-12);
    }

    @Test
    void ctr_is_zero_when_no_impressions() {
        CampaignStats s = new CampaignStats("CMP001");
        s.accumulate(0L, 5L, 10.0, 1L);
        assertEquals(0.0, s.getCtr(), 0.0);
    }

    @Test
    void cpa_is_null_when_no_conversions() {
        CampaignStats s = new CampaignStats("CMP001");
        s.accumulate(100L, 10L, 50.0, 0L);
        assertNull(s.getCpa());
    }

    @Test
    void cpa_is_spend_over_conversions() {
        CampaignStats s = new CampaignStats("CMP001");
        s.accumulate(100L, 10L, 100.0, 4L);
        assertEquals(25.0, s.getCpa(), 1e-12);
    }
}
