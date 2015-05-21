/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.munit.runner.mule.context;

import org.apache.commons.lang.StringUtils;
import org.mule.api.processor.MessageProcessor;
import org.mule.module.xml.transformer.AbstractXmlTransformer;
import org.mule.modules.interceptor.processors.MessageProcessorId;
import org.mule.munit.common.MunitCore;
import org.mule.munit.common.mp.MunitMessageProcessorInterceptorFactory;
import org.mule.routing.Foreach;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.xml.NamespaceHandler;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.util.HashMap;
import java.util.Map;

public class MunitHandlerWrapper implements NamespaceHandler {

    private NamespaceHandler realHandler;

    public MunitHandlerWrapper(NamespaceHandler realHandler) {
        this.realHandler = realHandler;
    }

    @Override
    public void init() {
    }

    @Override
    public BeanDefinition parse(Element element, ParserContext parserContext) {
        BeanDefinition beanDefinition = realHandler.parse(element, parserContext);
        if (beanDefinition == null || element == null) {
            return beanDefinition;
        }

        try {
            Class<?> beanType = Class.forName(beanDefinition.getBeanClassName());
            if (byPassDueToException(beanType)) {
                return beanDefinition;
            }

            if (isMessageProcessor(beanType)
                    && AbstractBeanDefinition.class.isAssignableFrom(beanDefinition.getClass())
                    && StringUtils.isEmpty(beanDefinition.getFactoryMethodName())) {
                String tagName = element.getTagName();

                if (!StringUtils.isEmpty(tagName) && beanDefinition.getConstructorArgumentValues().getArgumentCount() <= 5) {
                    String filename = parserContext.getReaderContext().getResource().getFilename();

                    MunitMessageProcessorInterceptorFactory.addFactoryDefinitionTo((AbstractBeanDefinition) beanDefinition)
                            .withConstructorArguments(beanType, new MessageProcessorId(getNameFrom(tagName), getNamespaceFrom(tagName)),
                                    getAttributes(element), filename, element.getAttribute(MunitCore.LINE_NUMBER_ELEMENT_ATTRIBUTE));

                    MunitBeanDefinitionScopper.makeBeanDefinitionSingletonIfApplicable(beanDefinition);

                    return beanDefinition;
                }
            }


        } catch (ClassNotFoundException e) {
            return beanDefinition;
        }

        return beanDefinition;
    }


    /**
     * Due to certain code in the ESB core that's unstable we are force to by pass some beans.
     * This will cause verifications to fail and coverage problems.
     * <p/>
     * List of Beans we by pass:
     * - Extensions API beans
     * <p/>
     * SINCE 3.7.0
     * TODO: FIX MU-266
     *
     * @param beanType
     * @return
     */
    private boolean byPassDueToException(Class<?> beanType) {
        if ("org.mule.module.extension.internal.config.ConfigurationInstanceProviderFactoryBean".equals(beanType.getCanonicalName())) {
            return true;
        }
        return false;
    }

    private Map<String, String> getAttributes(Element element) {
        Map<String, String> attrs = new HashMap<String, String>();
        NamedNodeMap attributes = element.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node attr = attributes.item(i);
            if (!MunitCore.LINE_NUMBER_ELEMENT_ATTRIBUTE.equals(attr.getNodeName())) {
                attrs.put(attr.getNodeName(), attr.getNodeValue());
            }
        }
        return attrs;
    }

    private String getNameFrom(String tagName) {
        String[] splitedName = tagName.split(":");
        if (splitedName.length == 1) {
            return splitedName[0];
        } else if (splitedName.length > 1) {
            return splitedName[1];
        }

        return "";
    }

    private String getNamespaceFrom(String tagName) {
        String[] splitedName = tagName.split(":");
        if (splitedName.length > 1) {
            return splitedName[0];
        }

        return "mule";
    }

    private boolean isMessageProcessor(Class<?> beanType) {
        return MessageProcessor.class.isAssignableFrom(beanType)
                || FactoryBean.class.isAssignableFrom(beanType);
    }

    @Override
    public BeanDefinitionHolder decorate(Node source, BeanDefinitionHolder definition, ParserContext parserContext) {
        return definition;
    }
}
