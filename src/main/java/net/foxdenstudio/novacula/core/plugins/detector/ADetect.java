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

package net.foxdenstudio.novacula.core.plugins.detector;

import java.io.DataInput;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
/**
 * Created by d4rkfly3r (Joshua F.) on 12/24/15.
 */
public final class ADetect {
    /**
     * {@code Reporter} is the base interface, used to report the detected annotations.
     * Every category of annotations (i.e. Type, Field and Method) has its own specialized
     * interface. This enables an efficient way of reporting the detected annotations.
     */
    public interface Reporter {

        /**
         * Return the {@code Annotation} classes which must be reported (all other
         * annotations are skipped).
         */
        Class<? extends Annotation>[] annotations();

    }

    /**
     * A {@code Reporter} for type annotations.
     */
    public interface TypeReporter extends Reporter {

        /**
         * This call back method is used to report an type level {@code Annotation}.
         * Only {@code Annotation}s, specified by {@link #annotations()} are reported!
         */
        void reportTypeAnnotation(Class<? extends Annotation> annotation, String className);

    }

    /**
     * A {@code Reporter} for field annotations.
     */
    public interface FieldReporter extends Reporter {

        /**
         * This call back method is used to report an field level {@code Annotation}.
         * Only {@code Annotation}s, specified by {@link #annotations()} are reported!
         */
        void reportFieldAnnotation(Class<? extends Annotation> annotation, String className,
                                   String fieldName);

    }

    /**
     * A {@code Reporter} for method annotations.
     */
    public interface MethodReporter extends Reporter {

        /**
         * This call back method is used to report an method level {@code Annotation}.
         * Only {@code Annotation}s, specified by {@link #annotations()} are reported!
         */
        void reportMethodAnnotation(Class<? extends Annotation> annotation, String className,
                                    String methodName);

    }

    // Only used during development. If set to "true" debug messages are displayed.
    private static final boolean DEBUG = false;

    // Constant Pool type tags
    private static final int CP_UTF8           = 1;
    private static final int CP_INTEGER        = 3;
    private static final int CP_FLOAT          = 4;
    private static final int CP_LONG           = 5;
    private static final int CP_DOUBLE         = 6;
    private static final int CP_CLASS          = 7;
    private static final int CP_STRING         = 8;
    private static final int CP_REF_FIELD      = 9;
    private static final int CP_REF_METHOD     = 10;
    private static final int CP_REF_INTERFACE  = 11;
    private static final int CP_NAME_AND_TYPE  = 12;
    private static final int CP_METHOD_HANDLE  = 15;
    private static final int CP_METHOD_TYPE    = 16;
    private static final int CP_INVOKE_DYNAMIC = 18;
    // AnnotationElementValue
    private static final int BYTE              = 'B';
    private static final int CHAR              = 'C';
    private static final int DOUBLE            = 'D';
    private static final int FLOAT             = 'F';
    private static final int INT               = 'I';
    private static final int LONG              = 'J';
    private static final int SHORT             = 'S';
    private static final int BOOLEAN           = 'Z';
    // used for AnnotationElement only
    private static final int STRING            = 's';
    private static final int ENUM              = 'e';
    private static final int CLASS             = 'c';
    private static final int ANNOTATION        = '@';
    private static final int ARRAY             = '[';

    // The buffer is reused during the life cycle of this AnnotationDetector instance
    private final ClassFileBuffer cpBuffer = new ClassFileBuffer();
    // the annotation types to report, see {@link #annotations()}
    private final Map<String, Class<? extends Annotation>> annotations;

    private TypeReporter   typeReporter;
    private FieldReporter  fieldReporter;
    private MethodReporter methodReporter;

    // the 'raw' name of this interface or class (using '/' instead of '.' in package name)
    private String   typeName;
    // Reusing the constantPool is not needed for better performance
    private Object[] constantPool;
    private String   memberName;

    /**
     * Create a new {@code AnnotationDetector}, reporting the detected annotations
     * to the specified {@code Reporter}.
     */
    public ADetect(final Reporter reporter) {

        final Class<? extends Annotation>[] a = reporter.annotations();
        annotations = new HashMap<String, Class<? extends Annotation>>(a.length);
        // map "raw" type names to Class object
        for (int i = 0; i < a.length; ++i) {
            annotations.put("L" + a[i].getName().replace('.', '/') + ";", a[i]);
        }
        if (reporter instanceof TypeReporter) {
            typeReporter = (TypeReporter) reporter;
        }
        if (reporter instanceof FieldReporter) {
            fieldReporter = (FieldReporter) reporter;
        }
        if (reporter instanceof MethodReporter) {
            methodReporter = (MethodReporter) reporter;
        }
        if (typeReporter == null && fieldReporter == null && methodReporter == null) {
            throw new AssertionError("No reporter defined");
        }
    }

    /**
     * Report all Java ClassFile files available on the class path.
     *
     * @see #detect(File...)
     */
    public void detect() throws IOException {
        detect(new ClassFileIterator());
    }

    /**
     * Report all Java ClassFile files available on the class path within
     * the specified packages and sub packages.
     *
     * @see #detect(File...)
     */
    public void detect(final String... packageNames) throws IOException {
        final String[] pkgNameFilter = new String[packageNames.length];
        for (int i = 0; i < pkgNameFilter.length; ++i) {
            pkgNameFilter[i] = packageNames[i].replace('.', '/');
            if (!pkgNameFilter[i].endsWith("/")) {
                pkgNameFilter[i] = pkgNameFilter[i].concat("/");
            }
        }
        final Set<File> files = new HashSet<File>();
        for (final String packageName : pkgNameFilter) {
            final ClassLoader loader = Thread.currentThread().getContextClassLoader();
            final Enumeration<URL> resourceEnum = loader.getResources(packageName);
            while (resourceEnum.hasMoreElements()) {
                final URL url = resourceEnum.nextElement();
                // Handle JBoss/WildFly VFS URL's which look like:
                // vfs:/foo/bar/website.war/WEB-INF/classes/nl/dvelop/
                // vfs:/foo/bar/website.war/WEB-INF/lib/dwebcore-0.0.1.jar/nl/dvelop/
                // Known vfs protocols: vfs, vfsfile, vfszip, vfsjar, and vfsmemory
                // See https://github.com/Atmosphere/atmosphere/issues/1701
                final boolean isVfs = url.getProtocol().startsWith("vfs");
                if ("file".equals(url.getProtocol()) || isVfs) {
                    final File dir = toFile(url);
                    if (dir.isDirectory()) {
                        files.add(dir);
                    } else if (isVfs) {
                        //Jar file via JBoss VFS protocol - strip package name
                        String jarPath = dir.getPath();
                        final int idx = jarPath.indexOf(".jar");
                        if (idx > -1) {
                            jarPath = jarPath.substring(0, idx + 4);
                            final File jarFile = new File(jarPath);
                            if (jarFile.isFile()) {
                                files.add(jarFile);
                            }
                        }
                    } else {
                        throw new AssertionError("Not a recognized file URL: " + url);
                    }
                } else {
                    // Resource in Jar File
                    final File jarFile =
                            toFile(((JarURLConnection) url.openConnection()).getJarFileURL());
                    if (jarFile.isFile()) {
                        files.add(jarFile);
                    } else {
                        throw new AssertionError("Not a File: " + jarFile);
                    }
                }
            }
        }
        if (DEBUG) {
            print("Files to scan: %s", files);
        }
        if (!files.isEmpty()) {
            detect(new ClassFileIterator(files.toArray(new File[files.size()]),
                    pkgNameFilter));
        }
    }

    /**
     * Report all Java ClassFile files available from the specified files
     * and/or directories, including sub directories.
     * <p>
     * Note that non-class files (files, not starting with the magic number
     * {@code CAFEBABE} are silently ignored.
     */
    public void detect(final File... filesOrDirectories) throws IOException {
        if (DEBUG) {
            print("detectFilesOrDirectories: %s", (Object) filesOrDirectories);
        }
        detect(new ClassFileIterator(filesOrDirectories, null));
    }

    // private

    private File toFile(final URL url) throws MalformedURLException {
        // only correct way to convert the URL to a File object, also see issue #16
        // Do not use URLDecoder
        try {
            return new File(url.toURI().getPath());
        } catch (Exception ex) {
            final MalformedURLException mue = new MalformedURLException(url.toExternalForm());
            mue.initCause(ex);
            throw mue;
        }
    }

    @SuppressWarnings("illegalcatch")
    private void detect(final ClassFileIterator iterator) throws IOException {
        InputStream stream;
        while ((stream = iterator.next()) != null) {
            try {
                cpBuffer.readFrom(stream);
                if (hasCafebabe(cpBuffer)) {
                    detect(cpBuffer);
                } // else ignore
            } catch (Throwable t) {
                // catch all errors
                if (!iterator.isFile()) {
                    // in case of an error we close the ZIP File here
                    stream.close();
                }
            } finally {
                // closing InputStream from ZIP Entry is handled by ZipFileIterator
                if (iterator.isFile()) {
                    stream.close();
                }
            }
        }
    }

    private boolean hasCafebabe(final ClassFileBuffer buffer) throws IOException {
        return buffer.size() > 4 && buffer.readInt() == 0xCAFEBABE;
    }

    /**
     * Inspect the given (Java) class file in streaming mode.
     */
    private void detect(final DataInput di) throws IOException {
        readVersion(di);
        readConstantPoolEntries(di);
        readAccessFlags(di);
        readThisClass(di);
        readSuperClass(di);
        readInterfaces(di);
        readFields(di);
        readMethods(di);
        readAttributes(di, 'T', typeReporter == null);
    }

    private void readVersion(final DataInput di) throws IOException {
        // sequence: minor version, major version (argument_index is 1-based)
        if (DEBUG) {
            print("Java Class version %2$d.%1$d",
                    di.readUnsignedShort(), di.readUnsignedShort());
        } else {
            di.skipBytes(4);
        }
    }

    private void readConstantPoolEntries(final DataInput di) throws IOException {
        final int count = di.readUnsignedShort();
        constantPool = new Object[count];
        for (int i = 1; i < count; ++i) {
            if (readConstantPoolEntry(di, i)) {
                // double slot
                ++i;
            }
        }
    }

    /**
     * Return {@code true} if a double slot is read (in case of Double or Long constant).
     */
    private boolean readConstantPoolEntry(final DataInput di, final int index)
            throws IOException {

        final int tag = di.readUnsignedByte();
        switch (tag) {
            case CP_METHOD_TYPE:
                di.skipBytes(2);  // readUnsignedShort()
                return false;
            case CP_METHOD_HANDLE:
                di.skipBytes(3);
                return false;
            case CP_INTEGER:
            case CP_FLOAT:
            case CP_REF_FIELD:
            case CP_REF_METHOD:
            case CP_REF_INTERFACE:
            case CP_NAME_AND_TYPE:
            case CP_INVOKE_DYNAMIC:
                di.skipBytes(4); // readInt() / readFloat() / readUnsignedShort() * 2
                return false;
            case CP_LONG:
            case CP_DOUBLE:
                di.skipBytes(8); // readLong() / readDouble()
                return true;
            case CP_UTF8:
                constantPool[index] = di.readUTF();
                return false;
            case CP_CLASS:
            case CP_STRING:
                // reference to CP_UTF8 entry. The referenced index can have a higher number!
                constantPool[index] = di.readUnsignedShort();
                return false;
            default:
                throw new ClassFormatError(
                        "Unkown tag value for constant pool entry: " + tag);
        }
    }

    private void readAccessFlags(final DataInput di) throws IOException {
        di.skipBytes(2); // u2
    }

    private void readThisClass(final DataInput di) throws IOException {
        typeName = resolveUtf8(di);
        if (DEBUG) {
            print("read type '%s'", typeName);
        }
    }

    private void readSuperClass(final DataInput di) throws IOException {
        di.skipBytes(2); // u2
    }

    private void readInterfaces(final DataInput di) throws IOException {
        final int count = di.readUnsignedShort();
        di.skipBytes(count * 2); // count * u2
    }

    private void readFields(final DataInput di) throws IOException {
        final int count = di.readUnsignedShort();
        if (DEBUG) {
            print("field count = %d", count);
        }
        for (int i = 0; i < count; ++i) {
            readAccessFlags(di);
            memberName = resolveUtf8(di);
            final String descriptor = resolveUtf8(di);
            readAttributes(di, 'F', fieldReporter == null);
            if (DEBUG) {
                print("Field: %s, descriptor: %s", memberName, descriptor);
            }
        }
    }

    private void readMethods(final DataInput di) throws IOException {
        final int count = di.readUnsignedShort();
        if (DEBUG) {
            print("method count = %d", count);
        }
        for (int i = 0; i < count; ++i) {
            readAccessFlags(di);
            memberName = resolveUtf8(di);
            final String descriptor = resolveUtf8(di);
            readAttributes(di, 'M', methodReporter == null);
            if (DEBUG) {
                print("Method: %s, descriptor: %s", memberName, descriptor);
            }
        }
    }

    private void readAttributes(final DataInput di, final char reporterType,
                                final boolean skipReporting) throws IOException {

        final int count = di.readUnsignedShort();
        if (DEBUG) {
            print("attribute count (%s) = %d", reporterType, count);
        }
        for (int i = 0; i < count; ++i) {
            final String name = resolveUtf8(di);
            // in bytes, use this to skip the attribute info block
            final int length = di.readInt();
            if (!skipReporting &&
                    ("RuntimeVisibleAnnotations".equals(name) ||
                            "RuntimeInvisibleAnnotations".equals(name))) {
                readAnnotations(di, reporterType);
            } else {
                if (DEBUG) {
                    print("skip attribute %s", name);
                }
                di.skipBytes(length);
            }
        }
    }

    private void readAnnotations(final DataInput di, final char reporterType)
            throws IOException {

        // the number of Runtime(In)VisibleAnnotations
        final int count = di.readUnsignedShort();
        if (DEBUG) {
            print("annotation count (%s) = %d", reporterType, count);
        }
        for (int i = 0; i < count; ++i) {
            final String rawTypeName = readAnnotation(di);
            final Class<? extends Annotation> type = annotations.get(rawTypeName);
            if (type == null) {
                continue;
            }
            final String externalTypeName = typeName.replace('/', '.');
            switch (reporterType) {
                case 'T':
                    typeReporter.reportTypeAnnotation(type, externalTypeName);
                    break;
                case 'F':
                    fieldReporter.reportFieldAnnotation(type, externalTypeName, memberName);
                    break;
                case 'M':
                    methodReporter.reportMethodAnnotation(type, externalTypeName, memberName);
                    break;
                default:
                    throw new AssertionError("reporterType=" + reporterType);
            }
        }
    }

    private String readAnnotation(final DataInput di) throws IOException {
        final String rawTypeName = resolveUtf8(di);
        // num_element_value_pairs
        final int count = di.readUnsignedShort();
        if (DEBUG) {
            print("annotation elements count: %d", count);
        }
        for (int i = 0; i < count; ++i) {
            if (DEBUG) {
                print("element '%s'", resolveUtf8(di));
            } else {
                di.skipBytes(2);
            }
            readAnnotationElementValue(di);
        }
        return rawTypeName;
    }


    private void readAnnotationElementValue(final DataInput di) throws IOException {
        final int tag = di.readUnsignedByte();
        if (DEBUG) {
            print("tag='%c'", (char) tag);
        }
        switch (tag) {
            case BYTE:
            case CHAR:
            case DOUBLE:
            case FLOAT:
            case INT:
            case LONG:
            case SHORT:
            case BOOLEAN:
            case STRING:
                di.skipBytes(2);
                break;
            case ENUM:
                di.skipBytes(4); // 2 * u2
                break;
            case CLASS:
                di.skipBytes(2);
                break;
            case ANNOTATION:
                readAnnotation(di);
                break;
            case ARRAY:
                final int count = di.readUnsignedShort();
                for (int i = 0; i < count; ++i) {
                    readAnnotationElementValue(di);
                }
                break;
            default:
                throw new ClassFormatError("Not a valid annotation element type tag: 0x" +
                        Integer.toHexString(tag));
        }
    }

    /**
     * Look up the String value, identified by the u2 index value from constant pool
     * (direct or indirect).
     */
    private String resolveUtf8(final DataInput di) throws IOException {
        final int index = di.readUnsignedShort();
        final Object value = constantPool[index];
        final String s;
        if (value instanceof Integer) {
            s = (String) constantPool[(Integer) value];
            if (DEBUG) {
                print("resolveUtf8(%d): %d --> %s", index, value, s);
            }
        } else {
            s = (String) value;
            if (DEBUG) {
                print("resolveUtf8(%d): %s", index, s);
            }
        }

        return s;
    }

    /**
     * Helper method for simple (debug) logging.
     */
    @SuppressWarnings("regexpsinglelinejava")
    private static void print(final String message, final Object... args) {
        if (DEBUG) {
            final String logMessage;
            if (args.length == 0) {
                logMessage = message;
            } else {
                for (int i = 0; i < args.length; ++i) {
                    // arguments may be null
                    if (args[i] == null) {
                        continue;
                    }
                    if (args[i].getClass().isArray()) {
                        // cast back to array! Note that primitive arrays are not supported
                        args[i] = Arrays.toString((Object[]) args[i]);
                    } else if (args[i] == Class.class) {
                        args[i] = ((Class<?>) args[i]).getName();
                    }
                }
                logMessage = String.format(message, args);
            }
            System.out.println(logMessage);
        }
    }
}
