package com.vandalsoftware.tools;

import java.util.HashSet;

/**
 * @author Jonathan Le
 */
class ClassInfo implements ClassFileReadListener {
    private static final String JAVA_LANG_OBJECT = "java.lang.Object";
    private String[] strings;
    private int[] classes;
    private HashSet<String> classNames;
    private String thisClassName;
    private String superClassName;

    /**
     * Convert a class specified as a field descriptor into a fully-qualified class name.
     */
    public static String getClassName(String fieldDescriptor) {
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

    @Override
    public void onReadAccessFlags(int accessFlags) {
    }

    @Override
    public void onReadThisClass(int cpIndex) {
        this.thisClassName = getClassName(this.strings[this.classes[cpIndex - 1]]);
    }

    @Override
    public void onReadSuperClass(int cpIndex) {
        final int index = this.classes[cpIndex - 1];
        if (index > 0) {
            this.superClassName = getClassName(this.strings[index]);
        } else {
            this.superClassName = JAVA_LANG_OBJECT;
        }
    }

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
                final String classDescriptor = this.strings[index];
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
                if (name.equals(className)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @return true if this class' superclass is not java.lang.Object.
     */
    public boolean hasSuperClass() {
        return !JAVA_LANG_OBJECT.equals(this.superClassName);
    }

    public String getSuperClass() {
        return this.superClassName;
    }
}
