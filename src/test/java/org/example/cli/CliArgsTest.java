package org.example.cli;

import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CliArgsTest {

    @Test
    void parse_uses_defaults() {
        CliArgs cli = CliArgs.parse(new String[] {});
        assertEquals(Paths.get("data/ad_data.csv"), cli.inputFile());
        assertEquals(Paths.get("output"), cli.outputDir());
    }

    @Test
    void parse_reads_flags() {
        CliArgs cli = CliArgs.parse(new String[] {
                "--input", "/tmp/in.csv",
                "--output", "/tmp/out"
        });
        assertEquals(Paths.get("/tmp/in.csv"), cli.inputFile());
        assertEquals(Paths.get("/tmp/out"), cli.outputDir());
    }

    @Test
    void parse_ignores_unknown_flags() {
        CliArgs cli = CliArgs.parse(new String[] { "--verbose", "--input", "x.csv" });
        assertEquals(Paths.get("x.csv"), cli.inputFile());
        assertEquals(Paths.get("output"), cli.outputDir());
    }
}
