package com.fduenasc.domain.usecase;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fduenasc.domain.model.JsonSyntaxCheck;
import com.fduenasc.domain.model.MessageKeys;
import com.fduenasc.domain.usecase.exception.DataModelException;
import com.fduenasc.domain.usecase.exception.InvalidJsonException;
import com.fduenasc.domain.usecase.exception.TemplateProcessingException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Validates, formats, and processes FreeMarker templates with a JSON data model.
 *
 * @author Francisco Dueñas
 * @since 0.1.0
 */
public class TemplateValidator {

    private final TemplateProcessor templateProcessor;
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final Pattern ASSIGN_MAP = Pattern.compile("(<#assign\\s+\\w+\\s*=\\s*)\\{([^}]*)}");
    private static final Pattern BRACE_BLOCK = Pattern.compile("\\{([^}]*)}");
    private static final Pattern COLON_WHITESPACE = Pattern.compile("\\s*:\\s*");
    private static final Pattern EDGE_WHITESPACE = Pattern.compile("(^\\s+)|(\\s+$)");
    /**
     * Starts of FreeMarker markup tags that Format Document may split onto their own line.
     */
    private static final String[] FREEMARKER_TAG_STARTS = {"</#", "<#", "</@", "<@"};

    public TemplateValidator(TemplateProcessor templateProcessor) {
        this.templateProcessor = templateProcessor;
    }

    public String processTemplate(String templateContent, Map<String, Object> dataModel) throws TemplateProcessingException {
        return templateProcessor.processTemplate(templateContent, dataModel);
    }

    public static List<String> validateFieldsPresentWithTypes(String jsonOutput, String[] expectedFields) throws InvalidJsonException {
        List<String> missing = new ArrayList<>(expectedFields.length);
        JsonNode jsonNode;
        try {
            jsonNode = MAPPER.readTree(jsonOutput);
        } catch (JsonProcessingException e) {
            throw new InvalidJsonException(e.getOriginalMessage() != null ? e.getOriginalMessage() : e.getMessage(), e);
        }
        for (String field : expectedFields) {
            if (field.isEmpty()) continue;
            String[] parts = field.split(":", 2);
            String fieldPath = parts[0];
            String expectedType = parts.length > 1 ? parts[1].toLowerCase() : null;
            JsonNode valueNode = getNestedField(jsonNode, fieldPath);
            if (valueNode == null || valueNode.isMissingNode()) {
                missing.add(field);
            } else if (expectedType != null && !matchesType(valueNode, expectedType)) {
                missing.add(field + " (Type mismatch)");
            }
        }
        return missing;
    }

    private static JsonNode getNestedField(JsonNode node, String fieldPath) {
        String[] parts = fieldPath.split("\\.");
        JsonNode current = node;
        for (String part : parts) {
            if (current == null || !current.has(part)) {
                return null;
            }
            current = current.get(part);
        }
        return current;
    }

    private static boolean matchesType(JsonNode node, String type) {
        return switch (type) {
            case "string" -> node.isTextual();
            case "number" -> node.isNumber();
            case "boolean" -> node.isBoolean();
            case "object" -> node.isObject();
            case "array" -> node.isArray();
            case "null" -> node.isNull();
            default -> false;
        };
    }

    public static Map<String, Object> parseJsonToDataModel(String json) throws DataModelException {
        try {
            return MAPPER.readValue(json, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            throw new DataModelException(
                    e.getOriginalMessage() != null ? e.getOriginalMessage() : e.getMessage(),
                    e);
        }
    }

    public static JsonSyntaxCheck checkDataModelJsonSyntax(String json) {
        String s = json == null ? "" : json.trim();
        if (s.isEmpty()) {
            return new JsonSyntaxCheck(true, "", -1, -1);
        }
        try {
            JsonNode root = MAPPER.readTree(s);
            if (root.isObject()) {
                return new JsonSyntaxCheck(true, "", -1, -1);
            }
            if (root.isNull()) {
                return new JsonSyntaxCheck(true, MessageKeys.JSON_DATA_MODEL_NULL_ROOT, -1, -1);
            }
            return new JsonSyntaxCheck(true, MessageKeys.JSON_DATA_MODEL_NEED_OBJECT, -1, -1);
        } catch (JsonProcessingException e) {
            JsonLocation loc = e.getLocation();
            int line = loc != null ? loc.getLineNr() : -1;
            int col = loc != null ? loc.getColumnNr() : -1;
            String msg = e.getOriginalMessage() != null ? e.getOriginalMessage() : e.getMessage();
            return new JsonSyntaxCheck(false, msg != null ? msg : MessageKeys.JSON_PARSE_FALLBACK, line, col);
        }
    }

    public static String formatFreemarkerTemplateCombined(String template) {
        String formatted = insertNewlinesAfterFreemarkerTags(template == null ? "" : template);

        StringBuilder sb = new StringBuilder();
        Matcher matcher = ASSIGN_MAP.matcher(formatted);
        int lastEnd = 0;
        while (matcher.find()) {
            sb.append(formatted, lastEnd, matcher.start());
            String before = matcher.group(1);
            String mapBody = matcher.group(2).trim();
            String[] entries = splitCommaSeparated(mapBody);
            StringBuilder mapFormatted = new StringBuilder("{\n");
            for (int i = 0; i < entries.length; i++) {
                mapFormatted.append("    ").append(entries[i]);
                if (i < entries.length - 1) {
                    mapFormatted.append(",");
                }
                mapFormatted.append("\n");
            }
            mapFormatted.append("}");
            sb.append(before).append(mapFormatted);
            lastEnd = matcher.end();
        }
        sb.append(formatted.substring(lastEnd));
        return sb.toString().replaceAll("[\\n\\r]+", "\n").replaceAll("\\n{2,}", "\n").trim();
    }

    /**
     * Inserts a newline after each complete FreeMarker tag without treating {@code >}
     * inside comparisons (e.g. {@code (score > 100)}) as the tag terminator.
     *
     * @param template the raw template.
     * @return the template with newlines after FreeMarker tags.
     */
    private static String insertNewlinesAfterFreemarkerTags(String template) {
        StringBuilder out = new StringBuilder(template.length() + 16);
        int index = 0;
        while (index < template.length()) {
            int tagStart = indexOfFreemarkerTag(template, index);
            if (tagStart < 0) {
                out.append(template, index, template.length());
                index = template.length();
            } else {
                out.append(template, index, tagStart);
                int tagEnd = findFreemarkerTagEnd(template, tagStart);
                if (tagEnd < 0) {
                    out.append(template, tagStart, template.length());
                    index = template.length();
                } else {
                    out.append(template, tagStart, tagEnd + 1);
                    if (tagEnd + 1 >= template.length() || template.charAt(tagEnd + 1) != '\n') {
                        out.append('\n');
                    }
                    index = tagEnd + 1;
                }
            }
        }
        return out.toString();
    }

    private static int indexOfFreemarkerTag(String template, int fromIndex) {
        int best = -1;
        for (String start : FREEMARKER_TAG_STARTS) {
            int at = template.indexOf(start, fromIndex);
            if (at >= 0 && (best < 0 || at < best)) {
                best = at;
            }
        }
        return best;
    }

    /**
     * Finds the closing {@code >} of a FreeMarker tag, ignoring {@code >} nested in
     * parentheses or quoted strings.
     *
     * @param template the template.
     * @param tagStart index of {@code <} starting the tag.
     * @return index of the closing {@code >}, or {@code -1} if not found.
     */
    private static int findFreemarkerTagEnd(String template, int tagStart) {
        int parenDepth = 0;
        boolean inSingleQuote = false;
        boolean inDoubleQuote = false;
        for (int i = tagStart + 1; i < template.length(); i++) {
            char current = template.charAt(i);
            if (inSingleQuote) {
                inSingleQuote = !isUnescapedQuoteAt(template, i, '\'');
            } else if (inDoubleQuote) {
                inDoubleQuote = !isUnescapedQuoteAt(template, i, '"');
            } else if (current == '\'') {
                inSingleQuote = true;
            } else if (current == '"') {
                inDoubleQuote = true;
            } else {
                parenDepth = adjustParenDepth(parenDepth, current);
                if (isFreemarkerTagTerminator(current, parenDepth)) {
                    return i;
                }
            }
        }
        return -1;
    }

    private static boolean isUnescapedQuoteAt(String template, int index, char quote) {
        return template.charAt(index) == quote && template.charAt(index - 1) != '\\';
    }

    private static int adjustParenDepth(int parenDepth, char current) {
        if (current == '(') {
            return parenDepth + 1;
        }
        if (current == ')' && parenDepth > 0) {
            return parenDepth - 1;
        }
        return parenDepth;
    }

    private static boolean isFreemarkerTagTerminator(char current, int parenDepth) {
        return current == '>' && parenDepth == 0;
    }

    public static String toSingleLine(String template) {
        if (template == null) return "";
        String noSpacesBetweenTags = template.replaceAll(">\\s+<", "><");

        StringBuilder result = new StringBuilder();
        Matcher matcher = BRACE_BLOCK.matcher(noSpacesBetweenTags);
        int lastEnd = 0;
        while (matcher.find()) {
            result.append(noSpacesBetweenTags, lastEnd, matcher.start());
            String content = EDGE_WHITESPACE.matcher(
                    COLON_WHITESPACE.matcher(stripAround(matcher.group(1))).replaceAll(":")
            ).replaceAll("");
            result.append("{").append(content).append("}");
            lastEnd = matcher.end();
        }
        result.append(noSpacesBetweenTags.substring(lastEnd));

        return result.toString().replaceAll("[\\r\\n]+", " ").replaceAll("\\s{2,}", " ").trim();
    }

    private static String[] splitCommaSeparated(String value) {
        List<String> parts = new ArrayList<>();
        int start = 0;
        for (int i = 0; i < value.length(); i++) {
            if (value.charAt(i) == ',') {
                parts.add(value.substring(start, i).strip());
                start = i + 1;
            }
        }
        String tail = value.substring(start).strip();
        if (!tail.isEmpty() || parts.isEmpty()) {
            parts.add(tail);
        }
        return parts.toArray(String[]::new);
    }

    private static String stripAround(String value) {
        StringBuilder compacted = new StringBuilder(value.length());
        boolean skipFollowingWhitespace = false;
        for (int i = 0; i < value.length(); i++) {
            char current = value.charAt(i);
            if (current == ',') {
                int length = compacted.length();
                while (length > 0 && Character.isWhitespace(compacted.charAt(length - 1))) {
                    length--;
                }
                compacted.setLength(length);
                compacted.append(',');
                skipFollowingWhitespace = true;
            } else if (!skipFollowingWhitespace || !Character.isWhitespace(current)) {
                skipFollowingWhitespace = false;
                compacted.append(current);
            }
        }
        return compacted.toString();
    }
}
