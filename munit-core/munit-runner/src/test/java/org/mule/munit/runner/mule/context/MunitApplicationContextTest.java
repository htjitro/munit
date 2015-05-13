/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.munit.runner.mule.context;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import org.mule.api.MuleContext;
import org.mule.config.ConfigResource;
import org.mule.config.spring.MissingParserProblemReporter;
import org.mule.modules.interceptor.connectors.ConnectorMethodInterceptorFactory;
import org.mule.munit.common.endpoint.MunitSpringFactoryPostProcessor;
import org.mule.munit.common.mp.MunitMessageProcessorInterceptorFactory;

import java.io.IOException;
import java.net.URL;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.core.io.Resource;

/**
 * @author Mulesoft Inc.
 * @since 3.3.2
 */
public class MunitApplicationContextTest
{


    public static final ConfigResource[] CONFIG_RESOURCES = new ConfigResource[] {};

    private MuleContext muleContext = mock(MuleContext.class);
    private DefaultListableBeanFactory beanFactory = mock(DefaultListableBeanFactory.class);
    private XmlBeanDefinitionReader reader = mock(XmlBeanDefinitionReader.class);

    @Test
    public void createWithNullConfigResource() throws IOException
    {
        MunitApplicationContext appContext = new MockMunitApplicationContext(muleContext, CONFIG_RESOURCES, null);
        appContext.loadBeanDefinitions(beanFactory);

        verify(reader, times(1)).setDocumentReaderClass(MunitBeanDefinitionDocumentReader.class);
        verify(reader, times(1)).setProblemReporter(any(MissingParserProblemReporter.class));
        verify(beanFactory, times(1)).registerBeanDefinition(eq(MunitMessageProcessorInterceptorFactory.ID), any(RootBeanDefinition.class));
        verify(beanFactory, times(1)).registerBeanDefinition(eq(ConnectorMethodInterceptorFactory.ID), any(RootBeanDefinition.class));
    }

    @Test
    public void createWithNotNullConfigResource() throws IOException
    {
        MunitApplicationContext appContext = new MockMunitApplicationContext(muleContext, CONFIG_RESOURCES, new MockingConfiguration(true, null, true, null));
        appContext.loadBeanDefinitions(beanFactory);

        verify(reader, times(1)).setDocumentReaderClass(MunitBeanDefinitionDocumentReader.class);
        verify(reader, times(1)).setProblemReporter(any(MissingParserProblemReporter.class));
        verify(beanFactory, times(1)).registerBeanDefinition(eq(MunitMessageProcessorInterceptorFactory.ID), any(RootBeanDefinition.class));
        verify(beanFactory, times(1)).registerBeanDefinition(eq(ConnectorMethodInterceptorFactory.ID), any(RootBeanDefinition.class));
        verify(beanFactory, times(1)).registerBeanDefinition(eq(MunitApplicationContext.MUNIT_FACTORY_POST_PROCESSOR), argThat(new Matcher<RootBeanDefinition>(){

            @Override
            public boolean matches(Object item)
            {
                RootBeanDefinition beanDefinition = (RootBeanDefinition) item;
                return beanDefinition.getBeanClass().equals(MunitSpringFactoryPostProcessor.class)
                        && beanDefinition.getPropertyValues().contains(MunitApplicationContext.MOCK_INBOUNDS_PROPERTY_NAME)
                        && beanDefinition.getPropertyValues().contains(MunitApplicationContext.MOCKING_EXCLUDED_FLOWS_PROPERTY_NAME)
                        && beanDefinition.getPropertyValues().contains(MunitApplicationContext.MOCK_CONNECTORS_PROPERTY_NAME);
            }

            @Override
            public void _dont_implement_Matcher___instead_extend_BaseMatcher_()
            {

            }

            @Override
            public void describeTo(Description description)
            {

            }
        }));
    }

    class MockMunitApplicationContext extends MunitApplicationContext{

        public MockMunitApplicationContext(MuleContext muleContext, ConfigResource[] configResources, MockingConfiguration configuration) throws BeansException
        {
            super(muleContext, configResources, configuration);
        }

        @Override
        protected XmlBeanDefinitionReader getMunitXmlBeanDefinitionReader(DefaultListableBeanFactory beanFactory)
        {
            return reader;
        }
    }

}
