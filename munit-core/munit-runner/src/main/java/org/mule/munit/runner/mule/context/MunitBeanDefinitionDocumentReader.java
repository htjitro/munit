/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.munit.runner.mule.context;

import org.mule.config.spring.MuleBeanDefinitionDocumentReader;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.beans.factory.xml.XmlReaderContext;
import org.w3c.dom.Element;

/**
 * <p>
 * The {@link MunitBeanDefinitionDocumentReader} that overrides {@link MuleBeanDefinitionDocumentReader} to create
 * a {@link MunitBeanDefinitionParserDelegate}
 * </p>
 *
 * @author Mulesoft Inc.
 * @since 3.3.2
 */
public class MunitBeanDefinitionDocumentReader extends MuleBeanDefinitionDocumentReader {

    private BeanDefinitionParserDelegate doCreateParserDelegate(XmlReaderContext readerContext, Element root, BeanDefinitionParserDelegate parentDelegate) {
        BeanDefinitionParserDelegate delegate = new MunitBeanDefinitionParserDelegate(readerContext, this);
        delegate.initDefaults(root, parentDelegate);
        return delegate;
    }

    @Override
    protected BeanDefinitionParserDelegate createHelper(XmlReaderContext readerContext, Element root) {
        BeanDefinitionParserDelegate delegate = new MunitBeanDefinitionParserDelegate(readerContext, this);
        delegate.initDefaults(root);
        return delegate;
    }

    @Override
    protected BeanDefinitionParserDelegate createDelegate(XmlReaderContext readerContext, Element root, BeanDefinitionParserDelegate parentDelegate) {
        return doCreateParserDelegate(readerContext, root, parentDelegate);
    }

    /* We maintain this in the event munit can be fully backward compatible */
    protected BeanDefinitionParserDelegate createHelper(XmlReaderContext readerContext, Element root, BeanDefinitionParserDelegate parentDelegate) {
        return doCreateParserDelegate(readerContext, root, parentDelegate);
    }

}
