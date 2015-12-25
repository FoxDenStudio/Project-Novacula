/**************************************************************************************************
 * The MIT License (MIT)                                                                          *
 *                                                                                                *
 * Copyright (c) 2015. FoxDenStudio                                                               *
 *                                                                                                *
 * Permission is hereby granted, free of charge, to any person obtaining a copy                   *
 * of this software and associated documentation files (the "Software"), to deal                  *
 * in the Software without restriction, including without limitation the rights                   *
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell                      *
 * copies of the Software, and to permit persons to whom the Software is                          *
 * furnished to do so, subject to the following conditions:                                       *
 *                                                                                                *
 * The above copyright notice and this permission notice shall be included in all                 *
 * copies or substantial portions of the Software.                                                *
 *                                                                                                *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR                     *
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,                       *
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE                    *
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER                         *
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,                  *
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE                  *
 * SOFTWARE.                                                                                      *
 **************************************************************************************************/

package net.foxdenstudio.novacula.anno;

import java.lang.reflect.Method;

/**
 * Created by d4rkfly3r (Joshua F.) on 12/24/15.
 */
public class NovaMethodListenerData {
    private final NovaMethodListener.RequestType requestType;
    private final Object classInstance;
    private final Method method;

    /**
     * @param requestType   RequestType Object - An enum of what type of object was requested... Currently not in use.
     * @param classInstance An object of the instance of the class that the method resides in.
     * @param method        A Method object that links to the method so that it can be called when needed.
     */
    public NovaMethodListenerData(NovaMethodListener.RequestType requestType, Object classInstance, Method method) {
        this.requestType = requestType;
        this.classInstance = classInstance;
        this.method = method;
    }

    /**
     * @return RequestType - An enum specifying what type of content should be returned. Not used yet.
     */
    public NovaMethodListener.RequestType getRequestType() {
        return requestType;
    }

    /**
     * @return Object - An instance of the class that the method resides in.
     */
    public Object getClassInstance() {
        return classInstance;
    }

    /**
     * @return Method - The method that will be called.
     */
    public Method getMethod() {
        return method;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "[reqType=" + requestType.name() + ", class=" + classInstance.getClass().getSimpleName() + ", method=" + method.getName() + "]";
    }
}
