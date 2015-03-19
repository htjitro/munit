package org.mule.munit.runner.remote;

import junit.framework.Assert;
import org.junit.Test;

/**
 * @author Mulesoft Inc.
 * @since 3.6.0
 */
public class MessageBuilderTest {

    @Test
    public void testBuildNumberOfTestsMessage() {
        String numberOfTest = "4";
        String expected = MessageBuilder.NUMBER_OF_TESTS_MSG_ID + numberOfTest;

        String message = MessageBuilder.buildNuberOfTestsMessage(numberOfTest);

        Assert.assertEquals("The Message is wrong!", expected, message);
    }

    @Test
    public void testBuildNewTestMessage() {
        String testName = "a_test_name";
        String expected = MessageBuilder.NEW_TEST_MSG_ID + testName;
        String message = MessageBuilder.buildNewTestMessage(testName);

        Assert.assertEquals("The Message is wrong!", expected, message);
    }

    @Test
    public void testBuildTestErroMessage() {
        String testName = "a_test_name";
        String fullMessage = "something_something_dark_sideException()";
        String expected = MessageBuilder.TEST_ERROR_MSG_ID + testName + MessageBuilder.MESSAGE_TOKEN_SEPARATOR + MessageBuilder.STACK_TRACE_MARKER + fullMessage + MessageBuilder.STACK_TRACE_MARKER;

        String message = MessageBuilder.buildTestErroMessage(testName, fullMessage);

        Assert.assertEquals("The Message is wrong!", expected, message);
    }

    @Test
    public void testBuildTestFailureMessage() {
        String testName = "a_test_name";
        String fullMessage = "something_something_dark_sideException()";
        String expected = MessageBuilder.TEST_FAILURE_MSG_ID + testName + MessageBuilder.MESSAGE_TOKEN_SEPARATOR + MessageBuilder.STACK_TRACE_MARKER + fullMessage + MessageBuilder.STACK_TRACE_MARKER;

        String message = MessageBuilder.buildTestFailureMessage(testName, fullMessage);

        Assert.assertEquals("The Message is wrong!", expected, message);
    }

    @Test
    public void testBuildTestFinishedMessage() {
        String testName = "a_test_name";
        String expected = MessageBuilder.TEST_FINSHED_MSG_ID + testName;

        String message = MessageBuilder.buildTestFinishedMessage(testName);

        Assert.assertEquals("The Message is wrong!", expected, message);
    }

    @Test
    public void testBuildTestRunFinishedMessage() {
        String path = "src/test/munit/a_test_name_suiite.xml";
        String expected = MessageBuilder.TEST_RUN_FINSHED_MSG_ID + path;

        String message = MessageBuilder.buildTestRunFinishedMessage(path);

        Assert.assertEquals("The Message is wrong!", expected, message);
    }
}
