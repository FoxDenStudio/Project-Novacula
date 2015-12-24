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

package net.foxdenstudio.novacula.core.utils;

import java.io.File;

/**
 * Created by d4rkfly3r (Joshua F.) on 12/23/15.
 */
public class NovaInfo {

    public static String getServerInfo() {
        String processors = "[+] Available processors: " + Runtime.getRuntime().availableProcessors();
        long totalMemory = Runtime.getRuntime().maxMemory();
        String maxMemory = "[+] Maximum memory: " + (totalMemory == Long.MAX_VALUE ? "no limit" : totalMemory);
        String freeMemory = "[+] Free memory: " + Runtime.getRuntime().freeMemory();
        String availibleMemory = "[+] Total memory available to JVM: " + Runtime.getRuntime().totalMemory();

        return NovaLogger.ANSI_GREEN + "The Server has...\n" + NovaLogger.ANSI_CYAN + "\t" + processors + " cores.\n\t" + maxMemory + " Bytes.\n\t" + freeMemory + " Bytes.\n\t" + availibleMemory + " Bytes";
    }

    public static String getFileInfo() {
        String info = NovaLogger.ANSI_GREEN + "The Server's File Info is...\n" + NovaLogger.ANSI_CYAN;
        File[] roots = File.listRoots();

        for (File root : roots) {
            info += "\t[+] File system root: " + root.getAbsolutePath() + "\n";
            info += "\t[+] Total space: " + root.getTotalSpace() + " Bytes.\n";
            info += "\t[+] Free space: " + root.getFreeSpace() + " Bytes.\n";
            info += "\t[+] Usable space: " + root.getUsableSpace() + " Bytes";
        }
        return info;
    }
}
