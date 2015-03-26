/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.notifiers.xml;

import com.thoughtworks.xstream.XStream;

import org.mule.munit.runner.mule.MunitTest;
import org.mule.munit.runner.mule.result.SuiteResult;
import org.mule.munit.runner.mule.result.TestResult;
import org.mule.munit.runner.mule.result.notification.NotificationListener;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class XmlNotificationListener implements NotificationListener
{


    private TestSuite suite;
    private String name;
    private PrintStream out;


    public XmlNotificationListener(String name, PrintStream out)
    {
        this.name = name.replace(".xml", "");
        this.out = out;
        this.suite = new TestSuite(dumpProperties(System.getProperties()), this.name);
    }

    @Override
    public void notifyStartOf(MunitTest test)
    {
    }

    @Override
    public void notify(TestResult testResult)
    {
        TestCase testCase = new TestCase(testResult.getTime(), name, testResult.getTestName());
        testCase.setSkipped(testResult.isSkipped());
        if (testResult.getFailure() != null)
        {
            testCase.setFailure(testResult.getFailure().getFullMessage());
        }
        if (testResult.getError() != null)
        {
            testCase.setError(testResult.getError().getFullMessage());
        }
        suite.add(testCase);
    }

    @Override
    public void notifyIgnored(TestResult testResult) {

    }

    @Override
    public void notifyEnd(SuiteResult result)
    {
        CdataAwareXppDriver xppDriver = new CdataAwareXppDriver();
        XStream xStream = new XStream(xppDriver);
        xStream.autodetectAnnotations(true);
        suite.setErrors(result.getNumberOfErrors());
        suite.setFailures(result.getNumberOfFailures());
        suite.setTests(result.getNumberOfTests());
        suite.setTime(result.getTime());
        suite.setSkipped(result.getNumberOfSkipped());
        out.print(xStream.toXML(suite));
    }

    private List<Property> dumpProperties(Properties properties)
    {
        ArrayList<Property> testProperties = new ArrayList<Property>();
        for (Map.Entry<Object, Object> entry : properties.entrySet())
        {
            testProperties.add(new Property((String) entry.getKey(), (String) entry.getValue()));
        }
        return testProperties;
    }
}
