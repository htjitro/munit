/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.munit;

import org.mule.munit.runner.java.AbstractMuleSuite;


public class ValidationModuleTest extends AbstractMuleSuite
{

    @Override
    public String getConfigResources()
    {
        return "validation-module-test.xml";
    }
}
