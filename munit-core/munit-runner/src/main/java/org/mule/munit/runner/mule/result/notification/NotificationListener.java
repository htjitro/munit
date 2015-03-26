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
 * <p>Clases implementing this interface handle the notification of a test result. </p>
 *
 * @author Mulesoft Inc.
 * @since 3.3.2
 */
public interface NotificationListener
{

    void notifyStartOf(MunitTest test);

    void notify(TestResult testResult);
    
    void notifyIgnored(TestResult testResult);

    void notifyEnd(SuiteResult result);
}
