/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.munit.runner.mule.context;

import org.mule.api.MuleContext;
import org.mule.config.ConfigResource;
import org.mule.config.spring.MissingParserProblemReporter;
import org.mule.config.spring.MuleArtifactContext;
import org.mule.modules.interceptor.connectors.ConnectorMethodInterceptorFactory;
import org.mule.munit.common.endpoint.MunitSpringFactoryPostProcessor;
import org.mule.munit.common.mp.MunitMessageProcessorInterceptorFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionReader;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;

import java.io.IOException;
import java.util.List;
import java.util.Map;


/**
 * <p>
 * The {@link MunitApplicationContext} that represents an app running with Munit.
 * </p>
 * <p/>
 * <p>
 * The main difference between {@link MunitApplicationContext} and {@link MuleArtifactContext} is that
 * it changes the bean definition reader in order to make the Mule stacktrace work and also registers the Bean definition
 * of the {@link MockingConfiguration}
 * </p>
 *
 * @author Mulesoft Inc.
 * @since 3.3.2
 */
public class MunitApplicationContext extends MuleArtifactContext {

    /**
     * <p>
     * The name of the Spring bean that defines the mocking strategy when running JAVA code
     * </p>
     */
    public static final String MUNIT_FACTORY_POST_PROCESSOR = "___MunitSpringFactoryPostProcessor";
    public static final String MOCK_INBOUNDS_PROPERTY_NAME = "mockInbounds";
    public static final String MOCK_CONNECTORS_PROPERTY_NAME = "mockConnectors";
    public static final String MOCKING_EXCLUDED_FLOWS_PROPERTY_NAME = "mockingExcludedFlows";

    /**
     * <p>
     * The {@link MockingConfiguration} for Munit it is null in case of XML test writing.
     * </p>
     */
    private MockingConfiguration configuration;

    public MunitApplicationContext(MuleContext muleContext, ConfigResource[] configResources, MockingConfiguration configuration) throws BeansException {
        super(muleContext, configResources);
        this.configuration = configuration;
    }

    @Override
    protected void loadBeanDefinitions(DefaultListableBeanFactory beanFactory) throws IOException {
//        XmlBeanDefinitionReader beanDefinitionReader = getMunitXmlBeanDefinitionReader(beanFactory);
        XmlBeanDefinitionReader beanDefinitionReader = getMunitXmlBeanDefinitionReader(beanFactory);
        //hook in our custom hierarchical reader
        beanDefinitionReader.setDocumentReaderClass(MunitBeanDefinitionDocumentReader.class);

        //overrite createbeandefinition reader
        // call super of that method in this parent class
        // with the beandefinition reader that returns create a wrapper of this shit
        // be happy and pray


        beanFactory.registerBeanDefinition(MunitMessageProcessorInterceptorFactory.ID, new RootBeanDefinition(MunitMessageProcessorInterceptorFactory.class));
        beanFactory.registerBeanDefinition(ConnectorMethodInterceptorFactory.ID, new RootBeanDefinition(ConnectorMethodInterceptorFactory.class));
        //add error reporting
        beanDefinitionReader.setProblemReporter(new MissingParserProblemReporter());


        beanFactory.getBean(MunitMessageProcessorInterceptorFactory.ID);
        if (configuration != null) {
            RootBeanDefinition beanDefinition = new RootBeanDefinition();
            beanDefinition.setBeanClass(MunitSpringFactoryPostProcessor.class);
            MutablePropertyValues propertyValues = new MutablePropertyValues();
            propertyValues.add(MOCK_INBOUNDS_PROPERTY_NAME, configuration.isMockInbounds());
            propertyValues.add(MOCK_CONNECTORS_PROPERTY_NAME, configuration.isMockConnectors());
            propertyValues.add(MOCKING_EXCLUDED_FLOWS_PROPERTY_NAME, configuration.getMockingExcludedFlows());
            beanDefinition.setPropertyValues(propertyValues);
            beanFactory.registerBeanDefinition(MUNIT_FACTORY_POST_PROCESSOR, beanDefinition);
        }
        // Communicate mule context to parsers

        getCurrentMuleContext().set(this.getMuleContext());
        beanDefinitionReader.loadBeanDefinitions(getConfigResources());

        getCurrentMuleContext().remove();
    }

//    protected MunitXmlBeanDefinitionReader getMunitXmlBeanDefinitionReader(DefaultListableBeanFactory beanFactory) {
    protected XmlBeanDefinitionReader getMunitXmlBeanDefinitionReader(DefaultListableBeanFactory beanFactory) {
        XmlBeanDefinitionReader beanDefinitionReader = (XmlBeanDefinitionReader)createBeanDefinitionReader(beanFactory);
        beanDefinitionReader.setDocumentLoader(new MunitDocumentLoader());
        return beanDefinitionReader;
//        return new MunitXmlBeanDefinitionReader(beanFactory);
    }


    @Override
    protected void prepareBeanFactory(ConfigurableListableBeanFactory beanFactory) {
        super.prepareBeanFactory(beanFactory);

        BeanDefinition beanDefinition = beanFactory.getBeanDefinition(MUNIT_FACTORY_POST_PROCESSOR);
        MutablePropertyValues propertyValues = beanDefinition.getPropertyValues();
        MunitApplicationContextPostProcessor postProcessor = new MunitApplicationContextPostProcessor();
        postProcessor.setMockConnectors((Boolean) propertyValues.getPropertyValue("mockConnectors").getValue());
        postProcessor.setMockInbounds((Boolean) propertyValues.getPropertyValue("mockInbounds").getValue());
        postProcessor.setMockingExcludedFlows((List) propertyValues.getPropertyValue("mockingExcludedFlows").getValue());
        postProcessor.postProcessBeanFactory(beanFactory);

    }


    @Override
    public <T> Map<String, T> getBeansOfType(Class<T> type) throws BeansException {
        return getBeansOfType(type, true, true);
    }


    @Override
    public <T> Map<String, T> getBeansOfType(Class<T> type, boolean includeNonSingletons, boolean allowEagerInit) throws BeansException {
        Map<String, T> result = super.getBeansOfType(type, includeNonSingletons, allowEagerInit);

        if (result.isEmpty()) {
            String[] beanDefinitionNames = super.getBeanDefinitionNames();
            for (String beanDefinitionName : beanDefinitionNames) {
                BeanDefinition beanDefinition = super.getBeanFactory().getBeanDefinition(beanDefinitionName);

                if ("create".equals(beanDefinition.getFactoryMethodName()) && "__messageProcessorEnhancerFactory".equals(beanDefinition.getFactoryBeanName())) {

                    try {
                        Class beanClass = Class.forName(beanDefinition.getBeanClassName());
                        if (type.isAssignableFrom(beanClass)) {
                            result.put(beanDefinitionName, (T) getBean(beanDefinitionName));
                        }
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }

            }
        }

        return result;
    }

}
