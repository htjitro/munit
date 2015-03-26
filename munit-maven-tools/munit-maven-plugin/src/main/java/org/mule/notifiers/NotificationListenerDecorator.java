/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.notifiers;

import org.mule.munit.runner.mule.MunitTest;
import org.mule.munit.runner.mule.result.SuiteResult;
import org.mule.munit.runner.mule.result.TestResult;
import org.mule.munit.runner.mule.result.notification.NotificationListener;

import java.util.ArrayList;
import java.util.List;

public class NotificationListenerDecorator implements NotificationListener
{

    private List<NotificationListener> notificationListeners = new ArrayList<NotificationListener>();


    @Override
    public void notifyStartOf(MunitTest test)
    {
        for (NotificationListener notificationListener : notificationListeners)
        {
            notificationListener.notifyStartOf(test);
        }
    }

    @Override
    public void notify(TestResult testResult)
    {
        for (NotificationListener notificationListener : notificationListeners)
        {
            notificationListener.notify(testResult);
        }
    }

    @Override
    public void notifyIgnored(TestResult testResult) {
        for (NotificationListener notificationListener : notificationListeners)
        {
            notificationListener.notifyIgnored(testResult);
        }
    }

    @Override
    public void notifyEnd(SuiteResult result)
    {
        for (NotificationListener notificationListener : notificationListeners)
        {
            notificationListener.notifyEnd(result);
        }
    }

    public void addNotificationListener(NotificationListener listener)
    {
        notificationListeners.add(listener);
    }
}
