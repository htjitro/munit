/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.munit.runner.mule.context;


import org.junit.Test;
import org.mockito.Mockito;
import org.mule.module.xml.transformer.DomDocumentToXml;
import org.mule.module.xml.transformer.XmlToDomDocument;
import org.mule.routing.Foreach;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;

public class MunitBeanDefinitionScopperTest {

    @Test
    public void testScopeOutForEach() throws ClassNotFoundException {
        BeanDefinition beanDefinitionMock = Mockito.mock(BeanDefinition.class);
        Mockito.when(beanDefinitionMock.getBeanClassName()).thenReturn(Foreach.class.getCanonicalName());

        MunitBeanDefinitionScopper.makeBeanDefinitionSingletonIfApplicable(beanDefinitionMock);

        Mockito.verify(beanDefinitionMock, Mockito.times(1)).setScope(ConfigurableBeanFactory.SCOPE_SINGLETON);
    }


    @Test
    public void testScopeOutDomDocumentToXml() throws ClassNotFoundException {
        BeanDefinition beanDefinitionMock = Mockito.mock(BeanDefinition.class);
        Mockito.when(beanDefinitionMock.getBeanClassName()).thenReturn(DomDocumentToXml.class.getCanonicalName());

        MunitBeanDefinitionScopper.makeBeanDefinitionSingletonIfApplicable(beanDefinitionMock);

        Mockito.verify(beanDefinitionMock, Mockito.times(1)).setScope(ConfigurableBeanFactory.SCOPE_SINGLETON);
    }



    @Test
    public void testScopeOutXmlToDomDocument() throws ClassNotFoundException {
        BeanDefinition beanDefinitionMock = Mockito.mock(BeanDefinition.class);
        Mockito.when(beanDefinitionMock.getBeanClassName()).thenReturn(XmlToDomDocument.class.getCanonicalName());

        MunitBeanDefinitionScopper.makeBeanDefinitionSingletonIfApplicable(beanDefinitionMock);

        Mockito.verify(beanDefinitionMock, Mockito.times(1)).setScope(ConfigurableBeanFactory.SCOPE_SINGLETON);
    }
}
