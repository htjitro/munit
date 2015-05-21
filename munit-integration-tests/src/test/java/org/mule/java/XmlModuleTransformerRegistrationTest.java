/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.java;

import junit.framework.Assert;
import org.junit.Test;
import org.mule.api.MessagingException;
import org.mule.api.MuleEvent;
import org.mule.munit.runner.functional.FunctionalMunitSuite;

import org.mule.api.transformer.TransformerException;

/**
 * The goal of this test is to just trigger a exception during the run of the test.
 * The scenario tries to produce two transformers to be registered as a side effect of the munit test being run.
 * <p/>
 * If such exception is never risen we consider the test to pass.
 */
public class XmlModuleTransformerRegistrationTest extends FunctionalMunitSuite {

    protected String getConfigResources() {
        return "xml-module-transformer-registration.xml";
    }

    @Test
    public void validateNoDuplicateTransformerGetsRegisteredTest() throws Exception {
        String testPayload = "<tests><test><nested/><nested/></test><test><nested/><nested/></test></tests>";
        MuleEvent testEvent = null;
        try {
            testEvent = testEvent(testPayload);
            runFlow("transformerException", testEvent);
            System.out.print("asdsaa");
        } catch (MessagingException e) {
            if (e.getCause().getClass().isAssignableFrom(TransformerException.class)) {
                Assert.fail(e.getCause().getMessage());
            }
        } catch (Exception e) {
            throw e;
        }

    }

}
