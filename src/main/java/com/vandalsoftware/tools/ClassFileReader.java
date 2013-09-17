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
import java.util.Map;

/**
 * @author Jonathan Le
 */
public class ClassFileReader {
    private HashMap<File, ClassNameCollector> collectorMap;

    public ClassFileReader() {
        this.collectorMap = new HashMap<File, ClassNameCollector>();
    }

    public static void main(String[] args) {
        final ClassFileReader reader = new ClassFileReader();
        reader.read(args[0]);
        // Check each file for usage of each input
        String[] input = new String[args.length - 1];
        System.arraycopy(args, 1, input, 0, input.length);
        final File[] files = reader.usages(input);
        for (File f : files) {
            System.out.println(f);
        }
    }

    private void listFiles(String path, Collection<File> c) {
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

    private void readClassFile(File f, ClassFileReadListener l) {
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

    public void read(String path) {
        final ArrayList<File> files = new ArrayList<File>();
        listFiles(path, files);
        for (File f : files) {
            if (f.isFile()) {
                final ClassNameCollector collector = new ClassNameCollector();
                readClassFile(f, collector);
                this.collectorMap.put(f, collector);
            }
        }
    }

    public File[] usages(String[] classNames) {
        final ArrayList<File> usages = new ArrayList<File>();
        for (String className : classNames) {
            for (Map.Entry<File, ClassNameCollector> entry : this.collectorMap.entrySet()) {
                if (entry.getValue().check(className)) {
                    usages.add(entry.getKey());
                }
            }
        }
        return usages.toArray(new File[usages.size()]);
    }
}
