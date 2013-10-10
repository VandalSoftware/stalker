package com.vandalsoftware.tools.gradle

/**
 * @author Jonathan Le
 */
class StalkerExtension {
    Set<File> srcRoots
    Set<File> targetClassPaths
    Set<File> srcClassPaths
    String revision
    OutputStream standardOutput

    Closure afterStalk

    StalkerExtension() {
        srcRoots = new LinkedHashSet<>()
        targetClassPaths = new LinkedHashSet<>()
        srcClassPaths = new LinkedHashSet<>()
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

    void srcClassPath(String path) {
        srcClassPaths.add(new File(path))
    }

    void srcClassPath(File dir) {
        srcClassPaths.add(dir)
    }

    void afterStalk(Closure c) {
        afterStalk = c
    }
}
