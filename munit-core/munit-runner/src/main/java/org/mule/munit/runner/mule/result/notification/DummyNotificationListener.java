/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.munit.runner.mule.result.notification;

import org.mule.munit.runner.mule.MunitTest;
import org.mule.munit.runner.mule.result.SuiteResult;
import org.mule.munit.runner.mule.result.TestResult;

/**
 * <p>The default notification listener for a Suite. Does nothing.</p>
 *
 * @author Mulesoft Inc.
 * @since 3.3.2
 */
public class DummyNotificationListener implements NotificationListener
{

    @Override
    public void notifyStartOf(MunitTest test)
    {

    }

    @Override
    public void notify(TestResult testResult)
    {

    }

    @Override
    public void notifyIgnored(TestResult testResult) {

    }

    @Override
    public void notifyEnd(SuiteResult result)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
