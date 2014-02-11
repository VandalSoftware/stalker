package com.vandalsoftware.tools;

import java.util.Collection;

/**
 * @author Jonathan Le
 */
public class ClassInfo {
    static final String JAVA_LANG_OBJECT = "java.lang.Object";
    public final int minorVersion;
    public final int majorVersion;
    public final String[] strings;
    public final int accessFlags;
    public final Collection<String> classNames;
    public final String thisClassName;
    public final String superClassName;

    ClassInfo(int minorVersion, int majorVersion, String[] strings, Collection<String> classNames,
              int accessFlags, String thisClassName, String superClassName) {
        this.minorVersion = minorVersion;
        this.majorVersion = majorVersion;
        this.strings = strings;
        this.thisClassName = thisClassName;
        this.superClassName = superClassName;
        this.accessFlags = accessFlags;
        this.classNames = classNames;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (String name : this.classNames) {
            sb.append(name).append('\n');
        }
        return sb.toString();
    }

    /**
     * @return true if
     */
    public boolean check(String className) {
        return this.classNames != null && this.classNames.contains(className);
    }

    /**
     * @return true if this class' superclass is not java.lang.Object.
     */
    public boolean hasSuperClass() {
        return !JAVA_LANG_OBJECT.equals(this.superClassName);
    }
}
