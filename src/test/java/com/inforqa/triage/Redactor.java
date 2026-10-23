package com.inforqa.triage;

import java.util.regex.Pattern;

/**
 * Removes anything sensitive-looking from text before it is written to the AI-ready prompt file.
 * Rule of thumb: the prompt may contain test names, failing steps and short error messages, and nothing else.
 */
public final class Redactor {

    private static final Pattern SECRET = Pattern.compile(
        "(?i)(token|password|passwd|secret|api[_-]?key|authorization|cookie|bearer)(\\s*[=:]\\s*|\\s+)([^\\s,;\"']+)");
    private static final Pattern EMAIL = Pattern.compile("[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+");
    private static final Pattern IP_ADDRESS = Pattern.compile("\\b\\d{1,3}(\\.\\d{1,3}){3}\\b");
    // Long strings containing a digit look like tokens or IDs. Plain long words (class names) are kept.
    private static final Pattern LONG_ID = Pattern.compile("\\b(?=[A-Za-z_-]*\\d)[A-Za-z0-9_-]{24,}\\b");

    private Redactor() {
    }

    public static String redact(String text, int maxLength) {
        if (text == null) {
            return "";
        }
        String result = text.replaceAll("\\s+", " ").trim();
        result = SECRET.matcher(result).replaceAll("$1=[REDACTED]");
        result = EMAIL.matcher(result).replaceAll("[EMAIL]");
        result = IP_ADDRESS.matcher(result).replaceAll("[IP]");
        result = LONG_ID.matcher(result).replaceAll("[LONG-ID]");
        if (result.length() > maxLength) {
            result = result.substring(0, maxLength) + "...";
        }
        return result;
    }
}
