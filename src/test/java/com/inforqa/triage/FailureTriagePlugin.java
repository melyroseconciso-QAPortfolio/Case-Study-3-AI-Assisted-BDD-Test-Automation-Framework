package com.inforqa.triage;

import io.cucumber.plugin.ConcurrentEventListener;
import io.cucumber.plugin.event.EventPublisher;
import io.cucumber.plugin.event.HookTestStep;
import io.cucumber.plugin.event.PickleStepTestStep;
import io.cucumber.plugin.event.Result;
import io.cucumber.plugin.event.Status;
import io.cucumber.plugin.event.TestCase;
import io.cucumber.plugin.event.TestCaseFinished;
import io.cucumber.plugin.event.TestRunFinished;
import io.cucumber.plugin.event.TestStep;
import io.cucumber.plugin.event.TestStepFinished;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Listens to the test run (registered in junit-platform.properties), classifies every failure,
 * and writes the triage report when the run ends. Safe to use with parallel execution.
 */
public class FailureTriagePlugin implements ConcurrentEventListener {

    private final List<FailureRecord> failures = Collections.synchronizedList(new ArrayList<>());
    private final AtomicInteger total = new AtomicInteger();
    private final AtomicInteger passed = new AtomicInteger();

    @Override
    public void setEventPublisher(EventPublisher publisher) {
        publisher.registerHandlerFor(TestStepFinished.class, this::onStepFinished);
        publisher.registerHandlerFor(TestCaseFinished.class, this::onCaseFinished);
        publisher.registerHandlerFor(TestRunFinished.class, event -> writeReports());
    }

    private void onStepFinished(TestStepFinished event) {
        Result result = event.getResult();
        if (result.getStatus() != Status.FAILED || result.getError() == null) {
            return;
        }
        TestCase testCase = event.getTestCase();
        FailureClassifier.Result decision = FailureClassifier.classify(result.getError());
        failures.add(new FailureRecord(
            testCase.getName(),
            testCase.getUri() + ":" + testCase.getLocation().getLine(),
            describe(event.getTestStep()),
            decision.category(),
            decision.reason(),
            Redactor.redact(result.getError().getMessage(), 400)));
    }

    private void onCaseFinished(TestCaseFinished event) {
        total.incrementAndGet();
        if (event.getResult().getStatus() == Status.PASSED) {
            passed.incrementAndGet();
        }
    }

    private void writeReports() {
        try {
            TriageReport.write(new ArrayList<>(failures), total.get(), passed.get());
        } catch (IOException e) {
            System.out.println("Could not write the failure triage report: " + e.getMessage());
        }
    }

    private static String describe(TestStep step) {
        if (step instanceof PickleStepTestStep pickleStep) {
            return pickleStep.getStep().getKeyword().trim() + " " + pickleStep.getStep().getText();
        }
        if (step instanceof HookTestStep hookStep) {
            return "Hook: " + hookStep.getHookType();
        }
        return "(unknown step)";
    }
}
