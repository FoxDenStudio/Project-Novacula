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

package net.foxdenstudio.novacula.core.plugins.events;

import net.foxdenstudio.novacula.core.utils.HTTPHeaderParser;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by d4rkfly3r (Joshua F.) on 12/24/15.
 */
public class ServerRequestEvent implements Event {

    private final OutputStream clientOutputStream;
    private final HTTPHeaderParser httpHeaderParser;
    private boolean handled = false;

    public ServerRequestEvent(OutputStream clientOutputStream, HTTPHeaderParser httpHeaderParser) {
        this.clientOutputStream = clientOutputStream;
        this.httpHeaderParser = httpHeaderParser;
    }

    @Override
    public String getName() {
        return "Server Request Event";
    }

    public OutputStream getClientOutputStream() {
        return clientOutputStream;
    }

    public HTTPHeaderParser getHttpHeaderParser() {
        return httpHeaderParser;
    }

    public boolean isHandled() {
        return handled;
    }

    public void handle() {
        this.handled = true;
        try {
            clientOutputStream.flush();
            clientOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
