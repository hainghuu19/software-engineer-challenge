package org.example.cli;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

/**
 * Parsed CLI options: {@code --input} and {@code --output} with sensible defaults.
 */
public final class CliArgs {

    private static final String DEFAULT_INPUT = "data/ad_data.csv";
    private static final String DEFAULT_OUTPUT_DIR = "output";

    private final Path inputFile;
    private final Path outputDir;

    private CliArgs(Path inputFile, Path outputDir) {
        this.inputFile = Objects.requireNonNull(inputFile);
        this.outputDir = Objects.requireNonNull(outputDir);
    }

    public Path inputFile() {
        return inputFile;
    }

    public Path outputDir() {
        return outputDir;
    }

    /**
     * Parses {@code --input <path>} and {@code --output <dir>}. Unknown flags are ignored.
     * Relative paths are resolved against the current working directory.
     */
    public static CliArgs parse(String[] args) {
        String input = DEFAULT_INPUT;
        String output = DEFAULT_OUTPUT_DIR;

        for (int i = 0; i < args.length; i++) {
            if ("--input".equals(args[i]) && i + 1 < args.length) {
                input = args[++i];
            } else if ("--output".equals(args[i]) && i + 1 < args.length) {
                output = args[++i];
            }
        }

        return new CliArgs(Paths.get(input), Paths.get(output));
    }
}
