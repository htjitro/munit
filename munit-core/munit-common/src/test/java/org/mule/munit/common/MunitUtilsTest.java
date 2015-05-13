/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.munit.common;

import org.junit.Before;
import org.junit.Test;

import org.mule.DefaultMuleMessage;
import org.mule.api.MuleContext;
import org.mule.api.MuleEvent;
import org.mule.api.MuleException;
import org.mule.api.config.MuleConfiguration;
import org.mule.api.processor.MessageProcessor;
import org.mule.munit.common.mocking.NotDefinedPayload;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * @author Mulesoft Inc.
 * @since 3.3.2
 */
public class MunitUtilsTest
{

    public static final String PROPERTY = "property";
    public static final String PROPERTY_VALUE = "propertyValue";
    public static final String ORIGINAL_PAYLOAD = "Original Payload";
    public static final String A_PAYLOAD = "A Payload";
    private MuleContext muleContext;
    private MuleEvent muleEvent;
    private MessageProcessor mp;

    @Before
    public void setUp()
    {
        muleContext = mock(MuleContext.class);
        muleEvent = mock(MuleEvent.class);
        mp = mock(MessageProcessor.class);

        MuleConfiguration muleConfiguration = mock(MuleConfiguration.class);
        when(muleConfiguration.getDefaultEncoding()).thenReturn("UTF-8");
        when(muleContext.getConfiguration()).thenReturn(muleConfiguration);
    }

    @Test
    public void testCopyMessageWithNoValues()
    {

        DefaultMuleMessage originalMessage = new DefaultMuleMessage(ORIGINAL_PAYLOAD, muleContext);
        DefaultMuleMessage copyToMessage = new DefaultMuleMessage(A_PAYLOAD, muleContext);
        MunitUtils.copyMessage(originalMessage, copyToMessage);

        assertEquals(ORIGINAL_PAYLOAD, copyToMessage.getPayload());
    }

    @Test
    public void testCopyMessageWithInbound()
    {

        DefaultMuleMessage originalMessage = new DefaultMuleMessage(ORIGINAL_PAYLOAD, muleContext);
        originalMessage.setInboundProperty(PROPERTY, PROPERTY_VALUE);
        DefaultMuleMessage copyToMessage = new DefaultMuleMessage(A_PAYLOAD, muleContext);
        MunitUtils.copyMessage(originalMessage, copyToMessage);

        assertEquals(ORIGINAL_PAYLOAD, copyToMessage.getPayload());
        assertEquals(PROPERTY_VALUE, copyToMessage.getInboundProperty(PROPERTY));
    }

    @Test
    public void testCopyMessageWithInvocation()
    {

        DefaultMuleMessage originalMessage = new DefaultMuleMessage(ORIGINAL_PAYLOAD, muleContext);
        originalMessage.setInvocationProperty(PROPERTY, PROPERTY_VALUE);
        DefaultMuleMessage copyToMessage = new DefaultMuleMessage(A_PAYLOAD, muleContext);
        MunitUtils.copyMessage(originalMessage, copyToMessage);

        assertEquals(ORIGINAL_PAYLOAD, copyToMessage.getPayload());
        assertEquals(PROPERTY_VALUE, copyToMessage.getInvocationProperty(PROPERTY));
    }

    @Test
    public void testCopyMessageWithOutbound()
    {

        DefaultMuleMessage originalMessage = new DefaultMuleMessage(ORIGINAL_PAYLOAD, muleContext);
        originalMessage.setOutboundProperty(PROPERTY, PROPERTY_VALUE);
        DefaultMuleMessage copyToMessage = new DefaultMuleMessage(A_PAYLOAD, muleContext);
        MunitUtils.copyMessage(originalMessage, copyToMessage);

        assertEquals(ORIGINAL_PAYLOAD, copyToMessage.getPayload());
        assertEquals(PROPERTY_VALUE, copyToMessage.getOutboundProperty(PROPERTY));
    }

    @Test
    public void ifNotDefinedPayloadThenDoNotCopy()
    {

        DefaultMuleMessage originalMessage = new DefaultMuleMessage(NotDefinedPayload.getInstance(), muleContext);
        DefaultMuleMessage copyToMessage = new DefaultMuleMessage(A_PAYLOAD, muleContext);
        MunitUtils.copyMessage(originalMessage, copyToMessage);

        assertEquals(A_PAYLOAD, copyToMessage.getPayload());
    }

    @Test
    public void ifNoAssertionsThenDoNothing()
    {
        MunitUtils.verifyAssertions(null, null);
    }


    @Test
    public void runAssertions() throws MuleException
    {
        List<MessageProcessor> messageProcessors = new ArrayList<MessageProcessor>();
        messageProcessors.add(mp);

        MunitUtils.verifyAssertions(muleEvent, messageProcessors);

        verify(mp, times(1)).process(muleEvent);
    }

}
