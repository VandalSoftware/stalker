package com.vandalsoftware.tools.gradle

import com.vandalsoftware.tools.ClassCollector
import com.vandalsoftware.tools.ClassInfo
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * @author Jonathan Le
 */
class Usages extends DefaultTask {
    public static final char CLASS_SEPARATOR_CHAR = '.' as char
    Set<File> files
    Set<String> classNames

    @TaskAction
    void usages() {
        Set srcRoots = srcRoots() as Set
        Set srcClassPaths = classpaths() as Set
        def inputs = input() as Set
        // Keep track of unique classes being examined
        Set inputClasses = new LinkedHashSet(inputs)
        LinkedList classesToExamine = new LinkedList(inputs)
        Set inputClassNames = new LinkedHashSet()
        final ClassCollector sourceReader = new ClassCollector();
        srcClassPaths.each() { File dir ->
            sourceReader.collect(dir);
        }
        while (!classesToExamine.isEmpty()) {
            String filePath = classesToExamine.remove()
            logger.info "Examining $filePath"
            srcRoots.each() { File srcRoot ->
                if (filePath.startsWith(srcRoot.path)) {
                    String relFilePath = filePath.substring(srcRoot.path.length() + 1,
                            filePath.indexOf(".java")) + ".class"
                    srcClassPaths.each() { File cp ->
                        File f = new File(cp, relFilePath)
                        ClassInfo info = sourceReader.collectFile(f)
                        if (info != null) {
                            String cname = info.thisClassName;
                            logger.info "$cname is an affected class"
                            inputClassNames.add(cname);
                            def subclasses = sourceReader.subclasses(cname)
                            subclasses.each() {
                                logger.info "-> $it extends $cname"
                                def path = classNameToPath(it, srcRoot.path, '.java')
                                if (!inputClasses.contains(path)) {
                                    inputClasses.add(path)
                                    classesToExamine.add(path)
                                }
                            }
                        }
                    }
                }
            }
        }
        final ClassCollector targetReader = new ClassCollector();
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
                    logger.info "Usage detected: $f"
                    classNames.add(pathToClassName(target.path, f.path, ".class"))
                }
            }
        }
        if (used.length == 0) {
            logger.lifecycle "No usages detected."
        }
    }

    private static String classNameToPath(String className, String basePath, String extension) {
        return new File(basePath, className.replace(CLASS_SEPARATOR_CHAR,
                File.separatorChar) + extension);
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
                path.indexOf(extension)).replace(File.separatorChar, CLASS_SEPARATOR_CHAR)
    }
}
