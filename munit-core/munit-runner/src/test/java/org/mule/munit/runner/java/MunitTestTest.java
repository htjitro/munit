/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.munit.runner.java;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mule.api.MuleContext;
import org.mule.api.MuleEvent;
import org.mule.api.config.MuleProperties;
import org.mule.api.registry.MuleRegistry;
import org.mule.munit.common.endpoint.MockEndpointManager;
import org.mule.munit.common.mp.MockedMessageProcessorManager;
import org.mule.munit.config.MunitFlow;
import org.mule.munit.config.MunitTestFlow;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

public class MunitTestTest {

    private static final String FLOW_NAME = "flow_name";
    private static final String FLOW_DESCRIPTION = "flow_description";

    private MunitFlow beforeMock = mock(MunitFlow.class);
    private MunitTestFlow flowMock = mock(MunitTestFlow.class);
    private MunitFlow afterMock = mock(MunitFlow.class);
    private MuleContext muleContextMock = mock(MuleContext.class);

    private MuleRegistry muleRegistry = mock(MuleRegistry.class);
    private MockedMessageProcessorManager manager = mock(MockedMessageProcessorManager.class);
    private MockEndpointManager endpointManager = mock(MockEndpointManager.class);


    private List<MunitFlow> beforeList = new ArrayList<MunitFlow>();
    private List<MunitFlow> afterList = new ArrayList<MunitFlow>();

    @Before
    public void setUp() {
        when(flowMock.getMuleContext()).thenReturn(muleContextMock);

        beforeList.add(beforeMock);
        afterList.add(afterMock);
    }


    @Test
    public void testCreation() {
        MunitTest munitTest = new MunitTest(beforeList, flowMock, afterList);

        Assert.assertNotNull("The munit tests should not be null", munitTest);
        verify(flowMock, times(1)).getMuleContext();
    }

    @Test
    public void testGetName() {
        when(flowMock.getName()).thenReturn(FLOW_NAME);

        MunitTest munitTest = new MunitTest(beforeList, flowMock, afterList);

        Assert.assertEquals("The munit tests name is wrong", FLOW_NAME, munitTest.getName());
        verify(flowMock, times(1)).getName();
    }

    @Test
    public void testCountTestsCases() {
        MunitTest munitTest = new MunitTest(beforeList, flowMock, afterList);

        Assert.assertEquals("The test cases count is wrong", 1, munitTest.countTestCases());
    }


    @Test
    public void runTestIsIgnore() throws Throwable {
        when(flowMock.isIgnore()).thenReturn(true);

        MunitTest munitTest = new MunitTest(beforeList, flowMock, afterList);
        munitTest.runTest();

        verify(flowMock, times(1)).isIgnore();
        verify(beforeMock, times(0)).getName();
    }

    @Test
    public void runTest() throws Throwable {
        when(muleContextMock.getRegistry()).thenReturn(muleRegistry);
        when(muleRegistry.lookupObject(MockedMessageProcessorManager.ID)).thenReturn(manager);
        when(muleRegistry.lookupObject(MuleProperties.OBJECT_MULE_ENDPOINT_FACTORY)).thenReturn(endpointManager);


        when(flowMock.getName()).thenReturn(FLOW_NAME);
        when(flowMock.getDescription()).thenReturn(FLOW_DESCRIPTION);
        when(flowMock.isIgnore()).thenReturn(false);

        beforeList.clear();
        afterList.clear();
        MunitTest munitTest = new MunitTest(beforeList, flowMock, afterList);
        munitTest.runTest();

        verify(flowMock, times(1)).isIgnore();
        verify(flowMock, times(1)).process(any(MuleEvent.class));
    }

    @Test
    public void runTestWithBefore() throws Throwable {
        when(muleContextMock.getRegistry()).thenReturn(muleRegistry);
        when(muleRegistry.lookupObject(MockedMessageProcessorManager.ID)).thenReturn(manager);
        when(muleRegistry.lookupObject(MuleProperties.OBJECT_MULE_ENDPOINT_FACTORY)).thenReturn(endpointManager);


        when(flowMock.getName()).thenReturn(FLOW_NAME);
        when(flowMock.getDescription()).thenReturn(FLOW_DESCRIPTION);
        when(flowMock.isIgnore()).thenReturn(false);

        when(beforeMock.getName()).thenReturn(FLOW_NAME);
        when(beforeMock.getDescription()).thenReturn(FLOW_DESCRIPTION);


        afterList.clear();
        MunitTest munitTest = new MunitTest(beforeList, flowMock, afterList);
        munitTest.runTest();


        verify(flowMock, times(1)).isIgnore();
        verify(flowMock, times(1)).process(any(MuleEvent.class));
        verify(beforeMock, times(1)).process(any(MuleEvent.class));
    }

    @Test
    public void runTestWithAfter() throws Throwable {
        when(muleContextMock.getRegistry()).thenReturn(muleRegistry);
        when(muleRegistry.lookupObject(MockedMessageProcessorManager.ID)).thenReturn(manager);
        when(muleRegistry.lookupObject(MuleProperties.OBJECT_MULE_ENDPOINT_FACTORY)).thenReturn(endpointManager);


        when(flowMock.getName()).thenReturn(FLOW_NAME);
        when(flowMock.getDescription()).thenReturn(FLOW_DESCRIPTION);
        when(flowMock.isIgnore()).thenReturn(false);

        when(afterMock.getName()).thenReturn(FLOW_NAME);
        when(afterMock.getDescription()).thenReturn(FLOW_DESCRIPTION);


        beforeList.clear();
        MunitTest munitTest = new MunitTest(beforeList, flowMock, afterList);
        munitTest.runTest();


        verify(flowMock, times(1)).isIgnore();
        verify(flowMock, times(1)).process(any(MuleEvent.class));
        verify(afterMock, times(1)).process(any(MuleEvent.class));
    }

    @Test(expected = RuntimeException.class)
    public void runTestThrowException() throws Throwable {
        when(muleContextMock.getRegistry()).thenReturn(muleRegistry);
        when(muleRegistry.lookupObject(MockedMessageProcessorManager.ID)).thenReturn(manager);
        when(muleRegistry.lookupObject(MuleProperties.OBJECT_MULE_ENDPOINT_FACTORY)).thenReturn(endpointManager);


        when(flowMock.getName()).thenReturn(FLOW_NAME);
        when(flowMock.getDescription()).thenReturn(FLOW_DESCRIPTION);
        when(flowMock.isIgnore()).thenReturn(false);

        when(afterMock.getName()).thenReturn(FLOW_NAME);
        when(afterMock.getDescription()).thenReturn(FLOW_DESCRIPTION);

        when(flowMock.process(any(MuleEvent.class))).thenThrow(new RuntimeException());

        when(flowMock.expectException(any(Throwable.class), any(MuleEvent.class))).thenReturn(false);

        beforeList.clear();
        MunitTest munitTest = new MunitTest(beforeList, flowMock, afterList);
        munitTest.runTest();


        verify(flowMock, times(1)).isIgnore();
        verify(flowMock, times(1)).process(any(MuleEvent.class));
        verify(flowMock, times(1)).expectException(any(Throwable.class), any(MuleEvent.class));

        verify(afterMock, times(1)).process(any(MuleEvent.class));
    }

    @Test
    public void runTestThrowExpectedException() throws Throwable {
        when(muleContextMock.getRegistry()).thenReturn(muleRegistry);
        when(muleRegistry.lookupObject(MockedMessageProcessorManager.ID)).thenReturn(manager);
        when(muleRegistry.lookupObject(MuleProperties.OBJECT_MULE_ENDPOINT_FACTORY)).thenReturn(endpointManager);


        when(flowMock.getName()).thenReturn(FLOW_NAME);
        when(flowMock.getDescription()).thenReturn(FLOW_DESCRIPTION);
        when(flowMock.isIgnore()).thenReturn(false);

        when(afterMock.getName()).thenReturn(FLOW_NAME);
        when(afterMock.getDescription()).thenReturn(FLOW_DESCRIPTION);

        when(flowMock.process(any(MuleEvent.class))).thenThrow(new RuntimeException());

        when(flowMock.expectException(any(Throwable.class), any(MuleEvent.class))).thenReturn(true);

        beforeList.clear();
        MunitTest munitTest = new MunitTest(beforeList, flowMock, afterList);
        munitTest.runTest();


        verify(flowMock, times(1)).isIgnore();
        verify(flowMock, times(1)).process(any(MuleEvent.class));
        verify(flowMock, times(1)).expectException(any(Throwable.class), any(MuleEvent.class));

        verify(afterMock, times(1)).process(any(MuleEvent.class));
    }


}
