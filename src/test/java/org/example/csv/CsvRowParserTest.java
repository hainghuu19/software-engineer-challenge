package org.example.csv;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CsvRowParserTest {

    @Test
    void looksLikeHeaderRow_detects_header() {
        assertTrue(CsvRowParser.looksLikeHeaderRow("campaign_id,date,impressions,clicks,spend,conversions"));
        assertTrue(CsvRowParser.looksLikeHeaderRow("Campaign_ID,x"));
    }

    @Test
    void looksLikeHeaderRow_rejects_non_header() {
        assertFalse(CsvRowParser.looksLikeHeaderRow("CMP001,2025-01-01,1,1,1.0,1"));
        assertFalse(CsvRowParser.looksLikeHeaderRow(""));
        assertFalse(CsvRowParser.looksLikeHeaderRow(null));
    }

    @Test
    void parseLine_valid_row() {
        Optional<CsvRowParser.ParsedRow> row = CsvRowParser.parseLine(
                "CMP001,2025-01-01,12000,300,45.50,12");
        assertTrue(row.isPresent());
        assertEquals("CMP001", row.get().campaignId());
        assertEquals(12_000L, row.get().impressions());
        assertEquals(300L, row.get().clicks());
        assertEquals(45.50, row.get().spend(), 0.0);
        assertEquals(12L, row.get().conversions());
    }

    @Test
    void parseLine_rejects_wrong_column_count() {
        assertTrue(CsvRowParser.parseLine("a,b,c").isEmpty());
    }

    @Test
    void parseLine_rejects_negative_numbers() {
        assertTrue(CsvRowParser.parseLine("CMP001,2025-01-01,-1,0,0,0").isEmpty());
    }

    @Test
    void parseLine_rejects_bad_number() {
        assertTrue(CsvRowParser.parseLine("CMP001,2025-01-01,10,x,1.0,1").isEmpty());
    }

    @Test
    void parseLine_trims_fields() {
        Optional<CsvRowParser.ParsedRow> row = CsvRowParser.parseLine(
                " CMP001 ,2025-01-01, 10 , 2 , 3.5 , 1 ");
        assertTrue(row.isPresent());
        assertEquals("CMP001", row.get().campaignId());
        assertEquals(10L, row.get().impressions());
    }
}
