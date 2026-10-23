package com.inforqa.triage;

/** The kinds of failure the triage can recognise, with a plain-English next step for each. */
public enum FailureCategory {
    PRODUCT_DEFECT("Suspected product defect",
        "Reproduce it manually, then raise or review a defect."),
    TEST_DATA("Test data or configuration",
        "Check the data, IDs, credentials or settings this test relies on."),
    ENVIRONMENT("Environment issue",
        "Check the site, network, browser or service status, then re-run."),
    FLAKY("Suspected flaky test",
        "Re-run once. If it passes, improve the waits or the test's stability."),
    TEST_SCRIPT("Test script issue",
        "Fix the test code, the element locator or the request format."),
    NEEDS_REVIEW("Needs human review",
        "No rule matched. Investigate manually.");

    private final String label;
    private final String nextStep;

    FailureCategory(String label, String nextStep) {
        this.label = label;
        this.nextStep = nextStep;
    }

    public String label() {
        return label;
    }

    public String nextStep() {
        return nextStep;
    }
}
