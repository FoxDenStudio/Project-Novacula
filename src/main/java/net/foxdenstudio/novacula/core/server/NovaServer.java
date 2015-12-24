/**************************************************************************************************
 * The MIT License (MIT)                                                                          *
 * *
 * Copyright (c) 2015. FoxDenStudio                                                               *
 * *
 * Permission is hereby granted, free of charge, to any person obtaining a copy                   *
 * of this software and associated documentation files (the "Software"), to deal                  *
 * in the Software without restriction, including without limitation the rights                   *
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell                      *
 * copies of the Software, and to permit persons to whom the Software is                          *
 * furnished to do so, subject to the following conditions:                                       *
 * *
 * The above copyright notice and this permission notice shall be included in all                 *
 * copies or substantial portions of the Software.                                                *
 * *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR                     *
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,                       *
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE                    *
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER                         *
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,                  *
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE                  *
 * SOFTWARE.                                                                                      *
 **************************************************************************************************/

package net.foxdenstudio.novacula.core.server;

import net.foxdenstudio.novacula.core.StartupArgs;
import net.foxdenstudio.novacula.core.utils.NovaInfo;
import net.foxdenstudio.novacula.core.utils.NovaLogger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by d4rkfly3r (Joshua F.) on 12/23/15.
 */
public class NovaServer {

    private final NovaLogger novaLogger;
    private ServerSocket serverSocket = null;
    private boolean running = true;

    protected Thread runningThread = null;

    public NovaServer(NovaLogger novaLogger) {
        this.novaLogger = novaLogger;
    }

    public synchronized void start() {
        synchronized (this) {
            this.runningThread = Thread.currentThread();
        }

        novaLogger.logQuiet(NovaLogger.ANSI_BLUE + "\n----------SERVER STARTING----------\n");
        try {
            serverSocket = new ServerSocket(StartupArgs.BASE_SERVER_PORT);

            novaLogger.logQuiet(NovaLogger.ANSI_BLUE + "\n----------SERVER IS RUNNING----------\n");
            novaLogger.logQuiet(NovaInfo.getServerInfo());
            novaLogger.logQuiet(NovaInfo.getFileInfo());
        } catch (IOException e) {
            novaLogger.logQuiet(NovaLogger.ANSI_BLUE + "\n----------SERVER START FAILED----------\n");

            e.printStackTrace();
            System.exit(1);
        }

        if (serverSocket != null) {
            while (isRunning()) {
                Socket clientSocket = null;
                try {
                    clientSocket = this.serverSocket.accept();
                } catch (IOException e) {
                    if (isRunning()) {
                        System.out.println("Server Stopped.");
                        return;
                    }
                    throw new RuntimeException("Error accepting client connection", e);
                }
                new Thread(
                        new ClientConnectionThread(clientSocket, "Multithreaded Server")
                ).start();
            }
        }
    }

    public synchronized void stop() {
        this.running = false;
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException("Error closing server", e);
        }
    }

    public boolean isRunning() {
        return this.running;
    }
}
