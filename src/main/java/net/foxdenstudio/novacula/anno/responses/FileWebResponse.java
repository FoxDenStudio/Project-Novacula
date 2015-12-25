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

import com.google.common.io.ByteStreams;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by d4rkfly3r (Joshua F.) on 12/24/15.
 */
public class FileWebResponse implements IWebServiceResponse {

    private byte[] byteData = new byte[1024];
    private String mimeType;

    public FileWebResponse(File page) throws IOException {
        try (FileInputStream fileInputStream = new FileInputStream(page)) {
            byteData = new byte[(int) page.length()];

            //noinspection ResultOfMethodCallIgnored
            fileInputStream.read(byteData);
        }
        mimeType = "text/html";
    }

    public FileWebResponse(InputStream resource) {
        try {
            byteData = ByteStreams.toByteArray(resource);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            resource.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return A string like plain/text that is the content type.  Is retrieved automatically based on file extension.
     */
    @Override
    public String mimeType() {
        return mimeType;
    }

    /**
     * @return A byte array that will be written out to the clients stream. In the case, the file data.
     */
    @Override
    public byte[] getByteData() {
        return byteData;
    }
}