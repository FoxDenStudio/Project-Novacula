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
import net.foxdenstudio.novacula.core.utils.HTTPHeaderParser;
import net.foxdenstudio.novacula.core.utils.NovaLogger;

import java.io.*;
import java.net.Socket;
import java.util.Date;

/**
 * Created by d4rkfly3r (Joshua F.) on 12/23/15.
 */
class ClientConnectionThread implements Runnable {

    private final Socket socket;
    private final String serverName;
    private final NovaLogger novaLogger;

    private String fileMimeType = "text/html";

    public ClientConnectionThread(NovaLogger novaLogger, Socket clientSocket, String serverName) {
        this.novaLogger = novaLogger;

        this.socket = clientSocket;
        this.serverName = serverName;
    }

    @Override
    public void run() {
        try (OutputStream outputStream = socket.getOutputStream(); InputStream inputStream = socket.getInputStream()) {
            long time = System.currentTimeMillis();

            HTTPHeaderParser headerParser = new HTTPHeaderParser(inputStream);
            novaLogger.log("P1: " + headerParser.parseRequest());
            novaLogger.log("P2: " + headerParser.getVersion());
            novaLogger.log("P3: " + headerParser.getRequestURL());
            novaLogger.log("P4: " + headerParser.getMethod());
            novaLogger.log("P5: " + headerParser.getParams());
            novaLogger.log("P6: " + headerParser.getHeaders());

//            QuickAccess.Success200(outputStream, fileMimeType);
            QuickAccess.Error404(outputStream, serverName);

            outputStream.flush();
            novaLogger.log("Request processed in: " + (System.currentTimeMillis() - time));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class QuickAccess {

        static void Error404(OutputStream outputStream, String serverName) throws IOException {
            String make = "";
            make += "HTTP/1.1 404 Not Found\r\n";
            make += "Date: " + new Date().toString() + "\r\n";
            make += "Server: NovaServer1.5r\n";
            make += "Accept-Ranges: bytes\r\n";
            make += ("Content-Type: text/html\r\n");
            make += "\r\n";
            make += "<html>\r\n";
            make += "<Title>404 File Not Found</Title>\r\n";
            make += "<body style='background-color: #2A3132;'>\r\n";
            make += "<p>&nbsp;</p><p>&nbsp;</p><p>&nbsp;</p>\r\n";
            make += "<div align='center'><center>\r\n";
            make += "<div style='width: 60%;padding: 7px;background-color: #763626;'>\r\n";
            make += "<p align='center'><font color='#FFFFFF' size='6'><strong>404 File Not Found</strong></font></p>\r\n";
            make += "<p><font color='#FFFFFF' size='4'>The Web Server cannot find the requested file or script.  Please check the URL to be sure that it is correct.</font></p>\r\n";
            make += "<p><font color='#FFFFFF' size='4'>If the problem persists, please contact the webmaster at " + StartupArgs.MAILTO + "</font></p>\r\n";
            make += "</div>\r\n";
            make += "</center></div>\r\n";
            make += "</html>" + "\r\n";
            outputStream.write(make.getBytes());
            outputStream.flush();
        }

        public static void Success200(OutputStream outputStream, String fileMimeType) throws IOException {
            String make = "";
            make += "HTTP/1.1 200 OK\r\n";
            make += "Date: " + new Date().toString() + "\r\n";
            make += "Server: NovaServer1.5r\n";
            make += "Accept-Ranges: bytes\r\n";
            make += "Content-Type: " + fileMimeType + "\r\n";
            make += "\r\n";
            outputStream.write(make.getBytes());
            outputStream.flush();
        }
    }
}
