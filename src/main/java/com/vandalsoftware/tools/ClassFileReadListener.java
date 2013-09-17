package com.vandalsoftware.tools;

/**
 * @author Jonathan Le
 */
public interface ClassFileReadListener {
    void onReadClass(int cpIndex, int nameIndex);

    void onReadUtf8(int cpIndex, String string);

    void onReadClassFileInfo(int magic, int minorVersion, int majorVersion, int constantPoolCount);

    void onReadFinished();
}
