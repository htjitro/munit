package org.mule.munit.runner.remote;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mule.munit.runner.mule.MunitTest;
import org.mule.munit.runner.mule.result.TestResult;
import org.mule.munit.runner.mule.result.notification.Notification;

import java.io.IOException;
import java.io.ObjectOutput;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Mulesoft Inc.
 * @since 3.6.0
 */
public class RemoteRunnerNotificationListenerTest {

    private ObjectOutput osMock;
    private String testName = "a_munit_test";

    private RemoteRunnerNotificationListener listener;

    @Before
    public void setUp() throws IOException {
        osMock = Mockito.mock(ObjectOutput.class);
        Mockito.doNothing().when(osMock).writeObject(anyObject());
        
        listener = new RemoteRunnerNotificationListener(osMock);
    }

    @Test
    public void testNotifyNumberOfTests() throws IOException {
        String numberOfTests = "4";

        Mockito.doNothing().when(osMock).writeObject(anyString());
        Mockito.doNothing().when(osMock).flush();

        listener.notifyNumberOfTest(Integer.valueOf(numberOfTests));

        verify(osMock).writeObject(MessageBuilder.buildNuberOfTestsMessage(numberOfTests));
    }

    @Test
    public void testNotifyStartOf() throws IOException {
        MunitTest munitTestMock = Mockito.mock(MunitTest.class);
        Mockito.when(munitTestMock.getName()).thenReturn(testName);

        listener.notifyStartOf(munitTestMock);

        verify(osMock).writeObject(MessageBuilder.buildNewTestMessage(testName));
    }

    @Test
    public void testNotifyError() throws IOException {
        String fullMessage = "something_something_dark_side";

        // Mock notification
        Notification notificationMock = Mockito.mock(Notification.class);
        when(notificationMock.getFullMessage()).thenReturn(fullMessage);

        // mock test result
        TestResult testResult = Mockito.mock(TestResult.class);
        when(testResult.getTestName()).thenReturn(testName);
        when(testResult.getError()).thenReturn(notificationMock);

        listener.notify(testResult);

        verify(osMock).writeObject(MessageBuilder.buildTestErroMessage(testName, fullMessage));
    }

    @Test
    public void testNotifyFailure() throws IOException {
        String fullMessage = "something_something_dark_side";

        // Mock Notification
        Notification notificationMock = Mockito.mock(Notification.class);
        when(notificationMock.getFullMessage()).thenReturn(fullMessage);

        // mock test result
        TestResult testResult = Mockito.mock(TestResult.class);
        when(testResult.getTestName()).thenReturn(testName);
        when(testResult.getError()).thenReturn(null);
        when(testResult.getFailure()).thenReturn(notificationMock);

        listener.notify(testResult);

        verify(osMock).writeObject(MessageBuilder.buildTestFailureMessage(testName, fullMessage));
    }

    @Test
    public void testNotifyFinished() throws IOException {
        // mock test result
        TestResult testResult = Mockito.mock(TestResult.class);
        when(testResult.getTestName()).thenReturn(testName);
        when(testResult.getError()).thenReturn(null);
        when(testResult.getFailure()).thenReturn(null);

        listener.notify(testResult);

        verify(osMock).writeObject(MessageBuilder.buildTestFinishedMessage(testName));
    }

    @Test
    public void testNotifyTestRunEnd() throws IOException {
        String testFullPath="src/test/munit/some-munit-test-suite.xml";

        listener.notifyTestRunEnd(testFullPath);

        verify(osMock).writeObject(MessageBuilder.buildTestRunFinishedMessage(testFullPath));
    }

}
