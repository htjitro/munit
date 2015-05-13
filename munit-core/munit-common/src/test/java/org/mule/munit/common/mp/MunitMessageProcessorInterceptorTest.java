/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.munit.common.mp;

import net.sf.cglib.proxy.MethodProxy;
import org.junit.Before;
import org.junit.Test;
import org.mule.DefaultMuleEvent;
import org.mule.DefaultMuleMessage;
import org.mule.MessageExchangePattern;
import org.mule.api.MuleContext;
import org.mule.api.MuleEvent;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.api.config.MuleConfiguration;
import org.mule.api.expression.ExpressionManager;
import org.mule.api.processor.MessageProcessor;
import org.mule.api.transport.PropertyScope;
import org.mule.construct.Flow;
import org.mule.modules.interceptor.processors.MessageProcessorBehavior;
import org.mule.modules.interceptor.processors.MessageProcessorCall;
import org.mule.modules.interceptor.processors.MessageProcessorId;
import org.mule.munit.common.mocking.CopyMessageTransformer;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static junit.framework.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * @author Mulesoft Inc.
 * @since 3.3.2
 */
public class MunitMessageProcessorInterceptorTest {

    public static final String NAMESPACE = "namespace";
    public static final String MP = "mp";
    public static final MessageProcessorId MESSAGE_PROCESSOR_ID = new MessageProcessorId(MP, NAMESPACE);
    public static final MessageProcessorCall MESSAGE_PROCESSOR_CALL = new MessageProcessorCall(MESSAGE_PROCESSOR_ID);
    public static final Exception EXCEPTION_TO_THROW = new Exception();
    public static final MessageProcessorBehavior EXCEPTION_BEHAVIOR = new MessageProcessorBehavior(MESSAGE_PROCESSOR_CALL, EXCEPTION_TO_THROW);
    public static final String PAYLOAD = "payload";
    public static final Object OBJECT = new Object();
    public static final String ATTR_VALUE = "hello";


    MockedMessageProcessorManager manager;
    MethodProxy proxy;
    private MuleEvent event;
    private MuleContext muleContext;
    private SpyMessageProcessor beforeAssertionMp;
    private SpyMessageProcessor afterAssertionMp;
    private ExpressionManager expressionManager;

    @Before
    public void setUp() throws Exception {
        manager = mock(MockedMessageProcessorManager.class);
        proxy = mock(MethodProxy.class);
        event = mock(MuleEvent.class);
        muleContext = mock(MuleContext.class);
        expressionManager = mock(ExpressionManager.class);
        beforeAssertionMp = new SpyMessageProcessor();
        afterAssertionMp = new SpyMessageProcessor();


        MuleConfiguration configurationMock = mock(MuleConfiguration.class);
        when(muleContext.getConfiguration()).thenReturn(configurationMock);
        when(configurationMock.getDefaultResponseTimeout()).thenReturn(0);
    }

    private MockMunitMessageProcessorInterceptor interceptor() {
        MockMunitMessageProcessorInterceptor interceptor = new MockMunitMessageProcessorInterceptor(manager);
        interceptor.setId(MESSAGE_PROCESSOR_ID);
        return interceptor;
    }

    /**
     * <p>
     * Scenario:
     * No Spy before assertion.
     * No Spy after assertion.
     * Throw exception.
     * No attributes.
     * </p>
     */
    @Test
    public void interceptWithExceptionToThrow() throws Throwable {
        MunitMessageProcessorInterceptor interceptor = interceptor();
        interceptor.setAttributes(new HashMap<String, String>());

        when(manager.getBetterMatchingBehavior(any(MessageProcessorCall.class))).thenReturn(EXCEPTION_BEHAVIOR);

        MuleMessage testMessage = new DefaultMuleMessage("", muleContext);
        MuleEvent testEvent = new DefaultMuleEvent(testMessage, MessageExchangePattern.REQUEST_RESPONSE, getTestFlow("aa", muleContext, false));

        try {
            interceptor.process(new Object(), new Object[]{testEvent}, proxy);
        } catch (Exception e) {
            assertEquals(EXCEPTION_TO_THROW, e);
            verify(manager).addCall(any(MunitMessageProcessorCall.class));
            return;
        }

        fail();
    }

    public static Flow getTestFlow(String name, MuleContext context, boolean initialize) {

        try {
            Flow flow = new Flow(name, context);
            if (initialize) {
                context.getRegistry().registerFlowConstruct(flow);
            }

            return flow;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }


    /**
     * <p>
     * Scenario:
     * No Spy before assertion.
     * No Spy after assertion.
     * Return Event.
     * No attributes.
     * </p>
     */
    @Test
    public void interceptWithValueToReturn() throws Throwable {
        MunitMessageProcessorInterceptor interceptor = interceptor();
        interceptor.setAttributes(new HashMap<String, String>());

        MuleMessage expectedMessage = muleMessage();

        MessageProcessorBehavior messageProcessorBehavior = returnValueBehavior();
        when(manager.getBetterMatchingBehavior(any(MessageProcessorCall.class))).thenReturn(messageProcessorBehavior);
        when(event.getMessage()).thenReturn(expectedMessage);


        MuleMessage testMessage = new DefaultMuleMessage(expectedMessage, muleContext);
        MuleEvent testEvent = new DefaultMuleEvent(testMessage, MessageExchangePattern.REQUEST_RESPONSE, getTestFlow("aa", muleContext, false));


        MuleEvent processed = (MuleEvent) interceptor.process(new Object(), new Object[]{testEvent}, proxy);

        verify(manager).addCall(any(MunitMessageProcessorCall.class));


        compareMessagesAsEquals(expectedMessage, processed.getMessage());
    }


    /**
     * <p>
     * Scenario:
     * With Spy before assertion.
     * With Spy after assertion.
     * Return Event.
     * No attributes.
     * </p>
     */
    @Test
    public void interceptWithSpyBeforeAssertion() throws Throwable {
        MunitMessageProcessorInterceptor interceptor = interceptor();
        interceptor.setAttributes(new HashMap<String, String>());

        MuleMessage expectedMessage = muleMessage();

        when(manager.getBetterMatchingBeforeSpyAssertion(any(MessageProcessorCall.class))).thenReturn(spyAssertion(createAssertions(beforeAssertionMp)));
        when(manager.getBetterMatchingAfterSpyAssertion(any(MessageProcessorCall.class))).thenReturn(spyAssertion(createAssertions(afterAssertionMp)));
        MessageProcessorBehavior messageProcessorBehavior = returnValueBehavior();
        when(manager.getBetterMatchingBehavior(any(MessageProcessorCall.class))).thenReturn(messageProcessorBehavior);
        when(event.getMessage()).thenReturn(expectedMessage);

        MuleMessage testMessage = new DefaultMuleMessage(expectedMessage, muleContext);
        MuleEvent testEvent = new DefaultMuleEvent(testMessage, MessageExchangePattern.REQUEST_RESPONSE, getTestFlow("aa", muleContext, false));


        MuleEvent processed = (MuleEvent) interceptor.process(new Object(), new Object[]{testEvent}, proxy);

        verify(manager).addCall(any(MunitMessageProcessorCall.class));

        compareMessagesAsEquals(expectedMessage, processed.getMessage());
        assertTrue(beforeAssertionMp.called);
        assertTrue(afterAssertionMp.called);
    }


    /**
     * <p>
     * Scenario:
     * With Spy before assertion.
     * With Spy after assertion.
     * Return Event.
     * with attributes.
     * </p>
     */
    @Test
    public void interceptWithSpyBeforeAssertionWithAttributes() throws Throwable {
        MockMunitMessageProcessorInterceptor interceptor = interceptor();
        interceptor.setAttributes(getAttributes());
        interceptor.setContext(muleContext);

        MuleMessage expectedMessage = muleMessage();


        when(muleContext.getExpressionManager()).thenReturn(expressionManager);
        when(expressionManager.isExpression(ATTR_VALUE)).thenReturn(true);
        when(expressionManager.parse(ATTR_VALUE, event)).thenReturn("any");
        when(manager.getBetterMatchingBeforeSpyAssertion(any(MessageProcessorCall.class))).thenReturn(spyAssertion(createAssertions(beforeAssertionMp)));
        when(manager.getBetterMatchingAfterSpyAssertion(any(MessageProcessorCall.class))).thenReturn(spyAssertion(createAssertions(afterAssertionMp)));
        MessageProcessorBehavior messageProcessorBehavior = returnValueBehavior();
        when(manager.getBetterMatchingBehavior(any(MessageProcessorCall.class))).thenReturn(messageProcessorBehavior);
        when(event.getMessage()).thenReturn(expectedMessage);

        MuleMessage testMessage = new DefaultMuleMessage(expectedMessage, muleContext);
        MuleEvent testEvent = new DefaultMuleEvent(testMessage, MessageExchangePattern.REQUEST_RESPONSE, getTestFlow("aa", muleContext, false));

        MuleEvent processed = (MuleEvent) interceptor.process(new Object(), new Object[]{testEvent}, proxy);

        verify(manager).addCall(any(MunitMessageProcessorCall.class));
        
        compareMessagesAsEquals(expectedMessage, processed.getMessage());
        assertTrue(beforeAssertionMp.called);
        assertTrue(afterAssertionMp.called);
    }

    /**
     * <p>
     * Scenario:
     * Not Mocked message processor
     * </p>
     */
    @Test
    public void interceptWithNoMockedMessageProcessor() throws Throwable {
        MunitMessageProcessorInterceptor interceptor = interceptor();
        interceptor.setAttributes(new HashMap<String, String>());

        when(manager.getBetterMatchingBehavior(any(MessageProcessorCall.class))).thenReturn(null);

        MuleMessage testMessage = new DefaultMuleMessage("", muleContext);
        MuleEvent testEvent = new DefaultMuleEvent(testMessage, MessageExchangePattern.REQUEST_RESPONSE, getTestFlow("aa", muleContext, false));
        
        Object[] args = {testEvent};
        when(proxy.invokeSuper(OBJECT, args)).thenReturn(testEvent);

        MuleEvent processed = (MuleEvent)interceptor.process(OBJECT, args, proxy);

        verify(manager).addCall(any(MunitMessageProcessorCall.class));

        compareMessagesAsEquals(testMessage, processed.getMessage());
    }

    @Test
    public void ifNoMessageProcessorClassThenCallProxyOnIntercept() throws Throwable {

        Method method = MunitMessageProcessorInterceptorTest.class.getMethod("ifNoMessageProcessorClassThenCallProxyOnIntercept");
        interceptor().intercept(OBJECT, method, null, proxy);

        verify(proxy).invokeSuper(OBJECT, null);
    }


    @Test
    public void interceptProcessCallsOnly() throws Throwable {

        Method method = MessageProcessor.class.getMethod("process", MuleEvent.class);
        MockMunitMessageProcessorInterceptor interceptor = interceptor();
        interceptor.setMockProcess(true);
        SpyMessageProcessor mp = new SpyMessageProcessor();
        Object intercept = interceptor.intercept(mp, method, null, proxy);

        verify(proxy, never()).invokeSuper(mp, null);
        assertEquals(mp, intercept);
    }

    private SpyAssertion spyAssertion(List<MessageProcessor> mp) {
        return new SpyAssertion(new MessageProcessorCall(MESSAGE_PROCESSOR_ID), mp);
    }

    private ArrayList<MessageProcessor> createAssertions(MessageProcessor messageProcessor) {
        ArrayList<MessageProcessor> messageProcessors = new ArrayList<MessageProcessor>();
        messageProcessors.add(messageProcessor);
        return messageProcessors;
    }

    private MessageProcessorBehavior returnValueBehavior() {
        return new MessageProcessorBehavior(MESSAGE_PROCESSOR_CALL, new CopyMessageTransformer((DefaultMuleMessage) muleMessage()));
    }

    private MuleMessage muleMessage() {
        return new DefaultMuleMessage(PAYLOAD, muleContext);
    }

    private HashMap<String, String> getAttributes() {
        HashMap<String, String> attrs = new HashMap<String, String>();
        attrs.put("attr", ATTR_VALUE);
        return attrs;
    }


    private void compareMessagesAsEquals(MuleMessage expectedMessage, MuleMessage actualMessage) {

        compareProperties(expectedMessage,actualMessage,PropertyScope.SESSION);
        compareProperties(expectedMessage,actualMessage,PropertyScope.INBOUND);
        compareProperties(expectedMessage,actualMessage,PropertyScope.OUTBOUND);
        compareProperties(expectedMessage,actualMessage,PropertyScope.INVOCATION);


        assertEquals(expectedMessage.getPayload(), actualMessage.getPayload());
    }

    private void compareProperties(MuleMessage expectedMessage, MuleMessage actualMessage, PropertyScope scope){
        assertEquals(expectedMessage.getPropertyNames(scope).size(), actualMessage.getPropertyNames(scope).size());
        for (String name : expectedMessage.getPropertyNames(scope)) {
            assertEquals(expectedMessage.getProperty(name, scope), actualMessage.getProperty(name, scope));
        }

    }


    private class SpyMessageProcessor implements MessageProcessor {

        boolean called;

        @Override
        public MuleEvent process(MuleEvent event) throws MuleException {
            called = true;
            return event;
        }
    }


}
