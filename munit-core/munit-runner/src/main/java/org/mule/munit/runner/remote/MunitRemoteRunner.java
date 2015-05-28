/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.munit.runner.remote;


import org.mule.munit.runner.mule.MunitSuiteRunner;
import org.mule.munit.runner.mule.MunitTest;
import org.mule.munit.runner.mule.result.notification.Notification;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class MunitRemoteRunner {
    public static final String PORT_PARAMETER = "-port";
    public static final String TEST_PATH_PARAMETER = "-path";
    public static final String RESOURCE_PARAMETER = "-resource";
    public static final String TEST_NAME_PARAMETER = "-test_name";

    String message;
    Socket requestSocket;
    ObjectOutputStream out;

    public static void main(String args[]) {
        int port = -1;
        String path = null;
        String resource = null;
        String testName = null;


        for (int i = 0; i < args.length; i++) {
            if (args[i].equalsIgnoreCase(RESOURCE_PARAMETER)) {
                resource = args[i + 1];
            }
            if (args[i].equalsIgnoreCase(PORT_PARAMETER)) {
                port = Integer.valueOf(args[i + 1]);
            }
            if (args[i].equalsIgnoreCase(TEST_PATH_PARAMETER)) {
                path = args[i + 1];
            }
            if (args[i].equalsIgnoreCase(TEST_NAME_PARAMETER)) {
                testName = args[i + 1];
            }
        }

        MunitRemoteRunner serverRemoteRunner = new MunitRemoteRunner();
        serverRemoteRunner.run(port, path, resource, testName);

    }

    public void run(int port, String path, String resource, String testName) {
        RemoteRunnerNotificationListener listener = null;
        try {
            //1. creating a socket to connect to the server
            requestSocket = new Socket("localhost", port);
            System.out.println("[" + this.getClass().getName() + "]" + "Connected to localhost in port " + port);
            out = new ObjectOutputStream(requestSocket.getOutputStream());
            out.flush();

            listener = new RemoteRunnerNotificationListener(out);


            MunitSuiteRunner runner;
            try {
                runner = new MunitSuiteRunner(resource, testName);
                runner.setNotificationListener(listener);
            } catch (RuntimeException e) {
                if (listener != null) {
                    listener.notifyRuntimeStartFailure(new Notification(e.getMessage(), MunitTest.stack2string(e)));
                }
                throw e;
            }

            listener.notifyNumberOfTest(runner.getNumberOfTests());

            runner.run();
        } catch (IOException ioException) {
            // TODO: catch other exceptions and notify errors
            ioException.printStackTrace();
        } finally {
            listener.notifyTestRunEnd(path);

            try {
                //4: Closing connection
                out.close();
                requestSocket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            System.out.println("[" + this.getClass().getName() + "]" + "Done");
        }
        System.exit(0);
    }


}
