package com.vandalsoftware.tools.gradle

import com.vandalsoftware.tools.ClassFileReader
import com.vandalsoftware.tools.ClassInfo
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
        def srcClassPaths = classpaths()
        def classes = input()
        Set inputClassNames = new LinkedHashSet()
        final ClassFileReader sourceReader = new ClassFileReader();
        srcClassPaths.each() { File dir ->
            sourceReader.collect(dir);
        }
        classes.each() { String filePath ->
            srcRoots.each() { File srcRoot ->
                if (filePath.startsWith(srcRoot.path)) {
                    String relFilePath = filePath.substring(srcRoot.path.length() + 1,
                            filePath.indexOf(".java")) + ".class"
                    srcClassPaths.each() { File cp ->
                        File f = new File(cp, relFilePath)
                        ClassInfo info = sourceReader.collectFile(f)
                        if (info != null) {
                            String cname = info.getThisClassName()
                            logger.debug(cname + " is an affected class")
                            inputClassNames.add(cname);
                            def subclasses = sourceReader.subclasses(cname)
                            subclasses.each() {
                                logger.debug(it + " is an affected subclass")
                            }
                            inputClassNames.addAll(subclasses)
                        }
                    }
                }
            }
        }
        final ClassFileReader targetReader = new ClassFileReader();
        def targetClassPaths = targets()
        targetClassPaths.each() { File dir ->
            targetReader.collect(dir);
        }
        // Check each file for usage of each input
        File[] used = targetReader.usages(inputClassNames);
        classNames = new LinkedHashSet<>()
        used.each() { f ->
            targetClassPaths.each() { File target ->
                if (f.path.startsWith(target.path)) {
                    classNames.add(pathToClassName(target.path, f.path, ".class"))
                }
            }
        }
    }

    boolean checkInputs() {
        return classpaths() && srcRoots() && input() && checkTargets()
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

    private static String pathToClassName(String basePath, String path, String extension) {
        return path.substring(basePath.length() + 1,
                path.indexOf(extension)).replace('/', '.')
    }
}
