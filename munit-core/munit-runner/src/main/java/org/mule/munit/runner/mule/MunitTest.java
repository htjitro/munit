/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.munit.runner.mule;

import static junit.framework.Assert.fail;
import static org.mule.munit.common.MunitCore.buildMuleStackTrace;
import org.mule.DefaultMuleEvent;
import org.mule.DefaultMuleMessage;
import org.mule.MessageExchangePattern;
import org.mule.api.MuleContext;
import org.mule.api.MuleEvent;
import org.mule.api.MuleException;
import org.mule.munit.common.MunitCore;
import org.mule.munit.config.MunitFlow;
import org.mule.munit.config.MunitTestFlow;
import org.mule.munit.runner.mule.result.TestResult;
import org.mule.munit.runner.mule.result.notification.Notification;
import org.mule.munit.runner.output.TestOutputHandler;
import org.mule.tck.MuleTestUtils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * <p>MUnit Test</p>
 *
 * @author Mulesoft Inc.
 * @since 3.3.2
 */
public class MunitTest
{

    /**
     * <p>The MUnit flows that have to be run before the MUnit test.</p>
     */
    private List<MunitFlow> before;

    /**
     * <p>The MUnit flows that have to be run after the MUnit test.</p>
     */
    private List<MunitFlow> after;

    /**
     * <p>The MUnit test.</p>
     */
    private MunitTestFlow test;

    /**
     * <p>The Output handler to be use.</p>
     */
    private TestOutputHandler outputHandler;
    private MuleContext muleContext;

    private static String stack2string(Throwable e)
    {
        try
        {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            return sw.toString();
        }
        catch (Exception e2)
        {
            return "";
        }
    }

    public MunitTest(List<MunitFlow> before,
                     MunitTestFlow test,
                     List<MunitFlow> after,
                     TestOutputHandler outputHandler, MuleContext muleContext)
    {
        this.before = before;
        this.after = after;
        this.test = test;
        this.outputHandler = outputHandler;
        this.muleContext = muleContext;
    }

    public String getName()
    {
        return test.getName();
    }
    
    public boolean isIgnore(){ return test.isIgnore();}
    
    public TestResult run()
    {
        TestResult result = new TestResult(getName());
        if (test.isIgnore())
        {
            result.setSkipped(true);
            return result;
        }

        long start = System.currentTimeMillis();
        MuleEvent event = muleEvent();

        try
        {
            run(event, before);
            showDescription();
            test.process(event);
            if (StringUtils.isNotBlank(test.getExpectExceptionThatSatisfies()))
            {
                fail("Exception matching '" + test.getExpectExceptionThatSatisfies() + "', but wasn't thrown");
            }
        }
        catch (final AssertionError t)
        {
            result.setFailure(buildNotifcationFrom(t));
        }
        catch (final MuleException e)
        {
            try
            {
                if (!test.expectException(e, event))
                {
                    e.setStackTrace(buildMuleStackTrace(event.getMuleContext())
                                            .toArray(new StackTraceElement[] {}));
                    result.setError(buildNotifcationFrom(e));
                }
            }
            catch (final AssertionError t)
            {
                t.setStackTrace(buildMuleStackTrace(event.getMuleContext())
                                        .toArray(new StackTraceElement[] {}));
                result.setFailure(buildNotifcationFrom(t));
            }
        }
        finally
        {
            MunitCore.reset(event.getMuleContext());
            runAfter(result, event);
        }

        long end = System.currentTimeMillis();
        result.setTime(new Float(end - start) / 1000);
        return result;

    }


    private Notification buildNotifcationFrom(Throwable t)
    {
        return new Notification(t.getMessage(), stack2string(t));
    }

    private void runAfter(TestResult result, MuleEvent event)
    {
        try
        {
            run(event, after);
        }
        catch (MuleException e)
        {
            result.setError(buildNotifcationFrom(e));
        }
    }

    private void run(MuleEvent event, List<MunitFlow> flows)
            throws MuleException
    {
        if (flows != null)
        {
            for (MunitFlow flow : flows)
            {
                outputHandler.printDescription(flow.getName(), flow.getDescription());
                flow.process(event);
            }
        }
    }

    private void showDescription()
    {
        outputHandler.printDescription(test.getName(), test.getDescription());
    }

    protected MuleEvent muleEvent()
    {
        try
        {
            return new DefaultMuleEvent(new DefaultMuleMessage(null, muleContext), MessageExchangePattern.REQUEST_RESPONSE, MuleTestUtils.getTestFlow(muleContext));

        }
        catch (Exception e)
        {
            return null;
        }
    }

}
