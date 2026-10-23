package com.inforqa.triage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/** Writes the two triage files into target/failure-triage. */
public final class TriageReport {

    private static final Path FOLDER = Path.of("target", "failure-triage");

    private TriageReport() {
    }

    public static void write(List<FailureRecord> failures, int total, int passed) throws IOException {
        List<FailureRecord> sorted = failures.stream()
            .sorted(Comparator.comparing(FailureRecord::category).thenComparing(FailureRecord::scenario))
            .toList();
        Files.createDirectories(FOLDER);
        Files.writeString(FOLDER.resolve("triage-report.md"), buildReport(sorted, total, passed));
        Files.writeString(FOLDER.resolve("ai-prompt.md"), buildAiPrompt(sorted));
    }

    private static String buildReport(List<FailureRecord> failures, int total, int passed) {
        StringBuilder md = new StringBuilder();
        md.append("## Failure triage report\n\n");
        md.append("Generated: ").append(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)).append("\n\n");
        md.append("**Scenarios:** ").append(total).append(" run, ")
            .append(passed).append(" passed, ").append(failures.size()).append(" failed\n\n");

        if (failures.isEmpty()) {
            md.append("No failures to triage. All scenarios passed.\n");
            return md.toString();
        }

        Map<FailureCategory, Integer> counts = new EnumMap<>(FailureCategory.class);
        for (FailureRecord f : failures) {
            counts.merge(f.category(), 1, Integer::sum);
        }
        md.append("### Summary by category\n\n| Category | Failures |\n|---|---|\n");
        counts.forEach((category, count) ->
            md.append("| ").append(category.label()).append(" | ").append(count).append(" |\n"));

        md.append("\n### Failures\n\n");
        md.append("| Scenario | Failing step | Category | Why | Suggested next step |\n|---|---|---|---|---|\n");
        for (FailureRecord f : failures) {
            md.append("| ").append(cell(f.scenario()))
                .append(" | ").append(cell(f.step()))
                .append(" | ").append(cell(f.category().label()))
                .append(" | ").append(cell(f.reason()))
                .append(" | ").append(cell(f.category().nextStep()))
                .append(" |\n");
        }

        md.append("\n### Error excerpts (redacted)\n\n");
        for (FailureRecord f : failures) {
            md.append("- **").append(f.scenario()).append("** (").append(f.location()).append("): ")
                .append(f.error()).append("\n");
        }
        md.append("\n*The categories are suggestions from simple rules. A person should confirm each one.*\n");
        return md.toString();
    }

    private static String buildAiPrompt(List<FailureRecord> failures) {
        StringBuilder md = new StringBuilder();
        md.append("# AI-assisted triage prompt (human in the loop)\n\n");
        md.append("**Before you use this:** read it, and only paste it into an AI assistant your organisation allows. ");
        md.append("Sensitive-looking values (tokens, passwords, emails, IP addresses, long IDs) were removed automatically, ");
        md.append("but you are responsible for checking. Never add screenshots, cookies, request bodies or real customer data.\n\n");
        md.append("---\n\n");
        md.append("You are helping a QA engineer triage failed automated tests.\n");
        md.append("For each failure below: (1) explain the most likely cause in plain English, ");
        md.append("(2) say whether you agree with the suggested category and why, ");
        md.append("(3) suggest one concrete next step. Use only the information given. ");
        md.append("If the information is not enough, say what extra evidence you would need. Do not guess.\n\n");
        if (failures.isEmpty()) {
            md.append("There are no failures in this run.\n");
            return md.toString();
        }
        int number = 1;
        for (FailureRecord f : failures) {
            md.append("### Failure ").append(number++).append("\n");
            md.append("- Scenario: ").append(f.scenario()).append("\n");
            md.append("- Failing step: ").append(f.step()).append("\n");
            md.append("- Suggested category: ").append(f.category().label()).append("\n");
            md.append("- Reason for suggestion: ").append(f.reason()).append("\n");
            md.append("- Error (redacted): ").append(f.error()).append("\n\n");
        }
        return md.toString();
    }

    private static String cell(String text) {
        return text == null ? "" : text.replace("|", "\\|").replace("\n", " ");
    }
}
