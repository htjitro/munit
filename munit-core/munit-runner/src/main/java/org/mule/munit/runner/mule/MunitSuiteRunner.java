/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.munit.runner.mule;

import org.mule.api.MuleContext;
import org.mule.munit.runner.MuleContextManager;
import org.mule.munit.runner.MunitRunner;
import org.mule.munit.runner.mule.result.SuiteResult;
import org.mule.munit.runner.mule.result.notification.NotificationListener;
import org.mule.munit.runner.output.DefaultOutputHandler;
import org.mule.munit.runner.output.TestOutputHandler;


/**
 * <p>
 * The Munit test runner
 * </p>
 *
 * @author Mulesoft Inc.
 * @since 3.3.2
 */
public class MunitSuiteRunner
{

    private MuleContext muleContext;
    private MunitSuite suite;
    private TestOutputHandler handler = new DefaultOutputHandler();
    private MuleContextManager muleContextManager = new MuleContextManager(null);



    public MunitSuiteRunner(String resources) {
        this(resources, null);
    }

    public MunitSuiteRunner(String resources, String testToRunName) {
        try {

            muleContext = muleContextManager.startMule(resources);

            suite = new MunitSuiteBuilder(muleContext, handler).build(resources);

        }
        catch (Exception e)
        {
            muleContextManager.killMule(muleContext);
            throw new RuntimeException(e);
        }

    }

    public SuiteResult run()
    {
        return new MunitRunner<SuiteResult>(handler, muleContextManager, muleContext)
        {

            @Override
            protected SuiteResult runSuite() throws Exception
            {
                return suite.run();
            }

            @Override
            protected String getSuiteName()
            {
                return suite.getName();
            }
        }.run();
    }

    public void setNotificationListener(NotificationListener notificationListener)
    {
        this.suite.setNotificationListener(notificationListener);
    }


    public int getNumberOfTests()
    {
        return suite.getNumberOfTests();
    }

}
