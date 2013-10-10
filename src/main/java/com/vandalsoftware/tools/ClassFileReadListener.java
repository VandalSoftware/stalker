package com.vandalsoftware.tools;

/**
 * @author Jonathan Le
 */
public interface ClassFileReadListener {
    void onReadAccessFlags(int accessFlags);

    /**
     * @param cpIndex this is the CONSTANT pool index; not the name index. The index here points to
     * a CONSTANT_Class_info structure.
     */
    void onReadThisClass(int cpIndex);

    /**
     * @param cpIndex this is the CONSTANT pool index; not the name index. The index here points to
     * a CONSTANT_Class_info structure.
     */
    void onReadSuperClass(int cpIndex);

    void onReadClass(int cpIndex, int nameIndex);

    void onReadUtf8(int cpIndex, String string);

    void onReadClassFileInfo(int magic, int minorVersion, int majorVersion, int constantPoolCount);

    void onReadFinished();
}
