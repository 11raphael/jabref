package org.jabref.logic.util;

import org.jabref.logic.util.io.FileUtil;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * This class is based on http://stackoverflow.com/a/5626340/873282
 * extended with LEFT CURLY BRACE and RIGHT CURLY BRACE
 * Replaces illegal characters in given file paths and removes LaTeX commands.
 *
 * Regarding the maximum length, see {@link FileUtil#getValidFileName(String)}
 */
class FileNameCleaner {

    private FileNameCleaner() {
    }

    /**
     * Removes LaTeX commands such as \textbf{...}, \emph{...}, \mkbibquote{...}
     * Also removes standalone LaTeX commands like \textbf and \emph.
     * Finally, it removes any remaining curly braces.
     *
     * @param input the string to clean
     * @return a string with LaTeX commands removed
     */
    static String stripLatexCommands(String input) {
        if (input == null) {
            return "";
        }

        // Remove LaTeX commands with curly braces, e.g., \textbf{Hello}
        String noCommands = input.replaceAll("\\\\[a-zA-Z]+\\{([^}]*)\\}", "$1");

        // Remove standalone LaTeX commands like \textbf, \emph
        noCommands = noCommands.replaceAll("\\\\[a-zA-Z]+", "");

        // Remove remaining curly braces
        noCommands = noCommands.replaceAll("[{}]", "");

        return noCommands.trim();
    }

    /**
     * Replaces illegal characters in given fileName by '_'
     * and removes LaTeX commands before sanitization.
     *
     * @param badFileName the fileName to clean
     * @return a clean filename
     */
    public static String cleanFileName(String badFileName) {
        if (badFileName == null) {
            return "";
        }

        badFileName = stripLatexCommands(badFileName); // Remove LaTeX commands

        StringBuilder cleanName = new StringBuilder(badFileName.length());
        for (int i = 0; i < badFileName.length(); i++) {
            char c = badFileName.charAt(i);
            if (FileUtil.isCharLegal(c) && (c != '/') && (c != '\\')) {
                cleanName.append(c);
            } else {
                cleanName.append('_');
            }
        }
        return cleanName.toString().trim();
    }

    /**
     * Replaces illegal characters in given directoryName by '_'.
     * Directory name may contain directory separators, e.g. 'deep/in/a/tree'; these are left untouched.
     * Also removes LaTeX commands before sanitization.
     *
     * @param badFileName the fileName to clean
     * @return a clean filename
     */
    public static String cleanDirectoryName(String badFileName) {
        if (badFileName == null) {
            return "";
        }

        badFileName = stripLatexCommands(badFileName); // Remove LaTeX commands

        StringBuilder cleanName = new StringBuilder(badFileName.length());
        for (int i = 0; i < badFileName.length(); i++) {
            char c = badFileName.charAt(i);
            if (FileUtil.isCharLegal(c)) {
                cleanName.append(c);
            } else {
                cleanName.append('_');
            }
        }
        return cleanName.toString().trim();
    }
}

class FileNameCleanerTests {

    @Test
    void stripLatexCommandsTest() {
        // Test case from the issue: \mkbibquote{Community}
        assertEquals("Building Community",
                FileNameCleaner.stripLatexCommands("Building \\mkbibquote{Community}"));

        // Test other common LaTeX commands
        assertEquals("Bold text",
                FileNameCleaner.stripLatexCommands("\\textbf{Bold} text"));
        assertEquals("Emphasized text",
                FileNameCleaner.stripLatexCommands("\\emph{Emphasized} text"));

        // Test nested commands
        assertEquals("Nested content",
                FileNameCleaner.stripLatexCommands("\\textbf{\\emph{Nested} content}"));

        // Test multiple commands in one string
        assertEquals("Multiple commands in text",
                FileNameCleaner.stripLatexCommands("\\textbf{Multiple} commands in \\emph{text}"));

        // Test standalone commands without braces
        assertEquals("text",
                FileNameCleaner.stripLatexCommands("\\LaTeX text"));

        // Test curly braces without commands (should be removed)
        assertEquals("Plain text with content",
                FileNameCleaner.stripLatexCommands("Plain text with {content}"));

        // Test command with empty content
        assertEquals("Empty content",
                FileNameCleaner.stripLatexCommands("Empty content \\textbf{}"));

        // Test null input
        assertEquals("", FileNameCleaner.stripLatexCommands(null));

        // Test empty string
        assertEquals("", FileNameCleaner.stripLatexCommands(""));

        // Test whitespace handling
        assertEquals("Spaces should be preserved",
                FileNameCleaner.stripLatexCommands("Spaces should be preserved"));

        // Test the exact example from the issue
        assertEquals("Building Community",
                FileNameCleaner.stripLatexCommands("Building \\mkbibquote{Community}"));
    }
}
