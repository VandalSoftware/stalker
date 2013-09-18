package com.vandalsoftware.tools.gradle

/**
 * @author Jonathan Le
 */
class StalkerExtension {
    Set<File> srcRoots
    Set<File> targetClassPaths
    String refId
    OutputStream standardOutput

    StalkerExtension() {
        srcRoots = new LinkedHashSet<>()
        targetClassPaths = new LinkedHashSet<>()
    }

    void srcRoot(String path) {
        srcRoots.add(new File(path))
    }

    void srcRoot(File dir) {
        srcRoots.add(dir)
    }

    void targetClassPath(String path) {
        targetClassPaths.add(new File(path))
    }

    void targetClassPath(File dir) {
        targetClassPaths.add(dir)
    }
}
