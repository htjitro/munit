/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.notifiers;


import org.apache.commons.lang3.StringUtils;
import org.mule.munit.runner.mule.MunitTest;
import org.mule.munit.runner.mule.result.SuiteResult;
import org.mule.munit.runner.mule.result.TestResult;
import org.mule.munit.runner.mule.result.notification.Notification;
import org.mule.munit.runner.mule.result.notification.NotificationListener;

import java.io.PrintStream;

public class StreamNotificationListener implements NotificationListener
{

    private PrintStream out;
    private boolean debugMode = true;

    public StreamNotificationListener(PrintStream out)
    {
        this.out = out;
    }

    public void notifyStartOf(MunitTest test)
    {
        out.flush();
    }

    public void notify(TestResult testResult)
    {
        Notification notification = null;
        if (testResult.getNumberOfErrors() > 0)
        {
            out.println("ERROR - The test " + testResult.getTestName() + " finished with an Error.");
            out.flush();
            notification = testResult.getError();
        }
        else if (testResult.getFailure() != null)
        {
            out.println("FAILURE - The test " + testResult.getTestName() + " finished with a Failure.");
            out.flush();
            notification = testResult.getFailure();
        }

        if (notification != null)
        {
            out.println(notification.getShortMessage());

            if (debugMode)
            {
                out.println(notification.getFullMessage());
            }
            out.flush();
        }
        else if (testResult.isSkipped())
        {
            out.println("SKIPPED - Test " + testResult.getTestName() + " was Skipped.");
            out.flush();
        }
        else
        {
            out.println("SUCCESS - Test " + testResult.getTestName() + " finished Successfully.");
            out.flush();
        }
    }

    @Override
    public void notifyIgnored(TestResult testResult) {
        out.flush();
    }

    @Override
    public void notifyEnd(SuiteResult result)
    {
        out.println();
        String title = "Number of tests run: " + result.getNumberOfTests() + " - Failed: " + result.getNumberOfFailures() + " - Errors: " + result.getNumberOfErrors() + " - Skipped: " + result.getNumberOfSkipped() + " - Time elapsed: " + result.getTime() + "ms";
        String titleFrame = StringUtils.repeat("=", title.length());
        out.println(titleFrame);
        out.println(title);
        out.println(titleFrame);
        out.flush();
    }

    public void setDebugMode(boolean debugMode)
    {
        this.debugMode = debugMode;
    }
}
