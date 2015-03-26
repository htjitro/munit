/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.munit.runner.mule;

import org.mule.munit.runner.mule.result.SuiteResult;
import org.mule.munit.runner.mule.result.TestResult;
import org.mule.munit.runner.mule.result.notification.DummyNotificationListener;
import org.mule.munit.runner.mule.result.notification.NotificationListener;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>MUnit Suite</p>
 *
 * @author Mulesoft Inc.
 * @since 3.3.2
 */
public class MunitSuite {

    private String name;
    private List<MunitTest> munitTests = new ArrayList<MunitTest>();
    private NotificationListener notificationListener = new DummyNotificationListener();

    public MunitSuite(String name) {
        this.name = name;
    }

    public void add(MunitTest test) {
        munitTests.add(test);
    }

    /**
     * <p>Runs all the tests of the suite </p>
     *
     * @return The Result of the suite execution
     * @throws Exception If the suite failed for one reason.
     */
    public SuiteResult run() throws Exception {
        SuiteResult result = new SuiteResult(name);

        for (MunitTest test : munitTests) {
            notificationListener.notifyStartOf(test);

            TestResult testResult = test.run();
            result.add(testResult);

            if (testResult.isSkipped()) {
                notificationListener.notifyIgnored(testResult);
            }

            notificationListener.notify(testResult);
        }

        notificationListener.notifyEnd(result);
        return result;
    }

    public void setNotificationListener(NotificationListener notificationListener) {
        if (notificationListener == null) {
            throw new IllegalArgumentException();
        }

        this.notificationListener = notificationListener;
    }

    public int getNumberOfTests() {

        return munitTests.size();
    }

    public String getName() {
        return name;
    }
}
