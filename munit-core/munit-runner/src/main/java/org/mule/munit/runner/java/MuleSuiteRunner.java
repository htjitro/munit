/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.munit.runner.java;

import junit.framework.*;
import org.junit.runner.Describable;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.manipulation.*;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.mule.api.MuleContext;
import org.mule.munit.runner.MuleContextManager;
import org.mule.munit.runner.MunitRunner;
import org.mule.munit.runner.output.DefaultOutputHandler;

import java.lang.reflect.Method;


/**
 * <p>
 * Mule for Junit Runners
 * </p>
 *
 * @author Mulesoft Inc.
 * @since 3.3.2
 */
public class MuleSuiteRunner extends Runner implements Filterable, Sortable {

    private TestSuite testSuite;
    private MuleContext muleContext;
    private MuleContextManager muleContextManager = new MuleContextManager(null);

    public MuleSuiteRunner(Class testClass) {
        try {
            Method getConfigResources = testClass.getMethod("getConfigResources");
            String resources = (String) getConfigResources.invoke(testClass.newInstance());

            muleContext = muleContextManager.startMule(resources);

            testSuite = new JunitTestSuiteBuilder(muleContext).build(testClass.getSimpleName());

        } catch (Exception e) {

            muleContextManager.killMule(muleContext);
            throw new RuntimeException(e);
        }
    }


    @Override
    public Description getDescription() {
        return makeDescription(testSuite);
    }

    public TestListener createAdaptingListener(final RunNotifier notifier) {
        return new OldTestClassAdaptingListener(notifier);
    }

    @Override
    public void run(RunNotifier notifier) {
        final TestResult result = new TestResult();
        result.addListener(createAdaptingListener(notifier));

        new MunitRunner<Void>(new DefaultOutputHandler(), muleContextManager, muleContext) {

            @Override
            protected Void runSuite() throws Exception {
                testSuite.run(result);
                return null;
            }

            @Override
            protected String getSuiteName() {
                return testSuite.getName();
            }
        }.run();

    }


    private static Description makeDescription(junit.framework.Test test) {

        if (test instanceof TestSuite) {
            TestSuite ts = (TestSuite) test;
            String name = ts.getName() == null ? createSuiteDescription(ts) : ts.getName();
            Description description = Description.createSuiteDescription(name);
            int n = ts.testCount();
            for (int i = 0; i < n; i++) {
                Description made = makeDescription(ts.testAt(i));
                description.addChild(made);
            }
            return description;
        }

        TestCase mt = (TestCase) test;
        return Description.createTestDescription(mt.getClass(), mt.getName());
    }

    private static String createSuiteDescription(TestSuite ts) {
        int count = ts.countTestCases();
        String example = count == 0 ? "" : String.format(" [example: %s]", ts.testAt(0));
        return String.format("TestSuite with %s tests%s", count, example);
    }


    public void filter(Filter filter) throws NoTestsRemainException {
        TestSuite filtered = new TestSuite(testSuite.getName());
        int n = testSuite.testCount();
        for (int i = 0; i < n; i++) {
            junit.framework.Test test = testSuite.testAt(i);
            if (filter.shouldRun(makeDescription(test))) {
                filtered.addTest(test);
            }
        }
        testSuite = filtered;
    }

    public void sort(Sorter sorter) {
        if (testSuite instanceof Sortable) {
            Sortable adapter = (Sortable) testSuite;
            adapter.sort(sorter);
        }
    }


    private final class OldTestClassAdaptingListener implements
            TestListener {

        private final RunNotifier fNotifier;

        private OldTestClassAdaptingListener(RunNotifier notifier) {
            fNotifier = notifier;
        }

        public void endTest(junit.framework.Test test) {
            fNotifier.fireTestFinished(asDescription(test));
        }

        public void startTest(junit.framework.Test test) {
            fNotifier.fireTestStarted(asDescription(test));
        }

        public void addError(junit.framework.Test test, Throwable t) {
            Failure failure = new Failure(asDescription(test), t);
            fNotifier.fireTestFailure(failure);
        }

        private Description asDescription(junit.framework.Test test) {
            if (test instanceof Describable) {
                Describable facade = (Describable) test;
                return facade.getDescription();
            }
            return Description.createTestDescription(getEffectiveClass(test), getName(test));
        }

        private Class<? extends junit.framework.Test> getEffectiveClass(junit.framework.Test test) {
            return test.getClass();
        }

        private String getName(junit.framework.Test test) {
            if (test instanceof TestCase) {
                return ((TestCase) test).getName();
            } else {
                return test.toString();
            }
        }

        public void addFailure(Test test, AssertionFailedError t) {
            addError(test, t);
        }
    }

}
