package com.vandalsoftware.tools;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * @author Jonathan Le
 */
public class ClassFileReader {
    private HashMap<File, ClassInfo> infoMap;

    public ClassFileReader() {
        this.infoMap = new HashMap<>();
    }

    public static void main(String[] args) {
        final BufferedReader buf = new BufferedReader(new InputStreamReader(System.in));
        final ArrayList<String> input = new ArrayList<>();
        try {
            input.add(buf.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeQuietly(buf);
        }

        final ClassFileReader reader = new ClassFileReader();
        reader.collect(args[0]);
        // Check each file for usage of each input
        final File[] files = reader.usages(input);
        for (File f : files) {
            System.out.println(f);
        }
    }

    private static void closeQuietly(Closeable c) {
        if (c != null) {
            try {
                c.close();
            } catch (IOException e) {
                e.printStackTrace();
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

    private void listFiles(File dir, Collection<File> c) {
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
                    listFiles(f, c);
                }
                c.add(f);
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
                case 1: { // CONSTANT_Utf8
                    strings[i] = in.readUTF();
                    break;
                }
                case 3: // CONSTANT_Integer
                case 4: { // CONSTANT_Float
                    in.readInt(); // bytes
                    break;
                }
                case 5: // CONSTANT_Long
                case 6: { // CONSTANT_Double
                    in.readInt(); // high_bytes
                    in.readInt(); // low_bytes
                    // Increment the index. 8-byte constants take up two entries because the i+1
                    // position is unusable. In Sun's own words, "In retrospect, making 8-byte
                    // constants take two constant pool entries was a poor choice."
                    i++;
                    break;
                }
                case 7: { // CONSTANT_Class
                    // name_index is one-based so offset by -1
                    classes[i] = (in.readShort() & 0xffff) - 1;
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
        final int accessFlags = in.readUnsignedShort();
        final int thisClass = in.readUnsignedShort();
        final String thisClassName = getClassName(strings[classes[thisClass - 1]]);
        final int superClass = classes[in.readUnsignedShort() - 1];
        final String superClassName;
        if (superClass > 0) {
            superClassName = getClassName(strings[superClass]);
        } else {
            superClassName = ClassInfo.JAVA_LANG_OBJECT;
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
                thisClassName, superClassName);
    }

    /**
     * Collect class file.
     */
    public ClassInfo collectFile(File f) {
        if (f.isFile()) {
            ClassInfo info = this.infoMap.get(f);
            if (info == null) {
                try {
                    info = readFile(f);
                    this.infoMap.put(f, info);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return info;
        }
        return null;
    }

    /**
     * Collect class files in a directory.
     */
    public void collect(File dir) {
        if (!dir.isDirectory()) {
            return;
        }
        final ArrayList<File> files = new ArrayList<>();
        listFiles(dir, files);
        for (File f : files) {
            collectFile(f);
        }
    }

    /**
     * Collect class files in a path.
     */
    public void collect(String path) {
        collect(new File(path));
    }

    /**
     * Check for usages of a single class name.
     */
    public File[] usages(String className) {
        final HashSet<File> usages = new HashSet<>();
        for (Map.Entry<File, ClassInfo> entry : this.infoMap.entrySet()) {
            if (entry.getValue().check(className)) {
                usages.add(entry.getKey());
            }
        }
        return usages.toArray(new File[usages.size()]);
    }

    /**
     * Check for usages of collection of class names.
     */
    public File[] usages(Collection<String> classNames) {
        final HashSet<File> usages = new HashSet<>();
        for (String className : classNames) {
            for (Map.Entry<File, ClassInfo> entry : this.infoMap.entrySet()) {
                final ClassInfo info = entry.getValue();
                final File file = entry.getKey();
                if (info.check(className)) {
                    usages.add(file);
                }
            }
        }
        return usages.toArray(new File[usages.size()]);
    }

    public Collection<String> subclasses(String className) {
        final HashSet<String> subclasses = new HashSet<>();
        for (ClassInfo info : this.infoMap.values()) {
            if (info.superClassName.equals(className)) {
                subclasses.add(info.thisClassName);
            }
        }
        return subclasses;
    }
}
