/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.munit.runner.mule.context;


import org.mule.module.xml.transformer.AbstractXmlTransformer;
import org.mule.routing.Foreach;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;

public class MunitBeanDefinitionScopper {

    public static void makeBeanDefinitionSingletonIfApplicable(BeanDefinition beanDefinition) throws ClassNotFoundException {
        scopeOutFlowControl(beanDefinition);
        scopeOutSpecialBeans(beanDefinition);
    }

    protected static void scopeOutFlowControl(BeanDefinition beanDefinition) throws ClassNotFoundException {
        Class<?> beanType = Class.forName(beanDefinition.getBeanClassName());

        if (Foreach.class.isAssignableFrom(beanType)) {
            beanDefinition.setScope(ConfigurableBeanFactory.SCOPE_SINGLETON);
        }
    }

    protected static void scopeOutSpecialBeans(BeanDefinition beanDefinition) throws ClassNotFoundException {
        Class<?> beanType = Class.forName(beanDefinition.getBeanClassName());

        if (AbstractXmlTransformer.class.isAssignableFrom(beanType)) {
            beanDefinition.setScope(ConfigurableBeanFactory.SCOPE_SINGLETON);
        }
    }
}
