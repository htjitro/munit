/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.munit;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.notNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.mule.api.DefaultMuleException;
import org.mule.api.MuleContext;
import org.mule.api.MuleEvent;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.api.NestedProcessor;
import org.mule.api.config.MuleConfiguration;
import org.mule.api.processor.MessageProcessor;
import org.mule.modules.interceptor.processors.MuleMessageTransformer;
import org.mule.munit.common.mocking.EndpointMocker;
import org.mule.munit.common.mocking.MessageProcessorMocker;
import org.mule.munit.common.mocking.MunitSpy;
import org.mule.munit.common.mocking.MunitVerifier;
import org.mule.munit.common.mocking.SpyProcess;
import org.mule.transformer.AbstractMessageTransformer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

/**
 * @author Mulesoft Inc.
 * @since 3.3.2
 */
@SuppressWarnings("unchecked")
public class MockModuleTest
{

    public static final String NAMESPACE = "namespace";
    public static final String MESSAGE_PROCESSOR = "mp";
    public static final String FULL_NAME = NAMESPACE + ":" + MESSAGE_PROCESSOR;
    public static final String INBOUND_KEY = "inboundKey";
    public static final String INBOUND_VALUE = "inboundValue";
    public static final String OUTBOUND_KEY = "outboundKey";
    public static final String OUTBOUND_VALUE = "outboundValue";
    public static final String INVOCATION_KEY = "invocationKey";
    public static final String INVOCATION_VALUE = "invocationValue";
    public static final String SESSION_VALUE = "sessionValue";
    public static final String SESSION_KEY = "sessionKey";
    public static final String PAYLOAD = "payload";
    public static final Exception EXCEPTION = new Exception("error");
    public static final String ADDRESS = "address";
    public static final ArrayList<Attribute> VERIFY_ATTRIBUTES = new ArrayList<Attribute>();
    private MessageProcessorMocker mocker = mock(MessageProcessorMocker.class);
    private MuleContext muleContext = mock(MuleContext.class);
    private EndpointMocker endpointMocker = mock(EndpointMocker.class);
    private MunitSpy spy = mock(MunitSpy.class);
    private MessageProcessor messageProcessor = mock(MessageProcessor.class);
    private MunitVerifier verifier = mock(MunitVerifier.class);
    private AbstractMessageTransformer muleTransformer = mock(AbstractMessageTransformer.class);

    @Before
    public void setUp(){
        MuleConfiguration muleConfiguration = mock(MuleConfiguration.class);
        when(muleConfiguration.getDefaultEncoding()).thenReturn("UTF-8");
        when(muleContext.getConfiguration()).thenReturn(muleConfiguration);
    }
    @Test
    public void whenMethodCanHandlerNullOptionals()
    {
        defineMockerBehavior();

        module().when(NAMESPACE + ":" + MESSAGE_PROCESSOR, null, null, null);

        verify(mocker, times(1)).when(MESSAGE_PROCESSOR);
        verify(mocker, times(1)).ofNamespace(NAMESPACE);
        verify(mocker, times(1)).withAttributes((Map<String, Object>) notNull());
        verify(mocker, times(1)).thenReturn(any(MuleMessage.class));
    }

    @Test
    public void whenMethodCanHandleNotNullProperties()
    {
        defineMockerBehavior();

        module().when(NAMESPACE + ":" + MESSAGE_PROCESSOR, createAttributes(), null, null);

        verify(mocker, times(1)).when(MESSAGE_PROCESSOR);
        verify(mocker, times(1)).ofNamespace(NAMESPACE);
        verify(mocker, times(1)).withAttributes((Map<String, Object>) notNull());
        verify(mocker, times(1)).thenReturn(any(MuleMessage.class));
    }

    @Test
    public void applyTransformer()
    {
        defineMockerBehavior();

        module().when(NAMESPACE + ":" + MESSAGE_PROCESSOR, createAttributes(), null, muleTransformer);

        verify(mocker, times(1)).when(MESSAGE_PROCESSOR);
        verify(mocker, times(1)).ofNamespace(NAMESPACE);
        verify(mocker, times(1)).withAttributes((Map<String, Object>) notNull());
        verify(mocker, times(1)).thenApply((MuleMessageTransformer) notNull());
    }

    @Test
    public void applyInvalidTransformer()
    {
        defineMockerBehavior();

        module().when(NAMESPACE + ":" + MESSAGE_PROCESSOR, createAttributes(), null, new Object());

        verify(mocker, times(1)).when(MESSAGE_PROCESSOR);
        verify(mocker, times(1)).ofNamespace(NAMESPACE);
        verify(mocker, times(1)).withAttributes((Map<String, Object>) notNull());
        verify(mocker, times(0)).thenApply((MuleMessageTransformer) notNull());
    }


    @Test
    public void noNamespaceMeansMuleNamespace()
    {
        defineMockerMuleNamespaceBehavior();

        module().when(MESSAGE_PROCESSOR, createAttributes(), null, null);

        verify(mocker, times(1)).when(MESSAGE_PROCESSOR);
        verify(mocker, times(1)).ofNamespace("mule");
        verify(mocker, times(1)).withAttributes((Map<String, Object>) notNull());
        verify(mocker, times(1)).thenReturn(any(MuleMessage.class));
    }

    @Test
    public void whenMethodCanHandleNotNullReturnValue()
    {
        defineMockerBehavior();

        module().when(NAMESPACE + ":" + MESSAGE_PROCESSOR, createAttributes(), createMuleMessage(), null);

        verify(mocker, times(1)).when(MESSAGE_PROCESSOR);
        verify(mocker, times(1)).ofNamespace(NAMESPACE);
        verify(mocker, times(1)).withAttributes((Map<String, Object>) notNull());
        verify(mocker, times(1)).thenReturn(any(MuleMessage.class));
    }

    @Test
    public void throwExceptionMustSupportNullOptionals()
    {
        defineMockerBehavior();
        module().throwAn(EXCEPTION, FULL_NAME, null);

        verify(mocker, times(1)).when(MESSAGE_PROCESSOR);
        verify(mocker, times(1)).ofNamespace(NAMESPACE);
        verify(mocker, times(1)).withAttributes((Map<String, Object>) notNull());
        verify(mocker, times(1)).thenThrow(EXCEPTION);
    }

    @Test
    public void throwExceptionMustSupportAttributes()
    {
        defineMockerBehavior();
        module().throwAn(EXCEPTION, NAMESPACE + ":" + MESSAGE_PROCESSOR, createAttributes());

        verify(mocker, times(1)).when(MESSAGE_PROCESSOR);
        verify(mocker, times(1)).ofNamespace(NAMESPACE);
        verify(mocker, times(1)).withAttributes((Map<String, Object>) notNull());
        verify(mocker, times(1)).thenThrow(EXCEPTION);
    }

    @Test
    public void endpointMockingMustSupportNullOptionals()
    {
        endpointMockerBehavior();

        module().outboundEndpoint(ADDRESS, null, null, null, null, null, null, null, null);

        verify(endpointMocker, times(1)).whenEndpointWithAddress(ADDRESS);
        verify(endpointMocker, times(1)).withIncomingMessageSatisfying((List<SpyProcess>) notNull());
        verify(endpointMocker, times(1)).thenReturn((MuleMessage) notNull());
    }


    @Test
    public void endpointMockingMustSupportOptionals()
    {
        endpointMockerBehavior();

        module().outboundEndpoint(ADDRESS, PAYLOAD,
                                  null,
                                  null,
                                  props(entry(INVOCATION_KEY, INVOCATION_VALUE)),
                                  props(entry(INBOUND_KEY, INBOUND_VALUE)),
                                  props(entry(SESSION_KEY, SESSION_VALUE)),
                                  props(entry(OUTBOUND_KEY, OUTBOUND_VALUE)),
                                  createAssertions());

        verify(endpointMocker, times(1)).whenEndpointWithAddress(ADDRESS);
        verify(endpointMocker, times(1)).withIncomingMessageSatisfying((List<SpyProcess>) notNull());
        verify(endpointMocker, times(1)).thenReturn((MuleMessage) notNull());
    }

    @Test
    public void endpointThatThrowsException()
    {
        endpointMockerBehavior();

        module().outboundEndpoint(ADDRESS, PAYLOAD,
                                  new DefaultMuleException("exception"), null,
                                  props(entry(INVOCATION_KEY, INVOCATION_VALUE)),
                                  props(entry(INBOUND_KEY, INBOUND_VALUE)),
                                  props(entry(SESSION_KEY, SESSION_VALUE)),
                                  props(entry(OUTBOUND_KEY, OUTBOUND_VALUE)),
                                  createAssertions());

        verify(endpointMocker, times(1)).whenEndpointWithAddress(ADDRESS);
        verify(endpointMocker, times(1)).withIncomingMessageSatisfying((List<SpyProcess>) notNull());
        verify(endpointMocker, times(1)).thenThrow((MuleException) notNull());
    }

    @Test
    public void endpointWithTransformer()
    {
        endpointMockerBehavior();

        module().outboundEndpoint(ADDRESS, PAYLOAD,
                                  new DefaultMuleException("exception"), muleTransformer,
                                  props(entry(INVOCATION_KEY, INVOCATION_VALUE)),
                                  props(entry(INBOUND_KEY, INBOUND_VALUE)),
                                  props(entry(SESSION_KEY, SESSION_VALUE)),
                                  props(entry(OUTBOUND_KEY, OUTBOUND_VALUE)),
                                  createAssertions());

        verify(endpointMocker, times(1)).whenEndpointWithAddress(ADDRESS);
        verify(endpointMocker, times(1)).withIncomingMessageSatisfying((List<SpyProcess>) notNull());
        verify(endpointMocker, times(1)).thenApply((MuleMessageTransformer) notNull());
    }

    @Test
    public void spyMustSupportNullOptionals()
    {
        spyBehavior();

        module().spy(FULL_NAME, null, null, null);

        verify(spy, times(1)).spyMessageProcessor(MESSAGE_PROCESSOR);
        verify(spy, times(1)).ofNamespace(NAMESPACE);
        verify(spy, times(1)).before((List<SpyProcess>) notNull());
        verify(spy, times(1)).after((List<SpyProcess>) notNull());
    }

    @Test
    public void spyMustSupportOptionals()
    {
        spyBehavior();

        module().spy(FULL_NAME, null, createAssertions(), createAssertions());

        verify(spy, times(1)).spyMessageProcessor(MESSAGE_PROCESSOR);
        verify(spy, times(1)).ofNamespace(NAMESPACE);
        verify(spy, times(1)).before((List<SpyProcess>) notNull());
        verify(spy, times(1)).before((List<SpyProcess>) notNull());
    }

    @Test
    public void createSpyIsCorrect() throws MuleException
    {
        SpyProcess spyProcess = module().createSpy(createMessageProcessors());

        MuleEvent event = mock(MuleEvent.class);
        spyProcess.spy(event);

        verify(messageProcessor, times(1)).process(event);
    }

    @Test
    public void verifyCallXTimes()
    {

        verifierBehavior();

        module().verifyCall(NAMESPACE + ":" + MESSAGE_PROCESSOR, VERIFY_ATTRIBUTES, 3, null,null);

        verify(verifier, times(1)).times(3);
    }

    @Test
    public void verifyCallAtLeastXTimes()
    {

        verifierBehavior();

        module().verifyCall(NAMESPACE + ":" + MESSAGE_PROCESSOR, VERIFY_ATTRIBUTES, null, 3,null);

        verify(verifier, times(1)).atLeast(3);
    }

    @Test
    public void verifyCallAtMostXTimes()
    {

        verifierBehavior();

        module().verifyCall(NAMESPACE + ":" + MESSAGE_PROCESSOR, VERIFY_ATTRIBUTES, null, null,3);

        verify(verifier,times(1)).atMost(3);
    }


    @Test
    public void verifyCallAtLeastOne()
    {

        verifierBehavior();

        module().verifyCall(NAMESPACE + ":" + MESSAGE_PROCESSOR, VERIFY_ATTRIBUTES, null, null,null);

        verify(verifier,times(1)).atLeastOnce();
    }

    private List<MessageProcessor> createMessageProcessors()
    {
        ArrayList<MessageProcessor> messageProcessors = new ArrayList<MessageProcessor>();

        messageProcessors.add(messageProcessor);
        return messageProcessors;
    }

    private void spyBehavior()
    {
        when(spy.ofNamespace(NAMESPACE)).thenReturn(spy);
        when(spy.spyMessageProcessor(MESSAGE_PROCESSOR)).thenReturn(spy);
        when(spy.withAttributes(anyMap())).thenReturn(spy);
        when(spy.before(anyList())).thenReturn(spy);
        when(spy.after(anyList())).thenReturn(spy);
    }

    private void verifierBehavior()
    {
        when(verifier.ofNamespace(NAMESPACE)).thenReturn(verifier);
        when(verifier.verifyCallOfMessageProcessor(MESSAGE_PROCESSOR)).thenReturn(verifier);
        when(verifier.withAttributes(anyMap())).thenReturn(verifier);
    }

    private List<NestedProcessor> createAssertions()
    {
        ArrayList<NestedProcessor> nestedProcessors = new ArrayList<NestedProcessor>();
        nestedProcessors.add(mock(NestedProcessor.class));
        return nestedProcessors;
    }

    private MunitMuleMessage createMuleMessage()
    {
        MunitMuleMessage munitMuleMessage = new MunitMuleMessage();
        munitMuleMessage.setInboundProperties(props(entry(INBOUND_KEY, INBOUND_VALUE)));
        munitMuleMessage.setOutboundProperties(props(entry(OUTBOUND_KEY, OUTBOUND_VALUE)));
        munitMuleMessage.setInvocationProperties(props(entry(INVOCATION_KEY, INVOCATION_VALUE)));
        // TODO: add this line and make test work
        //        munitMuleMessage.setSessionProperties(props(entry(SESSION_KEY, SESSION_VALUE)));
        munitMuleMessage.setPayload(PAYLOAD);
        return munitMuleMessage;
    }

    private HashMap<String, Object> props(Map.Entry<String, Object>... entries)
    {
        HashMap<String, Object> map = new HashMap<String, Object>();

        for (Map.Entry<String, Object> entry : entries)
        {
            map.put(entry.getKey(), entry.getValue());
        }
        return map;
    }

    private Map.Entry<String, Object> entry(String key, Object value)
    {
        return new HashMap.SimpleEntry<String, Object>(key, value);
    }

    private List<Attribute> createAttributes()
    {
        List<Attribute> attributes = new ArrayList<Attribute>();
        attributes.add(Attribute.create("attribute", "attributeValue"));
        return attributes;
    }

    private void defineMockerBehavior()
    {
        when(mocker.when(MESSAGE_PROCESSOR)).thenReturn(mocker);
        when(mocker.withAttributes(anyMap())).thenReturn(mocker);
        when(mocker.ofNamespace(NAMESPACE)).thenReturn(mocker);
    }

    private void defineMockerMuleNamespaceBehavior()
    {
        when(mocker.when(MESSAGE_PROCESSOR)).thenReturn(mocker);
        when(mocker.withAttributes(anyMap())).thenReturn(mocker);
        when(mocker.ofNamespace("mule")).thenReturn(mocker);
    }

    private MockMockModule module()
    {
        MockMockModule mockMockModule = new MockMockModule(mocker, endpointMocker, spy, verifier);
        mockMockModule.setMuleContext(muleContext);
        return mockMockModule;
    }

    private void endpointMockerBehavior()
    {
        when(endpointMocker.whenEndpointWithAddress(anyString())).thenReturn(endpointMocker);
        when(endpointMocker.withIncomingMessageSatisfying(anyList())).thenReturn(endpointMocker);
    }
}
