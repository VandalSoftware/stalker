/*
 * Copyright (C) 2014 Vandal LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.vandalsoftware.tools.classfile;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;

class ClassFileReader {
    /**
     * CONSTANT_Utf8 constant pool tag.
     */
    public static final int CONSTANT_UTF_8 = 1;
    /**
     * CONSTANT_Integer constant pool tag.
     */
    public static final int CONSTANT_INTEGER = 3;
    /**
     * CONSTANT_Float constant pool tag.
     */
    public static final int CONSTANT_FLOAT = 4;
    /**
     * CONSTANT_Long constant pool tag.
     */
    public static final int CONSTANT_LONG = 5;
    /**
     * CONSTANT_Double constant pool tag.
     */
    public static final int CONSTANT_DOUBLE = 6;
    /**
     * CONSTANT_Class constant pool tag.
     */
    public static final int CONSTANT_CLASS = 7;
    /**
     * CONSTANT_String constant pool tag.
     */
    public static final int CONSTANT_STRING = 8;
    /**
     * CONSTANT_Fieldref constant pool tag.
     */
    public static final int CONSTANT_FIELDREF = 9;
    /**
     * CONSTANT_Methodref constant pool tag.
     */
    public static final int CONSTANT_METHODREF = 10;
    /**
     * CONSTANT_InterfaceMethodref constant pool tag.
     */
    public static final int CONSTANT_INTERFACE_METHODREF = 11;
    /**
     * CONSTANT_NameAndType constant pool tag.
     */
    public static final int CONSTANT_NAME_AND_TYPE = 12;
    /**
     * CONSTANT_MethodHandle constant pool tag.
     */
    public static final int CONSTANT_METHOD_HANDLE = 15;
    /**
     * CONSTANT_MethodType constant pool tag.
     */
    public static final int CONSTANT_METHOD_TYPE = 16;
    /**
     * CONSTANT_InvokeDynamic constant pool tag.
     */
    public static final int CONSTANT_INVOKE_DYNAMIC = 18;

    public ClassFileReader() {
    }

    /**
     * Convert a class specified as a field descriptor into a fully-qualified class name.
     */
    private static String getClassName(String fieldDescriptor) {
        final int length = fieldDescriptor.length();
        int i;
        for (i = 0; i < length; i++) {
            final char c = fieldDescriptor.charAt(i);
            if (c != 'B' && c != 'C' && c != 'D' && c != 'F' && c != 'I' && c != 'J' && c != 'S' &&
                    c != 'Z' && c != 'L' && c != '[') {
                break;
            }
        }
        if (i < length) {
            int end = fieldDescriptor.length();
            // Exclude semicolon from class descriptors
            if (fieldDescriptor.charAt(end - 1) == ';') {
                end -= 1;
            }
            return fieldDescriptor.substring(i, end).replace('/', '.');
        } else {
            return "";
        }
    }

    private static void closeQuietly(Closeable c) {
        if (c != null) {
            try {
                c.close();
            } catch (IOException ignored) {
            }
        }
    }

    public ClassInfo readFile(File f) throws IOException {
        FileInputStream stream = null;
        try {
            stream = new FileInputStream(f);
            return readInputStream(stream);
        } finally {
            closeQuietly(stream);
        }
    }

    public ClassInfo readInputStream(InputStream stream) throws IOException {
        DataInputStream in = new DataInputStream(stream);
        // Skip magic, minor_version, and major_version
        final int magic = in.readInt();
        if (magic != 0xCAFEBABE) {
            throw new IOException("Not a class file");
        }
        final int minorVersion = in.readUnsignedShort();
        final int majorVersion = in.readUnsignedShort();
        final int constantPoolCount = (in.readUnsignedShort() & 0xffff) - 1;
        final String[] strings = new String[constantPoolCount];
        final int[] classes = new int[constantPoolCount];
        for (int i = 0; i < constantPoolCount; i++) {
            final int tag = in.readUnsignedByte();
            switch (tag) {
                case CONSTANT_UTF_8: {
                    strings[i] = in.readUTF();
                    break;
                }
                case CONSTANT_INTEGER:
                case CONSTANT_FLOAT: {
                    in.readInt(); // bytes
                    break;
                }
                case CONSTANT_LONG:
                case CONSTANT_DOUBLE: {
                    in.readInt(); // high_bytes
                    in.readInt(); // low_bytes
                    // Increment the index. 8-byte constants take up two entries because the i+1
                    // position is unusable. In Sun's own words, "In retrospect, making 8-byte
                    // constants take two constant pool entries was a poor choice."
                    i++;
                    break;
                }
                case CONSTANT_CLASS: {
                    // name_index is one-based so offset by -1
                    classes[i] = (in.readShort() & 0xffff) - 1;
                    break;
                }
                case CONSTANT_STRING: {
                    in.readUnsignedShort(); // string_index
                    break;
                }
                case CONSTANT_FIELDREF:
                case CONSTANT_METHODREF:
                case CONSTANT_INTERFACE_METHODREF: {
                    in.readUnsignedShort(); // class_index
                    in.readUnsignedShort(); // name_and_type_index
                    break;
                }
                case CONSTANT_NAME_AND_TYPE: {
                    in.readShort(); // name_index
                    in.readShort(); // descriptor_index
                    break;
                }
                case CONSTANT_METHOD_HANDLE: {
                    in.readUnsignedByte(); // reference_kind
                    in.readUnsignedShort(); // reference_index
                    break;
                }
                case CONSTANT_METHOD_TYPE: {
                    in.readUnsignedShort(); // descriptor_index
                    break;
                }
                case CONSTANT_INVOKE_DYNAMIC: {
                    in.readUnsignedShort(); // bootstrap_method_attr_index
                    in.readUnsignedShort(); // name_and_type_index
                    break;
                }
            }
        }
        final int accessFlags = in.readUnsignedShort();
        final int thisClass = in.readUnsignedShort() - 1;
        final String thisClassName = getClassName(strings[classes[thisClass]]);
        final int superClass = classes[in.readUnsignedShort() - 1];
        final String superClassName;
        if (superClass > 0) {
            superClassName = getClassName(strings[superClass]);
        } else {
            superClassName = ClassInfo.JAVA_LANG_OBJECT;
        }
        final int interfacesCount = in.readUnsignedShort();
        final ArrayList<String> interfaceNames = new ArrayList<>();
        for (int i = 0; i < interfacesCount; i++) {
            final int interfaceIndex = in.readUnsignedShort() - 1;
            interfaceNames.add(getClassName(strings[classes[interfaceIndex]]));
        }
        final HashSet<String> classNames = new HashSet<>();
        for (int index : classes) {
            if (index != 0) {
                final String classDescriptor = strings[index];
                final String name = getClassName(classDescriptor);
                if (!"".equals(name)) {
                    classNames.add(name);
                }
            }
        }
        return new ClassInfo(minorVersion, majorVersion, strings, classNames, accessFlags,
                thisClassName, superClassName, interfaceNames);
    }
}