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

import java.lang.annotation.*;

/**
 * Created by d4rkfly3r (Joshua F.) on 12/24/15.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NovaMethodListener {


    /**
     * The url of the page.
     * Ex. http://{host}:{port}/{pluginPath}/{NAME/THIS}
     *
     * @return String - Page access url.
     */
    String name();

    /**
     * Requested content type. Not in use right now.
     *
     * @return RequestType - Expected content type.
     */
    RequestType requestType() default RequestType.CONTENT;


    /**
     * an Enum of the possible separate types of expected content, will add more at a later date.  This will eventually allow for different methods to handle the same url but with different data types.
     */
    @SuppressWarnings("unused")
    enum RequestType {
        AUDIO("An Audio file/stream"), VIDEO("A Video file/stream"), IMAGE("An image file"), CONTENT("Any web content - HTML, JAVASCRIPT, CSS, etc."), OTHER("Anything else...");

        private final String helpData;

        RequestType(String helpData) {
            this.helpData = helpData;
        }

        /**
         * @return String - A short description of what the type is.
         */
        public String getHelpData() {
            return helpData;
        }
    }
}
