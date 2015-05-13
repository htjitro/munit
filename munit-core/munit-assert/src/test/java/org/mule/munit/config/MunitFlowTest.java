/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.munit.config;

import org.junit.Test;

import org.mule.api.MuleContext;
import org.mule.api.config.MuleConfiguration;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Mulesoft Inc.
 * @since 3.3.2
 */
public class MunitFlowTest
{

    MuleContext muleContext = mock(MuleContext.class);

    @Test
    public void testFlowDescription()
    {
        MuleConfiguration muleConfiguration = mock(MuleConfiguration.class);
        when(muleConfiguration.getDefaultProcessingStrategy()).thenReturn(null);
        when(muleContext.getConfiguration()).thenReturn(muleConfiguration);

        MunitFlow flow = new MunitFlow("name", muleContext);
        flow.setDescription("my Description");

        assertEquals("my Description", flow.getDescription());
    }
}
