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

package net.foxdenstudio.novacula.core;

import java.io.File;

/**
 * Created by d4rkfly3r (Joshua F.) on 12/23/15.
 */
public class StartupArgs {
    public static int BASE_SERVER_PORT = 8004;
    public static String BASE_DIR = System.getProperty("user.home", "") + File.separator + ".ProjectNovaculaData";
    public static boolean LOAD_PLUGINS = true;
    public static boolean CASE_SENSITIVE_PATHS = false;
    public static String SERVER_NAME = "ProjectNovaculaTest";
    public static String SERVER_BASE_DIR = BASE_DIR + File.separator + "ServerData";
    public static String MAILTO = "npjoshf@gmail.com";


    public static void parseArgs(String[] args) {
        for (String s : args) {
            System.out.println("Parsing startup arg: " + s);
        }

        if (!new File(BASE_DIR).exists())
            new File(BASE_DIR).mkdirs();
    }
}
