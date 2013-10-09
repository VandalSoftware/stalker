package com.vandalsoftware.tools.gradle

import com.vandalsoftware.tools.ClassFileReader
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * @author Jonathan Le
 */
class Usages extends DefaultTask {
    Set<File> files
    Set<String> classNames

    @TaskAction
    void usages() {
        def srcRoots = srcRoots()
        Set set = new LinkedHashSet()
        def classes = input()
        classes.each() { String filePath ->
            srcRoots.each() { File srcRoot ->
                if (filePath.startsWith(srcRoot.path)) {
                    set.add(transform(srcRoot.path, filePath, ".java"))
                }
            }
        }
        final ClassFileReader reader = new ClassFileReader();
        def targets = targets()
        targets.each() { File dir ->
            reader.collect(dir);
        }
        // Check each file for usage of each input
        File[] used = reader.usages(set);
        classNames = new LinkedHashSet<>()
        used.each() { f ->
            targets.each() { File target ->
                if (f.path.startsWith(target.path)) {
                    classNames.add(transform(target.path, f.path, ".class"))
                }
            }
        }
    }

    boolean checkInputs() {
        return srcRoots() && input() && checkTargets()
    }

    private boolean checkTargets() {
        boolean run = false
        targets().each() { File dir ->
            if (dir.exists()) {
                run = true
            }
        }
        return run
    }

    private static String transform(String basePath, String path, String extension) {
        return path.substring(basePath.length() + 1,
                path.indexOf(extension)).replace('/', '.')
    }
}
