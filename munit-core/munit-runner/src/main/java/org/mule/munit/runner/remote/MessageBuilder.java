/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.munit.runner.remote;


/**
 * @author Mulesoft Inc.
 * @since 3.6.0
 */
public class MessageBuilder {

    public static final String MESSAGE_TOKEN_SEPARATOR = ";";

    public static final String NUMBER_OF_TESTS_MSG_ID = "0" + MESSAGE_TOKEN_SEPARATOR;
    public static final String NEW_TEST_MSG_ID = "1" + MESSAGE_TOKEN_SEPARATOR;
    public static final String TEST_FAILURE_MSG_ID = "2" + MESSAGE_TOKEN_SEPARATOR;
    public static final String TEST_ERROR_MSG_ID = "3" + MESSAGE_TOKEN_SEPARATOR;
    public static final String TEST_FINSHED_MSG_ID = "4" + MESSAGE_TOKEN_SEPARATOR;
    public static final String TEST_RUN_FINSHED_MSG_ID = "5" + MESSAGE_TOKEN_SEPARATOR;
    public static final String TEST_IGNORED_MSG_ID = "6" + MESSAGE_TOKEN_SEPARATOR;

    public static final String STACK_TRACE_MARKER = "'";

    public static String buildNuberOfTestsMessage(String numberOfTests) {
        StringBuilder builder = new StringBuilder();
        builder.append(NUMBER_OF_TESTS_MSG_ID);
        builder.append(numberOfTests);
        return builder.toString();
    }

    public static String buildNewTestMessage(String testName) {
        StringBuilder builder = new StringBuilder();
        builder.append(NEW_TEST_MSG_ID);
        builder.append(testName);
        return builder.toString();
    }


    public static String buildTestErroMessage(String testName, String fullMessage) {
        StringBuilder builder = new StringBuilder();
        builder.append(TEST_ERROR_MSG_ID);
        builder.append(testName);
        builder.append(MESSAGE_TOKEN_SEPARATOR);
        builder.append(STACK_TRACE_MARKER);
        builder.append(fullMessage);
        builder.append(STACK_TRACE_MARKER);

        return builder.toString();
    }

    public static String buildTestFailureMessage(String testName, String fullMessage) {
        StringBuilder builder = new StringBuilder();
        builder.append(TEST_FAILURE_MSG_ID);
        builder.append(testName);
        builder.append(MESSAGE_TOKEN_SEPARATOR);
        builder.append(STACK_TRACE_MARKER);
        builder.append(fullMessage);
        builder.append(STACK_TRACE_MARKER);
        return builder.toString();
    }
    
    public static String buildTestIgnoredMessage(String testName) {
        StringBuilder builder = new StringBuilder();
        builder.append(TEST_IGNORED_MSG_ID);
        builder.append(testName);
        return builder.toString();
    }
    

    public static String buildTestFinishedMessage(String testName) {
        StringBuilder builder = new StringBuilder();
        builder.append(TEST_FINSHED_MSG_ID);
        builder.append(testName);
        return builder.toString();
    }

    public static String buildTestRunFinishedMessage(String testFullPath) {
        StringBuilder builder = new StringBuilder();
        builder.append(TEST_RUN_FINSHED_MSG_ID);
        builder.append(testFullPath);
        return builder.toString();
    }
}
