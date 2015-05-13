/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.java;

import junit.framework.Assert;
import org.junit.Test;
import org.mule.munit.IntialisableBean;
import org.mule.munit.runner.functional.FunctionalMunitSuite;

import java.util.Collection;


public class InicialisableBeansMunitTest extends FunctionalMunitSuite {

    private static final String INITIALISABLE_BEAN_ID = "myInitialisableBean";

    @Override
    protected String getConfigResources() {
        return "mule-inisialisable-bean.xml";
    }

    @Test
    public void test() throws Exception {
        IntialisableBean bean = (IntialisableBean) muleContext.getRegistry().get(INITIALISABLE_BEAN_ID);

        Collection c = muleContext.getRegistry().lookupObjects(IntialisableBean.CLASS_TO_LOOKUP);

        Assert.assertEquals(2, bean.getCallsToInitialise());
        Assert.assertNotNull(c);
        Assert.assertTrue(c.size()!= 0);;
//        System.out.println("munit lalalal ---: " + c.size());
    }


}
