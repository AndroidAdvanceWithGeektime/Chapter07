/*
 * Tencent is pleased to support the open source community by making wechat-matrix available.
 * Copyright (C) 2018 THL A29 Limited, a Tencent company. All rights reserved.
 * Licensed under the BSD 3-Clause License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://opensource.org/licenses/BSD-3-Clause
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.geektime.systrace;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Field;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;


public final class Util {
    private static final String TAG         = "Util";
    public static final  int    BUFFER_SIZE = 16384;

    private Util() {
    }

    public static void addZipEntry(ZipOutputStream zipOutputStream, ZipEntry zipEntry, InputStream inputStream) throws Exception {
        try {
            zipOutputStream.putNextEntry(zipEntry);
            byte[] buffer = new byte[BUFFER_SIZE];
            int length = -1;
            while ((length = inputStream.read(buffer, 0, buffer.length)) != -1) {
                zipOutputStream.write(buffer, 0, length);
                zipOutputStream.flush();
            }
        } catch (ZipException e) {
            Log.e(TAG, "addZipEntry err!");
        } finally {
            closeQuietly(inputStream);

            zipOutputStream.closeEntry();
        }
    }

    public static boolean isNullOrNil(String str) {
        return str == null || str.isEmpty();
    }

    public static Field getDeclaredFieldRecursive(Object clazz, String fieldName) throws NoSuchFieldException, ClassNotFoundException {
        Class<?> realClazz = null;
        if (clazz instanceof String) {
            realClazz = Class.forName((String) clazz);
        } else if (clazz instanceof Class) {
            realClazz = (Class<?>) clazz;
        } else {
            throw new IllegalArgumentException("Illegal clazz type: " + clazz.getClass());
        }
        Class<?> currClazz = realClazz;
        while (true) {
            try {
                Field field = currClazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                return field;
            } catch (NoSuchFieldException e) {
                if (currClazz.equals(Object.class)) {
                    throw e;
                }
                currClazz = currClazz.getSuperclass();
            }
        }
    }

    public static boolean isRealZipOrJar(File input) {
        ZipFile zf = null;
        try {
            zf = new ZipFile(input);
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            Util.closeQuietly(zf);
        }
    }


    /**
     * Close {@code target} quietly.
     *
     * @param obj
     *  Object to be closed.
     */
    public static void closeQuietly(Object obj) {
        if (obj == null) {
            return;
        }
        if (obj instanceof Closeable) {
            try {
                ((Closeable) obj).close();
            } catch (Throwable ignored) {
                // ignore
            }
        } else if (obj instanceof AutoCloseable) {
            try {
                ((AutoCloseable) obj).close();
            } catch (Throwable ignored) {
                // ignore
            }
        } else if (obj instanceof ZipFile) {
            try {
                ((ZipFile) obj).close();
            } catch (Throwable ignored) {
                // ignore
            }
        } else {
            throw new IllegalArgumentException("obj " + obj + " is not closeable");
        }
    }

    public static String readFileAsString(String filePath) {
        StringBuffer fileData = new StringBuffer();
        Reader fileReader = null;
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(filePath);
            fileReader = new InputStreamReader(inputStream, "UTF-8");
            char[] buf = new char[BUFFER_SIZE];
            int numRead = 0;
            while ((numRead = fileReader.read(buf)) != -1) {
                String readData = String.valueOf(buf, 0, numRead);
                fileData.append(readData);
            }
        } catch (Exception e) {
            Log.e(TAG, "file op readFileAsString e type:%s, e msg:%s, filePath:%s",
                  e.getClass().getSimpleName(), e.getMessage(), filePath);

        } finally {
            try {
                closeQuietly(fileReader);
                closeQuietly(inputStream);
            } catch (Exception e) {
                Log.e(TAG, "file op readFileAsString close e type:%s, e msg:%s, filePath:%s",
                      e.getClass().getSimpleName(), e.getMessage(), filePath);
            }
        }
        return fileData.toString();
    }

    public static void copyFileUsingStream(File source, File dest) throws IOException {
        FileInputStream is = null;
        FileOutputStream os = null;
        File parent = dest.getParentFile();
        if (parent != null && (!parent.exists())) {
            parent.mkdirs();
        }
        try {
            is = new FileInputStream(source);
            os = new FileOutputStream(dest, false);

            byte[] buffer = new byte[BUFFER_SIZE];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        } finally {
            closeQuietly(is);
            closeQuietly(os);
        }
    }

}
