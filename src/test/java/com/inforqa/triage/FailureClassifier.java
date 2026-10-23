package com.inforqa.triage;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Rule-based failure triage. It reads the error a test produced and decides which category it most
 * likely belongs to. The rules run in order and the first match wins. The result is a SUGGESTION:
 * a person should confirm it. This is rule-based, not AI.
 */
public final class FailureClassifier {

    /** The decision plus the reason, so the report can explain itself. */
    public record Result(FailureCategory category, String reason) {
    }

    private static final Pattern HTTP_STATUS = Pattern.compile("(?is)HTTP status.*?but was:\\s*(\\d{3})");

    private static final Pattern ENVIRONMENT = Pattern.compile(
        "(?i)session not created|SessionNotCreated|cannot find chrome binary|UnknownHost|ConnectException"
            + "|connection refused|sslhandshake|ERR_NAME_NOT_RESOLVED|ERR_CONNECTION|ERR_INTERNET_DISCONNECTED"
            + "|bad gateway|service unavailable|gateway time-?out|no such host|chrome not reachable"
            + "|not connected to devtools");

    private static final Pattern FLAKY = Pattern.compile(
        "(?i)TimeoutException|SocketTimeout|ReadTimeout|timed out|StaleElementReference"
            + "|ElementClickIntercepted|ElementNotInteractable");

    private static final Pattern TEST_SCRIPT = Pattern.compile(
        "(?i)NoSuchElementException|NullPointerException|IllegalArgumentException|IllegalStateException"
            + "|ClassCastException|UndefinedStepException|PendingException|InvalidSelectorException");

    private FailureClassifier() {
    }

    public static Result classify(Throwable error) {
        String text = describe(error);

        Matcher status = HTTP_STATUS.matcher(text);
        if (status.find()) {
            return fromHttpStatus(Integer.parseInt(status.group(1)));
        }
        if (ENVIRONMENT.matcher(text).find()) {
            return new Result(FailureCategory.ENVIRONMENT,
                "The error mentions a connection, network or browser start-up problem.");
        }
        if (FLAKY.matcher(text).find()) {
            return new Result(FailureCategory.FLAKY,
                "The error is a timeout or timing-related problem that often passes on a re-run.");
        }
        if (TEST_SCRIPT.matcher(text).find()) {
            return new Result(FailureCategory.TEST_SCRIPT,
                "The error points to the test code itself (missing element, missing value or undefined step).");
        }
        if (error instanceof AssertionError) {
            return new Result(FailureCategory.PRODUCT_DEFECT,
                "A check on the page or response did not match what was expected.");
        }
        return new Result(FailureCategory.NEEDS_REVIEW, "No rule matched this error.");
    }

    private static Result fromHttpStatus(int code) {
        if (code == 502 || code == 503 || code == 504) {
            return new Result(FailureCategory.ENVIRONMENT,
                "The service or gateway was unavailable (HTTP " + code + ").");
        }
        if (code == 429) {
            return new Result(FailureCategory.FLAKY, "The service rate-limited the request (HTTP 429).");
        }
        if (code >= 500) {
            return new Result(FailureCategory.PRODUCT_DEFECT, "The service returned a server error (HTTP " + code + ").");
        }
        if (code == 400 || code == 401 || code == 403 || code == 404 || code == 409 || code == 422) {
            return new Result(FailureCategory.TEST_DATA,
                "The service rejected the request or could not find the data it used (HTTP " + code + ").");
        }
        if (code == 405 || code == 406 || code == 415 || code == 418) {
            return new Result(FailureCategory.TEST_SCRIPT,
                "The service did not accept the request format or method (HTTP " + code + ").");
        }
        if (code < 400) {
            return new Result(FailureCategory.PRODUCT_DEFECT,
                "The service answered, but with a different success code than expected (HTTP " + code + ").");
        }
        return new Result(FailureCategory.NEEDS_REVIEW, "Unexpected HTTP status " + code + ".");
    }

    /** Error type and message, including the chain of underlying causes. */
    private static String describe(Throwable error) {
        StringBuilder text = new StringBuilder();
        Throwable current = error;
        int depth = 0;
        while (current != null && depth < 6) {
            text.append(current.getClass().getName()).append(' ').append(current.getMessage()).append(' ');
            current = current.getCause();
            depth++;
        }
        return text.toString();
    }
}
