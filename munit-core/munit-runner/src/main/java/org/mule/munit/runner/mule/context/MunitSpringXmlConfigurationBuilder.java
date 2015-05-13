/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.munit.runner.mule.context;

import org.mule.api.MuleContext;
import org.mule.api.config.ConfigurationException;
import org.mule.config.ConfigResource;
import org.mule.config.spring.*;

import org.springframework.context.ApplicationContext;

/**
 * <p>
 * Override of the {@link SpringXmlConfigurationBuilder} for Munit. It just overrides the creation of the
 * {@link ApplicationContext} in order to create an {@link MunitApplicationContext}
 * </p>
 *
 * @author Mulesoft Inc.
 * @since 3.3.2
 */
public class MunitSpringXmlConfigurationBuilder extends SpringXmlConfigurationBuilder
{

    private MockingConfiguration configuration;

    public MunitSpringXmlConfigurationBuilder(String configResources, MockingConfiguration configuration) throws ConfigurationException
    {
        super(configResources);
        this.configuration = configuration;
    }

    protected ApplicationContext doCreateApplicationContext(MuleContext muleContext, ConfigResource[] configResources, OptionalObjectsController optionalObjectsController) {
        return new MunitApplicationContext(muleContext, configResources, configuration);
    }
}
