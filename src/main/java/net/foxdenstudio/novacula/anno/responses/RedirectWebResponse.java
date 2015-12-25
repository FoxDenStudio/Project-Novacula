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

package net.foxdenstudio.novacula.anno.responses;

import java.net.URL;

/**
 * Created by d4rkfly3r (Joshua F.) on 12/24/15.
 */
@SuppressWarnings("SameParameterValue")
public class RedirectWebResponse implements IWebServiceResponse {


    private final String data;

    public RedirectWebResponse(URL passTarget) {
        this(passTarget, 3);
    }

    @SuppressWarnings("WeakerAccess")
    public RedirectWebResponse(URL passTarget, Integer redirectTime) {
        this(passTarget, redirectTime, "");
    }

    @SuppressWarnings("WeakerAccess")
    public RedirectWebResponse(URL passTarget, Integer redirectTime, String customMessage) {
        data = "<head><meta http-equiv='refresh' content='" + redirectTime + "; url=" + passTarget.toExternalForm() + "'></head><body><h2>Redirect</h2>You will be redirected to " + passTarget.toExternalForm() + " in 3 seconds.<br />If the page does not automatically reload, please click <a href='" + passTarget.toExternalForm() + "'>HERE</a>.<br /><br />" + customMessage + "</body>";
    }

    /**
     * @return A byte array created from a redirect request that is passed on to the client.
     */
    @Override
    public byte[] getByteData() {
        return data.getBytes();
    }
}