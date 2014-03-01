package com.vandalsoftware.tools.classfile;

import java.util.Collection;

/**
 * @author Jonathan Le
 */
public class ClassInfo {
    static final String JAVA_LANG_OBJECT = "java.lang.Object";
    /**
     * Declared public; may be accessed from outside its package.
     */
    private static final int ACC_PUBLIC = 0x0001;
    /**
     * Declared final; no subclasses allowed.
     */
    private static final int ACC_FINAL = 0x0010;
    /**
     * Treat superclass methods specially when invoked by the invokespecial instruction.
     */
    private static final int ACC_SUPER = 0x0020;
    /**
     * Is an interface, not a class.
     */
    private static final int ACC_INTERFACE = 0x0200;
    /**
     * Declared abstract; must not be instantiated.
     */
    private static final int ACC_ABSTRACT = 0x0400;
    /**
     * Declared synthetic; not present in the source code.
     */
    private static final int ACC_SYNTHETIC = 0x1000;
    /**
     * Declared as an annotation type.
     */
    private static final int ACC_ANNOTATION = 0x2000;
    /**
     * Declared as an enum type.
     */
    private static final int ACC_ENUM = 0x4000;
    public final int minorVersion;
    public final int majorVersion;
    public final String[] strings;
    public final int accessFlags;
    public final Collection<String> classNames;
    public final String thisClassName;
    public final String superClassName;
    private final Collection<String> interfaceNames;

    ClassInfo(int minorVersion, int majorVersion, String[] strings, Collection<String> classNames,
              int accessFlags, String thisClassName, String superClassName,
              Collection<String> interfaceNames) {
        this.minorVersion = minorVersion;
        this.majorVersion = majorVersion;
        this.strings = strings;
        this.thisClassName = thisClassName;
        this.superClassName = superClassName;
        this.accessFlags = accessFlags;
        this.classNames = classNames;
        this.interfaceNames = interfaceNames;
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
     * Checks if this class uses a class.
     *
     * @param className the fully-qualified class name to check
     * @return true if this class references className
     */
    public boolean usesClass(String className) {
        return this.classNames != null && this.classNames.contains(className);
    }

    /**
     * @return true if this class' superclass is not {@link java.lang.Object}.
     */
    public boolean isSubclass() {
        return !JAVA_LANG_OBJECT.equals(this.superClassName);
    }

    public boolean isPublic() {
        return (this.accessFlags & ACC_PUBLIC) != 0;
    }

    public boolean isFinal() {
        return (this.accessFlags & ACC_FINAL) != 0;
    }

    public boolean isInterface() {
        return (this.accessFlags & ACC_INTERFACE) != 0;
    }

    public boolean isAbstract() {
        return (this.accessFlags & ACC_ABSTRACT) != 0;
    }

    public Collection<String> getInterfaces() {
        return this.interfaceNames;
    }
}
