/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.java;


import org.junit.Test;
import org.mule.api.MuleContext;
import org.mule.munit.MunitPollManager;
import org.mule.munit.runner.functional.FunctionalMunitSuite;

import java.util.Arrays;

public class PollTest extends FunctionalMunitSuite {

    @Override
    protected void muleContextCreated(MuleContext muleContext)
    {
        MunitPollManager.instance(muleContext).avoidPollLaunch();
    }

    @Override
    protected boolean haveToDisableInboundEndpoints()
    {
        return false;
    }

    @Override
    protected boolean haveToMockMuleConnectors()
    {
        return false;
    }

    @Test
    public void test() throws Exception {
        whenMessageProcessor("create-issue")
                .ofNamespace("jira")
                .thenReturn(muleMessageWithPayload("OK"));

        runFlow("jira-mock-exampleFlow", testEvent(Arrays.asList("Something"))).getMessage().getPayload();

        verifyCallOfMessageProcessor("create-issue").ofNamespace("jira").times(1);
    }
}
