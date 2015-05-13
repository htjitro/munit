/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.munit.runner.mule.context;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;

/**
 * This class is not needed anymore
 */
@Deprecated
public class MunitXmlBeanDefinitionReader /*extends XmlBeanDefinitionReader*/ {

    /**
     * Create new XmlBeanDefinitionReader for the given bean factory.
     *
     * @param registry the BeanFactory to load bean definitions into,
     *                 in the form of a BeanDefinitionRegistry
     */
    public MunitXmlBeanDefinitionReader(BeanDefinitionRegistry registry) {
//        super(registry);
//        setDocumentLoader(new MunitDocumentLoader());
    }
}
