/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.munit.common.mp;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.apache.commons.lang.StringUtils;

import org.mule.DefaultMuleMessage;
import org.mule.api.MuleContext;
import org.mule.api.MuleEvent;
import org.mule.api.expression.ExpressionManager;
import org.mule.api.processor.MessageProcessor;
import org.mule.modules.interceptor.processors.AbstractMessageProcessorInterceptor;
import org.mule.modules.interceptor.processors.MessageProcessorBehavior;
import org.mule.modules.interceptor.processors.MessageProcessorCall;
import org.mule.modules.interceptor.processors.MessageProcessorId;
import org.mule.munit.common.MunitCore;
import org.mule.munit.common.MunitUtils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * It intercepts the {@link MessageProcessor#process(org.mule.api.MuleEvent)}  calls
 * </p>
 *
 * @author Mulesoft Inc.
 * @since 3.3.2
 */
public class MunitMessageProcessorInterceptor extends AbstractMessageProcessorInterceptor
{

    private String fileName;
    private String lineNumber;


    public Object process(Object obj, Object[] args, MethodProxy proxy) throws Throwable
    {
        MuleEvent event = (MuleEvent) args[0];

        MockedMessageProcessorManager manager = getMockedMessageProcessorManager();

        MunitMessageProcessorCall messageProcessorCall = buildCall(event);
        runSpyBeforeAssertions(manager, event);

        registerCall(manager, messageProcessorCall);
        MessageProcessorBehavior behavior = manager.getBetterMatchingBehavior(messageProcessorCall);
        if (behavior != null)
        {
            if (behavior.getExceptionToThrow() != null)
            {
                runSpyAfterAssertions(manager, event);
                throw behavior.getExceptionToThrow();
            }

            MunitUtils.copyMessage((DefaultMuleMessage) behavior.getReturnMuleMessage(), (DefaultMuleMessage) event.getMessage());

            runSpyAfterAssertions(manager, event);
            return event;
        }


        Object o = proxy.invokeSuper(obj, args);
        runSpyAfterAssertions(manager, (MuleEvent) o);
        return o;
    }


    private void registerCall(MockedMessageProcessorManager manager, MunitMessageProcessorCall messageProcessorCall)
    {
        manager.addCall(messageProcessorCall);
    }

    private void runSpyAfterAssertions(MockedMessageProcessorManager manager, MuleEvent event)
    {
        SpyAssertion spyAssertion = getAssertionFrom(manager);
        if (spyAssertion == null)
        {
            return;
        }

        MunitUtils.verifyAssertions(event, spyAssertion.getAfterMessageProcessors());
    }

    private void runSpyBeforeAssertions(MockedMessageProcessorManager manager, MuleEvent event)
    {
        SpyAssertion spyAssertion = getAssertionFrom(manager);
        if (spyAssertion == null)
        {
            return;
        }

        MunitUtils.verifyAssertions(event, spyAssertion.getBeforeMessageProcessors());
    }

    private SpyAssertion getAssertionFrom(MockedMessageProcessorManager manager)
    {
        Map<MessageProcessorId, SpyAssertion> assertions = manager.getSpyAssertions();
        if (assertions.isEmpty())
        {
            return null;
        }
        SpyAssertion spyAssertion = assertions.get(id);
        if (spyAssertion == null)
        {
            return null;
        }
        return spyAssertion;
    }

    private MunitMessageProcessorCall buildCall(MuleEvent event)
    {
        MunitMessageProcessorCall call = new MunitMessageProcessorCall(id);
        call.setAttributes(getAttributes(event));
        call.setFlowConstruct(event.getFlowConstruct());
        call.setFileName(fileName);
        call.setLineNumber(lineNumber);
        return call;
    }


    protected MockedMessageProcessorManager getMockedMessageProcessorManager()
    {
        return ((MockedMessageProcessorManager) getMuleContext().getRegistry().lookupObject(MockedMessageProcessorManager.ID));
    }

    public MuleContext getMuleContext()
    {
        return MunitCore.getMuleContext();
    }

    public String getFileName()
    {
        return fileName;
    }

    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }

    public void setLineNumber(String lineNumber)
    {
        this.lineNumber = lineNumber;
    }
}
