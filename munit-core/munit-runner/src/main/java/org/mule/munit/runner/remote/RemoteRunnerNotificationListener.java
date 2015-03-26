/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.munit.runner.remote;

import org.mule.munit.runner.mule.MunitTest;
import org.mule.munit.runner.mule.result.SuiteResult;
import org.mule.munit.runner.mule.result.TestResult;
import org.mule.munit.runner.mule.result.notification.NotificationListener;

import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

/**
 *
 * @author Mulesoft Inc.
 * @since 3.6.0
 */
public class RemoteRunnerNotificationListener implements NotificationListener {

    private ObjectOutput out;

    public RemoteRunnerNotificationListener(ObjectOutput out) {
        this.out = out;
    }

    public void notifyNumberOfTest(int numberOfTests) {
        sendMessage(MessageBuilder.buildNuberOfTestsMessage(String.valueOf(numberOfTests)));
    }

    public void notifyStartOf(MunitTest test) {
        sendMessage(MessageBuilder.buildNewTestMessage(test.getName()));
    }

    public void notify(TestResult testResult) {
        if (testResult.getError() != null) {
            sendMessage(MessageBuilder.buildTestErroMessage(testResult.getTestName(), testResult.getError().getFullMessage()));
        } else if (testResult.getFailure() != null) {
            sendMessage(MessageBuilder.buildTestFailureMessage(testResult.getTestName(), testResult.getFailure().getFullMessage()));
        } else {
            sendMessage(MessageBuilder.buildTestFinishedMessage(testResult.getTestName()));
        }

    }

    @Override
    public void notifyIgnored(TestResult testResult) {
        sendMessage(MessageBuilder.buildTestIgnoredMessage(testResult.getName()));
    }
    
    @Override
    public void notifyEnd(SuiteResult result) {
        // DO NOTHING
        // TODO: FIX THIS
    }

    //TODO: shouldn't this be the default implementation of notifyEnd?
    public void notifyTestRunEnd(String testFullPath) {
        sendMessage(MessageBuilder.buildTestRunFinishedMessage(testFullPath));
    }

    private void sendMessage(String message) {
        try {
            out.writeObject(message);
            out.flush();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


}
