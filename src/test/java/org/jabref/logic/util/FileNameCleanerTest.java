package org.jabref.logic.util.io;

/**
 * This class is based on http://stackoverflow.com/a/5626340/873282
 * extended with LEFT CURLY BRACE and RIGHT CURLY BRACE
 * Replaces illegal characters in given file paths and removes LaTeX commands.
 *
 * Regarding the maximum length, see {@link FileUtil#getValidFileName(String)}
 */
public class FileNameCleaner {

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
    private static String stripLatexCommands(String input) {
        if (input == null) return "";

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
        if (badFileName == null) return "";

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
        if (badFileName == null) return "";

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
