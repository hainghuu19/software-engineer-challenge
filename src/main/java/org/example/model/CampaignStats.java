package org.example.model;

/**
 * Aggregated metrics for a single campaign, built incrementally while streaming the CSV.
 */
public final class CampaignStats {

    private final String campaignId;
    private long totalImpressions;
    private long totalClicks;
    private double totalSpend;
    private long totalConversions;

    public CampaignStats(String campaignId) {
        this.campaignId = campaignId;
    }

    public void accumulate(long impressions, long clicks, double spend, long conversions) {
        this.totalImpressions += impressions;
        this.totalClicks += clicks;
        this.totalSpend += spend;
        this.totalConversions += conversions;
    }

    public String getCampaignId() {
        return campaignId;
    }

    public long getTotalImpressions() {
        return totalImpressions;
    }

    public long getTotalClicks() {
        return totalClicks;
    }

    public double getTotalSpend() {
        return totalSpend;
    }

    public long getTotalConversions() {
        return totalConversions;
    }

    /**
     * Click-through rate. When there are no impressions, CTR is defined as 0 to keep sorting well-defined.
     */
    public double getCtr() {
        if (totalImpressions == 0L) {
            return 0.0;
        }
        return (double) totalClicks / (double) totalImpressions;
    }

    /**
     * Cost per acquisition. {@code null} when there are no conversions.
     */
    public Double getCpa() {
        if (totalConversions == 0L) {
            return null;
        }
        return totalSpend / (double) totalConversions;
    }
}
