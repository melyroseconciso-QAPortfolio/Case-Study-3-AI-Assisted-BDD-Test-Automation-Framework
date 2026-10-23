package com.inforqa.triage;

/** One failed scenario, with the triage decision. The error text is already redacted. */
public record FailureRecord(
    String scenario,
    String location,
    String step,
    FailureCategory category,
    String reason,
    String error) {
}
