package com.fduenasc.infrastructure.drivenadapters.freemarker;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Locale;
import java.util.Set;

/**
 * Helpers for locating FreeMarker markup tags and unclosed directives.
 *
 * @author Francisco Dueñas
 * @since 0.4.1
 */
final class FreemarkerTagSupport {

    /**
     * Empty location when no unclosed open tag can be resolved.
     */
    private static final int[] NO_LOCATION = new int[0];

    /**
     * Block {@code #} directives that require a matching end-tag.
     */
    private static final Set<String> BLOCK_HASH_DIRECTIVES = Set.of(
            "if", "list", "items", "sep", "foreach", "switch", "macro", "function",
            "attempt", "compress", "escape", "noescape", "transform", "autoesc", "noautoesc");

    private FreemarkerTagSupport() {
    }

    /**
     * When FreeMarker reports an unclosed {@code @} / {@code #} at EOF, returns the
     * 1-based line/column of the innermost unmatched opening tag.
     *
     * @param source    template source.
     * @param editorMsg FreeMarker editor message.
     * @return {@code int[]{line, column}}, or an empty array if not found.
     */
    static int[] findUnclosedOpenLocation(String source, String editorMsg) {
        if (source == null || source.isEmpty() || editorMsg == null) {
            return NO_LOCATION;
        }
        String msg = editorMsg.toLowerCase(Locale.ROOT);
        if (!msg.contains("unclosed")) {
            return NO_LOCATION;
        }
        int openIndex = resolveUnclosedOpenIndex(source, msg);
        if (openIndex < 0) {
            return NO_LOCATION;
        }
        return indexToLineColumn(source, openIndex);
    }

    /**
     * Resolves the innermost unclosed open index.
     *
     * @param source the source.
     * @param msg    the message.
     * @return the innermost unclosed open index.
     */
    private static int resolveUnclosedOpenIndex(String source, String msg) {
        if (msg.contains("unclosed @")) {
            return findInnermostUnclosedAt(source);
        }
        if (msg.contains("unclosed #")) {
            return findInnermostUnclosedHash(source);
        }
        int atIndex = findInnermostUnclosedAt(source);
        return atIndex >= 0 ? atIndex : findInnermostUnclosedHash(source);
    }

    /**
     * Finds the innermost unclosed {@code @} open index.
     *
     * @param source the source.
     * @return the innermost unclosed {@code @} open index.
     */
    private static int findInnermostUnclosedAt(String source) {
        Deque<Integer> openIndexes = new ArrayDeque<>();
        int index = 0;
        while (index < source.length()) {
            index = advanceAtScan(source, openIndexes, index);
        }
        return openIndexes.isEmpty() ? -1 : openIndexes.peek();
    }

    /**
     * Advances the scan for the innermost unclosed {@code @} open index.
     *
     * @param source the source.
     * @param openIndexes the open indexes.
     * @param index the index.
     * @return the advanced index.
     */
    private static int advanceAtScan(String source, Deque<Integer> openIndexes, int index) {
        if (source.startsWith("</@", index)) {
            return advanceMatchingClose(source, openIndexes, index);
        }
        if (source.startsWith("<@", index)) {
            return advanceOpenAt(source, openIndexes, index);
        }
        return index + 1;
    }

    /**
     * Advances the scan for the innermost unclosed {@code @} open index.
     *
     * @param source the source.
     * @param openIndexes the open indexes.
     * @param index the index.
     * @return the advanced index.
     */
    private static int advanceOpenAt(String source, Deque<Integer> openIndexes, int index) {
        int tagEnd = findTagEnd(source, index);
        if (tagEnd < 0) {
            return source.length();
        }
        if (requiresEndTag(source, tagEnd)) {
            openIndexes.push(index);
        }
        return tagEnd + 1;
    }

    /**
     * Finds the innermost unclosed {@code #} open index.
     *
     * @param source the source.
     * @return the innermost unclosed {@code #} open index.
     */
    private static int findInnermostUnclosedHash(String source) {
        Deque<Integer> openIndexes = new ArrayDeque<>();
        int index = 0;
        while (index < source.length()) {
            index = advanceHashScan(source, openIndexes, index);
        }
        return openIndexes.isEmpty() ? -1 : openIndexes.peek();
    }

    /**
     * Advances the scan for the innermost unclosed {@code #} open index.
     *
     * @param source the source.
     * @param openIndexes the open indexes.
     * @param index the index.
     * @return the advanced index.
     */
    private static int advanceHashScan(String source, Deque<Integer> openIndexes, int index) {
        if (source.startsWith("<#--", index)) {
            int close = source.indexOf("-->", index + 4);
            return close < 0 ? source.length() : close + 3;
        }
        if (source.startsWith("</#", index)) {
            return advanceMatchingClose(source, openIndexes, index);
        }
        if (source.startsWith("<#", index)) {
            return advanceOpenHash(source, openIndexes, index);
        }
        return index + 1;
    }

    /**
     * Advances the scan for the innermost unclosed {@code #} open index.
     *
     * @param source the source.
     * @param openIndexes the open indexes.
     * @param index the index.
     * @return the advanced index.
     */
    private static int advanceOpenHash(String source, Deque<Integer> openIndexes, int index) {
        int tagEnd = findTagEnd(source, index);
        if (tagEnd < 0) {
            return source.length();
        }
        String name = hashDirectiveName(source, index);
        if (name != null && BLOCK_HASH_DIRECTIVES.contains(name) && requiresEndTag(source, tagEnd)) {
            openIndexes.push(index);
        }
        return tagEnd + 1;
    }

    /**
     * Advances the scan for the innermost unclosed {@code #} open index.
     *
     * @param source the source.
     * @param openIndexes the open indexes.
     * @param index the index.
     * @return the advanced index.
     */
    private static int advanceMatchingClose(String source, Deque<Integer> openIndexes, int index) {
        int tagEnd = findTagEnd(source, index);
        if (tagEnd < 0) {
            return source.length();
        }
        if (!openIndexes.isEmpty()) {
            openIndexes.pop();
        }
        return tagEnd + 1;
    }

    /**
     * Finds the directive name of a FreeMarker {@code #} open tag.
     *
     * @param source the source.
     * @param tagStart the tag start.
     * @return the directive name.
     */
    private static String hashDirectiveName(String source, int tagStart) {
        int nameStart = tagStart + 2;
        int nameEnd = nameStart;
        while (nameEnd < source.length() && isDirectiveNameChar(source.charAt(nameEnd))) {
            nameEnd++;
        }
        if (nameEnd == nameStart) {
            return null;
        }
        return source.substring(nameStart, nameEnd).toLowerCase(Locale.ROOT);
    }

    /**
     * Checks if a character is a valid directive name character.
     *
     * @param current the character.
     * @return {@code true} if the character is a valid directive name character.
     */
    private static boolean isDirectiveNameChar(char current) {
        return Character.isLetterOrDigit(current) || current == '_';
    }

    /**
     * Checks if a FreeMarker tag requires an end tag.
     *
     * @param source the source.
     * @param tagEnd the tag end.
     * @return {@code true} if the tag requires an end tag.
     */
    private static boolean requiresEndTag(String source, int tagEnd) {
        return tagEnd <= 0 || source.charAt(tagEnd - 1) != '/';
    }

    /**
     * Finds the closing {@code >} of a FreeMarker tag, ignoring {@code >} nested in
     * parentheses or quoted strings.
     *
     * @param template the template.
     * @param tagStart index of {@code <} starting the tag.
     * @return index of the closing {@code >}, or {@code -1} if not found.
     */
    static int findTagEnd(String template, int tagStart) {
        int parenDepth = 0;
        boolean inSingleQuote = false;
        boolean inDoubleQuote = false;
        for (int i = tagStart + 1; i < template.length(); i++) {
            char current = template.charAt(i);
            if (inSingleQuote) {
                inSingleQuote = remainsInsideQuote(template, i, '\'');
            } else if (inDoubleQuote) {
                inDoubleQuote = remainsInsideQuote(template, i, '"');
            } else if (current == '\'') {
                inSingleQuote = true;
            } else if (current == '"') {
                inDoubleQuote = true;
            } else {
                parenDepth = adjustParenDepth(parenDepth, current);
                if (current == '>' && parenDepth == 0) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * Checks if a character remains inside a quote.
     *
     * @param template the template.
     * @param index the index.
     * @param quote the quote.
     * @return {@code true} if the character remains inside a quote.
     */
    private static boolean remainsInsideQuote(String template, int index, char quote) {
        return template.charAt(index) != quote || template.charAt(index - 1) == '\\';
    }

    /**
     * Adjusts the parenthesized depth.
     *
     * @param parenDepth the parenthesized depth.
     * @param current the current character.
     * @return the adjusted parenthesized depth.
     */
    private static int adjustParenDepth(int parenDepth, char current) {
        if (current == '(') {
            return parenDepth + 1;
        }
        if (current == ')' && parenDepth > 0) {
            return parenDepth - 1;
        }
        return parenDepth;
    }

    /**
     * Converts an index to a line/column.
     *
     * @param source the source.
     * @param index the index.
     * @return the line/column.
     */
    static int[] indexToLineColumn(String source, int index) {
        int line = 1;
        int column = 1;
        int last = Math.clamp(index, 0, source.length());
        for (int i = 0; i < last; i++) {
            if (source.charAt(i) == '\n') {
                line++;
                column = 1;
            } else {
                column++;
            }
        }
        return new int[]{line, column};
    }
}
