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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by d4rkfly3r (Joshua F.) on 12/24/15.
 */
@SuppressWarnings("unused")
public class CombinedWebResponse implements IWebServiceResponse {


    private LinkedList<IWebServiceResponse> responseList;

    public CombinedWebResponse() {
        this(new LinkedList<>());
    }

    public CombinedWebResponse(IWebServiceResponse response) {
        this(new LinkedList<IWebServiceResponse>() {{
            add(response);
        }});
    }

    public CombinedWebResponse(LinkedList<IWebServiceResponse> responseList) {
        this.responseList = responseList;
    }

    public CombinedWebResponse addResponse(IWebServiceResponse response) {
        responseList.add(response);
        return this;
    }

    public CombinedWebResponse addResponse(int index, IWebServiceResponse response) {
        responseList.add(index, response);
        return this;
    }

    public CombinedWebResponse removeResponse(int index) {
        responseList.remove(index);
        return this;
    }

    public int getSize() {
        return responseList.size();
    }

    public LinkedList<IWebServiceResponse> getResponseList() {
        return responseList;
    }

    public void setResponseList(LinkedList<IWebServiceResponse> responseList) {
        this.responseList = responseList;
    }

    /**
     * @return A byte array that will be written out to the clients stream.
     */
    @Override
    public byte[] getByteData() {
        List<byte[]> list = new LinkedList<>();
        responseList.forEach(response -> list.add(response.getByteData()));
        return concatenateByteArrays(list);
    }

    private byte[] concatenateByteArrays(List<byte[]> blocks) {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            for (byte[] b : blocks) {
                os.write(b, 0, b.length);
            }
            return os.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    //TODO Add Mime Priorities....

    /**
     * @return A string like plain/text that is the content type.  If the plugin is a dependency, you can use the Mimes.getMime(".{fileExt}")
     */
    @Override
    public String mimeType() {
        return null;
    }
}