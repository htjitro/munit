/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.munit.runner.mule.context;


import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import org.mule.api.MuleContext;
import org.mule.config.ConfigResource;

import org.junit.Test;

/**
 * @author Mulesoft Inc.
 * @since 3.4
 */
public class MunitSpringXmlConfigurationBuilderTest
{

    private MockingConfiguration configuration = mock(MockingConfiguration.class);
    private MuleContext muleContext = mock(MuleContext.class);

    @Test
    public void testInstanceCreation() throws Exception
    {
        MunitSpringXmlConfigurationBuilder builder = new MunitSpringXmlConfigurationBuilder("munit-config.xml", configuration);
        assertTrue(builder.doCreateApplicationContext(muleContext, new ConfigResource[]{},null) instanceof MunitApplicationContext);
    }
}
