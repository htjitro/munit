/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.munit.config;

import org.junit.Before;
import org.junit.Test;
import org.mule.api.MessagingException;
import org.mule.api.MuleContext;
import org.mule.api.MuleEvent;
import org.mule.api.config.MuleConfiguration;
import org.mule.api.expression.ExpressionManager;
import org.mule.api.registry.MuleRegistry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Mulesoft Inc.
 * @since 3.3.2
 */
public class MunitTestFlowTest {

    public static final String EXPECTED = "expected";
    MuleContext muleContext = mock(MuleContext.class);
    MuleRegistry registry = mock(MuleRegistry.class);
    private MuleEvent muleEvent = mock(MuleEvent.class);
    private ExpressionManager expressionManager = mock(ExpressionManager.class);


    @Before
    public void setUp() {
        when(muleContext.getRegistry()).thenReturn(registry);
        when(muleContext.getExpressionManager()).thenReturn(expressionManager);

    }

    @Test
    public void testSetters()

    {
        MuleConfiguration muleConfiguration = mock(MuleConfiguration.class);
        when(muleConfiguration.getDefaultProcessingStrategy()).thenReturn(null);
        when(muleContext.getConfiguration()).thenReturn(muleConfiguration);

        MunitTestFlow testFlow = new MunitTestFlow("name", muleContext);

        testFlow.setExpectExceptionThatSatisfies(EXPECTED);
        testFlow.setIgnore(true);

        assertTrue(testFlow.isIgnore());
        assertEquals(EXPECTED, testFlow.getExpectExceptionThatSatisfies());
    }

    @Test
    public void testExceptionWhenMatchesExpression() {


        MuleConfiguration muleConfiguration = mock(MuleConfiguration.class);
        when(muleConfiguration.getDefaultProcessingStrategy()).thenReturn(null);
        when(muleContext.getConfiguration()).thenReturn(muleConfiguration);

        MunitTestFlow testFlow = new MunitTestFlow("name", muleContext);
        testFlow.setExpectExceptionThatSatisfies(EXPECTED);

        when(expressionManager.isExpression(EXPECTED)).thenReturn(true);
        when(expressionManager.evaluate(EXPECTED, muleEvent)).thenReturn(true);

        assertTrue(testFlow.expectException(new Exception(), muleEvent));
    }

    @Test(expected = junit.framework.AssertionFailedError.class)
    public void testExceptionWhenDoesntMatchExpression() {

        MuleConfiguration muleConfiguration = mock(MuleConfiguration.class);
        when(muleConfiguration.getDefaultProcessingStrategy()).thenReturn(null);
        when(muleContext.getConfiguration()).thenReturn(muleConfiguration);

        MunitTestFlow testFlow = new MunitTestFlow("name", muleContext);
        testFlow.setExpectExceptionThatSatisfies(EXPECTED);

        when(expressionManager.isExpression(EXPECTED)).thenReturn(true);
        when(expressionManager.evaluate(EXPECTED, muleEvent)).thenReturn(false);

        testFlow.expectException(new Exception(), muleEvent);
    }

    @Test
    public void testExceptionWheIsNotExpressionButMatchesName() {

        MuleConfiguration muleConfiguration = mock(MuleConfiguration.class);
        when(muleConfiguration.getDefaultProcessingStrategy()).thenReturn(null);
        when(muleContext.getConfiguration()).thenReturn(muleConfiguration);

        MunitTestFlow testFlow = new MunitTestFlow("name", muleContext);
        testFlow.setExpectExceptionThatSatisfies(Exception.class.getCanonicalName());

        when(expressionManager.isExpression(EXPECTED)).thenReturn(false);

        assertTrue(testFlow.expectException(new Exception(), muleEvent));
    }

    @Test(expected = junit.framework.AssertionFailedError.class)
    public void testExceptionWheIsNotExpressionAndDoesntMatchName() {

        MuleConfiguration muleConfiguration = mock(MuleConfiguration.class);
        when(muleConfiguration.getDefaultProcessingStrategy()).thenReturn(null);
        when(muleContext.getConfiguration()).thenReturn(muleConfiguration);

        MunitTestFlow testFlow = new MunitTestFlow("name", muleContext);
        testFlow.setExpectExceptionThatSatisfies("any");

        when(expressionManager.isExpression(EXPECTED)).thenReturn(false);

        testFlow.expectException(new Exception(), muleEvent);
    }

    @Test(expected = junit.framework.AssertionFailedError.class)
    public void testMessagingWithNoCauseException() {

        MuleConfiguration muleConfiguration = mock(MuleConfiguration.class);
        when(muleConfiguration.getDefaultProcessingStrategy()).thenReturn(null);
        when(muleContext.getConfiguration()).thenReturn(muleConfiguration);

        MunitTestFlow testFlow = new MunitTestFlow("name", muleContext);
        testFlow.setExpectExceptionThatSatisfies("any");

        when(expressionManager.isExpression(EXPECTED)).thenReturn(false);

        testFlow.expectException(new MessagingException(muleEvent, null), muleEvent);
    }

    @Test(expected = junit.framework.AssertionFailedError.class)
    public void testMessagingWithCauseExceptionThatDoesntMatchTheName() {

        MuleConfiguration muleConfiguration = mock(MuleConfiguration.class);
        when(muleConfiguration.getDefaultProcessingStrategy()).thenReturn(null);
        when(muleContext.getConfiguration()).thenReturn(muleConfiguration);

        MunitTestFlow testFlow = new MunitTestFlow("name", muleContext);
        testFlow.setExpectExceptionThatSatisfies("any");

        when(expressionManager.isExpression(EXPECTED)).thenReturn(false);

        testFlow.expectException(new MessagingException(muleEvent, new Exception()), muleEvent);
    }

    @Test
    public void testMessagingWithCauseExceptionThattMatchesTheName() {

        MuleConfiguration muleConfiguration = mock(MuleConfiguration.class);
        when(muleConfiguration.getDefaultProcessingStrategy()).thenReturn(null);
        when(muleContext.getConfiguration()).thenReturn(muleConfiguration);

        MunitTestFlow testFlow = new MunitTestFlow("name", muleContext);
        testFlow.setExpectExceptionThatSatisfies(Exception.class.getCanonicalName());

        when(expressionManager.isExpression(EXPECTED)).thenReturn(false);

        testFlow.expectException(new MessagingException(muleEvent, new Exception()), muleEvent);
    }


}
