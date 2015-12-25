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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * Created by d4rkfly3r (Joshua F.) on 12/24/15.
 */
public class PHPHandler {

    public static boolean isPHPFile(String filename) {
        int extPos = filename.lastIndexOf('.');
        for (String allowedPHPExt : StartupArgs.PHP_EXTENSIONS.split(";")) {
            if (filename.substring(extPos + 1).equalsIgnoreCase(allowedPHPExt)) return true;
        }
        return false;
    }

    public static boolean isCGIFile(String filename) {
        int extPos = filename.lastIndexOf('.');
        for (String allowedCGIExt : StartupArgs.CGI_EXTENSIONS.split(";")) {
            if (filename.substring(extPos + 1).equalsIgnoreCase(allowedCGIExt)) return true;
        }
        return false;
    }

    public static void processFile(String file, OutputStream outputStream) throws IOException {
        Runtime runtime = Runtime.getRuntime();
        Process process = null;

        if (isPHPFile(file)) {
            process = runtime.exec(StartupArgs.PHP_EXEC.trim() + " " + file);
        } else if (isCGIFile(file)) {
            process = runtime.exec(file);
        }

        if (process != null) {
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    outputStream.write((line + "\r\n").getBytes());
                    outputStream.flush();
                }
            }

            process.destroy();
        }

    }
}
