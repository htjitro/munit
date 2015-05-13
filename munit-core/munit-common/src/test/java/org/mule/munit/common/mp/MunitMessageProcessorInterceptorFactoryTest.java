/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.munit.common.mp;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.mule.api.MuleContext;
import org.mule.api.config.MuleConfiguration;
import org.mule.api.processor.LoggerMessageProcessor;
import org.mule.component.simple.EchoComponent;
import org.mule.config.spring.factories.FlowRefFactoryBean;
import org.mule.construct.Flow;
import org.mule.modules.interceptor.processors.MessageProcessorId;

import java.util.HashMap;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import org.junit.Test;
import org.mule.munit.util.EchoComponentWithPrimitiveTypeInConstructor;
import org.mule.munit.util.NotExtensibleEchoComponent;
import org.mule.munit.util.NotExtensibleEchoComponentWithConstructor;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * @author Mulesoft Inc.
 * @since 3.3.2
 */
public class MunitMessageProcessorInterceptorFactoryTest
{

    private MuleContext context = mock(MuleContext.class);

    @Test
    public void testIdIsCorrect()
    {
        assertEquals("__messageProcessorEnhancerFactory", MunitMessageProcessorInterceptorFactory.ID);
    }

    @Test
    public void testFactoryBeanNameAndMethod()
    {
        RootBeanDefinition beanDefinition = new RootBeanDefinition();
        MunitMessageProcessorInterceptorFactory.addFactoryDefinitionTo(beanDefinition);

        assertEquals("create", beanDefinition.getFactoryMethodName());
        assertEquals(MunitMessageProcessorInterceptorFactory.ID, beanDefinition.getFactoryBeanName());
    }

    @Test
    public void createCorrectInterceptor()
    {
        MethodInterceptor interceptor = new MunitMessageProcessorInterceptorFactory().createInterceptor();
        assertTrue(interceptor instanceof MunitMessageProcessorInterceptor);
    }


    @Test
    public void testCreate()
    {
        MunitMessageProcessorInterceptorFactory factory = new MunitMessageProcessorInterceptorFactory();

        factory.create(EchoComponent.class, new MessageProcessorId("name", "namespace"), new HashMap<String, String>(), "fileName", "2");
    }

    @Test
    public void testCreateLogger()
    {
        MunitMessageProcessorInterceptorFactory factory = new MunitMessageProcessorInterceptorFactory();

        factory.create(LoggerMessageProcessor.class, new MessageProcessorId("name", "namespace"), new HashMap<String, String>(), "fileName", "2");
    }

    @Test
    public void testCreateWithTwoConstructorArguments()
    {
        MuleConfiguration muleConfiguration = mock(MuleConfiguration.class);
        when(muleConfiguration.getDefaultProcessingStrategy()).thenReturn(null);
        when(context.getConfiguration()).thenReturn(muleConfiguration);


        MunitMessageProcessorInterceptorFactory factory = new MunitMessageProcessorInterceptorFactory();

        Object o = factory.create(Flow.class, new MessageProcessorId("name", "namespace"), new HashMap<String, String>(), "fileName", "2", "flowName", context);

        assertTrue(Enhancer.isEnhanced(o.getClass()));
    }

    @Test(expected = java.lang.Error.class)
    public void testCreateWithOneConstructorArgumentsButFail()
    {
        MunitMessageProcessorInterceptorFactory factory = new MunitMessageProcessorInterceptorFactory();

        factory.create(Flow.class, new MessageProcessorId("name", "namespace"), new HashMap<String, String>(), "fileName", "2", "flowName");
    }



    @Test
    public void testCreateNotExtensibleMpWithNoDefaultConstructor(){
        MunitMessageProcessorInterceptorFactory factory = new MunitMessageProcessorInterceptorFactory();

        Object o = factory.create(NotExtensibleEchoComponentWithConstructor.class, new MessageProcessorId("name", "namespace"), new HashMap<String, String>(), "fileName", "2", "flowName");

        assertFalse(Enhancer.isEnhanced(o.getClass()));
    }

    @Test
    public void testCreateNotExtensibleMpWithDefaultConstructor(){
        MunitMessageProcessorInterceptorFactory factory = new MunitMessageProcessorInterceptorFactory();

        Object o = factory.create(NotExtensibleEchoComponent.class, new MessageProcessorId("name", "namespace"), new HashMap<String, String>(), "fileName", "2", "flowName");

        assertFalse(Enhancer.isEnhanced(o.getClass()));
    }

    @Test
    public void testCreateWithPrimitiveTypesInConstructor(){
        MunitMessageProcessorInterceptorFactory factory = new MunitMessageProcessorInterceptorFactory();

        Object o = factory.create(EchoComponentWithPrimitiveTypeInConstructor.class, new MessageProcessorId("name", "namespace"), new HashMap<String, String>(), "fileName", "2", 3);

        assertTrue(Enhancer.isEnhanced(o.getClass()));
    }

    @Test(expected = Error.class)
    public void testCreateWithOneConstructorArgumentsButFailAndThenCreateRealFail(){
        MunitMessageProcessorInterceptorFactory factory = new MunitMessageProcessorInterceptorFactory();

        factory.create(EchoComponentWithPrimitiveTypeInConstructor.class, new MessageProcessorId("name", "namespace"), new HashMap<String, String>(), "fileName", "2", new Boolean(false));
    }

    @Test
    public void testCreateWithOneConstructorArgumentsButFailAndThenCreateReal()
    {
        MunitMessageProcessorInterceptorFactory factory = new MunitMessageProcessorInterceptorFactory();

        Object o = factory.create(EchoComponent.class, new MessageProcessorId("name", "namespace"), new HashMap<String, String>(), "fileName", "2", "Error");

        assertFalse(Enhancer.isEnhanced(o.getClass()));
    }

    @Test
    public void failAndCreateTheRealOne()
    {
        MunitMessageProcessorInterceptorFactory factory = new MunitMessageProcessorInterceptorFactory();

        Object o = factory.create(EchoComponent.class, new MessageProcessorId("name", "namespace"), new HashMap<String, String>(), "fileName", "2", "Error");

        assertFalse(Enhancer.isEnhanced(o.getClass()));
    }

    @Test
    public void createAFactoryBean()
    {
        MunitMessageProcessorInterceptorFactory factory = new MunitMessageProcessorInterceptorFactory();

        Object o = factory.create(FlowRefFactoryBean.class, new MessageProcessorId("name", "namespace"), new HashMap<String, String>(), "fileName", "2");

        assertTrue(Enhancer.isEnhanced(o.getClass()));
    }

    @Test(expected = java.lang.Error.class)
    public void failCreatingMockWithoutConstructor()
    {
        MunitMessageProcessorInterceptorFactory factory = new MunitMessageProcessorInterceptorFactory();

        factory.create(Flow.class, new MessageProcessorId("name", "namespace"), new HashMap<String, String>(), "fileName", "2");
    }


    @Test
    public void createWithEmptyConstructors()
    {
        MunitMessageProcessorInterceptorFactory factory = new MunitMessageProcessorInterceptorFactory();

        Object o = factory.create(EchoComponent.class, new MessageProcessorId("name", "namespace"), new HashMap<String, String>(), "fileName", "2", new Object[]{});

        assertTrue(Enhancer.isEnhanced(o.getClass()));

    }

}
