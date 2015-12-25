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

package net.foxdenstudio.novacula.core.plugins.detector;

import java.io.*;
import java.util.zip.ZipFile;

/**
 * Created by d4rkfly3r (Joshua F.) on 12/24/15.
 */
final class ClassFileIterator {

    private final FileIterator fileIterator;
    private final String[] pkgNameFilter;
    private ZipFileIterator zipIterator;
    private boolean isFile;

    /**
     * Create a new {@code ClassFileIterator} returning all Java ClassFile files available
     * from the class path ({@code System.getProperty("java.class.path")}).
     */
    ClassFileIterator() throws IOException {
        this(classPath(), null);
    }

    /**
     * Create a new {@code ClassFileIterator} returning all Java ClassFile files available
     * from the specified files and/or directories, including sub directories.
     * <p>
     * If the (optional) package filter is defined, only class files staring with one of the
     * defined package names are returned.
     * NOTE: package names must be defined in the native format (using '/' instead of '.').
     */
    ClassFileIterator(final File[] filesOrDirectories, final String[] pkgNameFilter)
            throws IOException {

        this.fileIterator = new FileIterator(filesOrDirectories);
        this.pkgNameFilter = pkgNameFilter;
    }

    /**
     * Return the name of the Java ClassFile returned from the last call to {@link #next()}.
     * The name is either the path name of a file or the name of an ZIP/JAR file entry.
     */
    public String getName() {
        // Both getPath() and getName() are very light weight method calls
        return zipIterator == null ?
                fileIterator.getFile().getPath() :
                zipIterator.getEntry().getName();
    }

    /**
     * Return {@code true} if the current {@link InputStream} is reading from a plain
     * {@link File}. Return {@code false} if the current {@link InputStream} is reading from a
     * ZIP File Entry.
     */
    public boolean isFile() {
        return isFile;
    }

    /**
     * Return the next Java ClassFile as an {@code InputStream}.
     * <p>
     * NOTICE: Client code MUST close the returned {@code InputStream}!
     */
    public InputStream next() throws IOException {
        while (true) {
            if (zipIterator == null) {
                final File file = fileIterator.next();
                if (file == null) {
                    return null;
                } else {
                    final String name = file.getName();
                    if (name.endsWith(".class")) {
                        isFile = true;
                        return new FileInputStream(file);
                    } else if (fileIterator.isRootFile() && (endsWithIgnoreCase(name, ".jar") || isZipFile(file))) {
                        zipIterator = new ZipFileIterator(new ZipFile(file), pkgNameFilter);
                    } // else just ignore
                }
            } else {
                final InputStream is = zipIterator.next();
                if (is == null) {
                    zipIterator = null;
                } else {
                    isFile = false;
                    return is;
                }
            }
        }
    }

    // private

    private boolean isZipFile(File file) throws IOException {
        DataInputStream in = new DataInputStream(new FileInputStream(file));
        int n = in.readInt();
        in.close();
        return n == 0x504b0304;
    }

    /**
     * Returns the class path of the current JVM instance as an array of {@link File} objects.
     */
    private static File[] classPath() {
        final String[] fileNames = System.getProperty("java.class.path")
                .split(File.pathSeparator);
        final File[] files = new File[fileNames.length];
        for (int i = 0; i < files.length; ++i) {
            files[i] = new File(fileNames[i]);
        }
        return files;
    }

    private static boolean endsWithIgnoreCase(final String value, final String suffix) {
        final int n = suffix.length();
        return value.regionMatches(true, value.length() - n, suffix, 0, n);
    }
}