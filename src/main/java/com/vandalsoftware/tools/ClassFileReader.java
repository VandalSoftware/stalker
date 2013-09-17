package com.vandalsoftware.tools;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * @author Jonathan Le
 */
public class ClassFileReader {
    public static void main(String[] args) {
        final String path = args[0];
        final ArrayList<File> files = new ArrayList<File>();
        HashMap<File, ClassNameCollector> collectorMap = new HashMap<File, ClassNameCollector>();
        listFiles(path, files);
        for (File f : files) {
            if (f.isFile()) {
                final ClassNameCollector collector = new ClassNameCollector();
                readClassFile(f, collector);
                collectorMap.put(f, collector);
            }
        }
        // Check each file for usage of each input
        final int length = args.length;
        for (int i = 1; i < length; i++) {
            for (Map.Entry<File, ClassNameCollector> entry : collectorMap.entrySet()) {
                if (entry.getValue().check(args[i])) {
                    System.out.println(entry.getKey() + " uses " + args[i]);
                }
            }
        }
    }

    private static void readClassFile(File f, ClassFileReadListener l) {
        FileInputStream stream = null;
        try {
            stream = new FileInputStream(f);
            DataInputStream in = new DataInputStream(stream);
            // Skip magic, minor_version, and major_version
            final int magic = in.readInt();
            final int minorVersion = in.readUnsignedShort();
            final int majorVersion = in.readUnsignedShort();
            final int constantPoolCount = in.readUnsignedShort();
            l.onReadClassFileInfo(magic, minorVersion, majorVersion, constantPoolCount);
            for (int i = 0; i < constantPoolCount; i++) {
                final int tag = in.readUnsignedByte();
                switch (tag) {
                    case 1: { // CONSTANT_Utf8
                        final String s = in.readUTF();
                        l.onReadUtf8(i, s);
                        break;
                    }
                    case 3: // CONSTANT_Integer
                    case 4: { // CONSTANT_Float
                        in.readInt(); // bytes
                    }
                    case 5: // CONSTANT_Long
                    case 6: { // CONSTANT_Double
                        in.readInt(); // high_bytes
                        in.readInt(); // low_bytes
                    }
                    case 7: { // CONSTANT_Class
                        final int nameIndex = in.readUnsignedShort(); // name_index
                        l.onReadClass(i, nameIndex);
                        break;
                    }
                    case 8: { // CONSTANT_String
                        in.readUnsignedShort(); // string_index
                        break;
                    }
                    case 9: // CONSTANT_Fieldref
                    case 10: // CONSTANT_Methodref
                    case 11: { // CONSTANT_InterfaceMethodref
                        in.readUnsignedShort(); // class_index
                        in.readUnsignedShort(); // name_and_type_index
                        break;
                    }
                    case 12: { // CONSTANT_NameAndType
                        in.readShort(); // name_index
                        in.readShort(); // descriptor_index
                        break;
                    }
                    case 15: { // CONSTANT_MethodHandle
                        in.readUnsignedByte(); // reference_kind
                        in.readUnsignedShort(); // reference_index
                        break;
                    }
                    case 16: { // CONSTANT_MethodType
                        in.readUnsignedShort(); // descriptor_index
                        break;
                    }
                    case 18: { // CONSTANT_InvokeDynamic
                        in.readUnsignedShort(); // bootstrap_method_attr_index
                        in.readUnsignedShort(); // name_and_type_index
                        break;
                    }
                }
            }
            l.onReadFinished();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void listFiles(String path, Collection<File> c) {
        final File dir = new File(path);
        if (dir.exists()) {
            final File[] files = dir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File file, String s) {
                    final File f = new File(file, s);
                    return f.isDirectory() || s.endsWith(".class");
                }
            });
            for (File f : files) {
                if (f.isDirectory()) {
                    listFiles(f.getPath(), c);
                }
                c.add(f);
            }
        }
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
            if (fieldDescriptor.indexOf(';') != -1) {
                end -= 1;
            }
            return fieldDescriptor.substring(i, end).replace('/', '.');
        } else {
            return "";
        }
    }

    private static class ClassNameCollector implements ClassFileReadListener {
        private String[] strings;
        private int[] classes;
        private HashSet<String> classNames;

        @Override
        public void onReadClass(int cpIndex, int nameIndex) {
            this.classes[cpIndex] = nameIndex;
        }

        @Override
        public void onReadUtf8(int cpIndex, String string) {
            this.strings[cpIndex] = string;
        }

        @Override
        public void onReadClassFileInfo(int magic, int minorVersion, int majorVersion,
                                        int constantPoolCount) {
            this.strings = new String[constantPoolCount];
            this.classes = new int[constantPoolCount];
        }

        @Override
        public void onReadFinished() {
            this.classNames = new HashSet<String>();
            for (int index : this.classes) {
                if (index != 0) {
                    final String classDescriptor = this.strings[index - 1];
                    final String name = getClassName(classDescriptor);
                    if (!"".equals(name)) {
                        this.classNames.add(name);
                    }
                }
            }
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (String name : this.classNames) {
                sb.append(name).append('\n');
            }
            return sb.toString();
        }

        public boolean check(String className) {
            if (this.classNames != null) {
                for (String name : this.classNames) {
                    if (name.startsWith(className)) {
                        return true;
                    }
                }
            }
            return false;
        }
    }
}
