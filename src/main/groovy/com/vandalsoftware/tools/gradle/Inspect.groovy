/*
 * Copyright (C) 2014 Vandal LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.vandalsoftware.tools.gradle

import com.vandalsoftware.tools.classfile.ClassCollector
import com.vandalsoftware.tools.classfile.ClassInfo
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * @author Jonathan Le
 */
class Inspect extends DefaultTask {
    public static final char CLASS_SEPARATOR_CHAR = '.' as char
    /**
     * Names of affected classes.
     */
    Set<String> affectedClasses

    @TaskAction
    void inspect() {
        Set srcRoots = srcRoots() as Set
        Set srcClassPaths = classpaths() as Set
        def inputs = input() as Set
        if (logger.isInfoEnabled()) {
            printDirs(srcRoots, 'srcRoots')
            printDirs(srcClassPaths, 'srcClassPaths')
        }
        // Keep track of unique classes being examined
        Set inputClasses = new LinkedHashSet(inputs)
        LinkedList classesToExamine = new LinkedList(inputs)
        Set inputClassNames = new LinkedHashSet()
        final ClassCollector sourceReader = new ClassCollector()
        srcClassPaths.each() { File dir ->
            sourceReader.collect(dir)
        }
        def examined = [] as HashSet
        while (!classesToExamine.isEmpty()) {
            String filePath = classesToExamine.remove()
            File fileToExamine = new File(filePath)
            if (!fileToExamine.isFile()) {
                continue
            }
            srcRoots.each() { File srcRoot ->
                if (filePath.startsWith(srcRoot.path)) {
                    logger.info "Examining $fileToExamine"
                    def fileName = fileToExamine.name
                    def fileExt = fileName.substring(fileName.lastIndexOf('.'))
                    String relFilePath = filePath.substring(srcRoot.path.length() + 1,
                            filePath.lastIndexOf('.')) + ".class"
                    srcClassPaths.each() { File cp ->
                        File f = new File(cp, relFilePath)
                        ClassInfo info = sourceReader.collectFile(f)
                        if (info != null) {
                            String cname = info.thisClassName
                            inputClassNames.add(cname)
                            collectInputs(sourceReader.findSubclasses(cname),
                                    srcRoot, fileExt, inputClasses, classesToExamine,
                                    { logger.info "  $it extends $cname" })
                            if (info.isInterface()) {
                                collectInputs(sourceReader.findImplementations(cname),
                                        srcRoot, fileExt, inputClasses, classesToExamine,
                                        { logger.info "  $it implements $cname" })
                            }
                        }
                    }
                    examined.add(filePath)
                }
            }
        }
        def skipped = []
        inputClasses.each() {
            if (!examined.contains(it)) {
                skipped.add(it)
            }
        }
        if (skipped.size() > 0) {
            logger.info "Not found in any source root:"
            skipped.each {
                logger.info "  $it"
            }
        }
        final ClassCollector targetReader = new ClassCollector()
        def targetClassPaths = targets()
        if (logger.isInfoEnabled()) {
            printDirs(targetClassPaths, 'targetClassPaths')
        }
        targetClassPaths.each() { File dir ->
            targetReader.collect(dir)
        }

        affectedClasses = new LinkedHashSet<>()

        // Check each file for usage of each input
        File[] used = targetReader.findUsages(inputClassNames)
        used.each() { f ->
            targetClassPaths.each() { File target ->
                if (f.path.startsWith(target.path)) {
                    def className = pathToClassName(target.path, f.path, ".class")
                    affectedClasses.add(className)
                }
            }
        }
    }

    /**
     * Collects class files to use as inputs and to examine.
     *
     * @param classes the class names to check
     * @param srcRoot the source root
     * @param inputClasses the collection to add class file paths. If the path already exists in
     * this collection, it will be skipped.
     * @param classesToExamine the paths of class files to examine in the next usages check
     * @param log logging closure to execute
     */
    private static void collectInputs(Collection<String> classes, File srcRoot, fileExt,
                                      inputClasses, classesToExamine, log) {
        classes.each() { className ->
            log(className)
            def path = classNameToPath(className, srcRoot.path, fileExt)
            if (!inputClasses.contains(path)) {
                inputClasses.add(path)
                classesToExamine.add(path)
            }
        }
    }

    private static String classNameToPath(String className, String basePath, String extension) {
        return new File(basePath, className.replace(CLASS_SEPARATOR_CHAR,
                File.separatorChar) + extension)
    }

    boolean checkInputs() {
        StringBuilder msg = new StringBuilder()
        def hasClasspaths = checkPathsExist(classpaths(), msg)
        if (!hasClasspaths) {
            msg.insert(0, "  No such classpaths exist:\n")
        }
        def hasTargets = checkPathsExist(targets(), msg)
        if (!hasTargets) {
            msg.insert(0, "  No such targets exist:\n")
        }
        def hasInput = input()
        if (!hasInput) {
            msg.insert(0, "  No inputs.")
        }
        def hasSrcRoots = checkPathsExist(srcRoots(), msg)
        if (!hasSrcRoots) {
            msg.insert(0, "  No such source roots exist:")
        }
        boolean ok = hasClasspaths && hasSrcRoots && hasInput && hasTargets
        if (!ok) {
            logger.lifecycle("Skipping ${name} because...\n$msg")
        }
        return ok
    }

    /**
     * Checks and returns {@code true} if at least one path exists.
     *
     * @param paths the paths to check
     * @param skipMsg the skip message to append the non-existent path
     * @return true if at least one path exists
     */
    private static boolean checkPathsExist(paths, skipMsg) {
        boolean atLeastOneExists = false
        StringBuilder missing = new StringBuilder()
        paths.each() { File dir ->
            if (dir.exists()) {
                atLeastOneExists = true
            } else {
                missing.append("  ").append(dir).append('\n')
            }
        }
        if (!atLeastOneExists) {
            skipMsg.append(missing)
        }
        return atLeastOneExists
    }

    private static String pathToClassName(String basePath, String path, String extension) {
        return path.substring(basePath.length() + 1,
                path.indexOf(extension)).replace(File.separatorChar, CLASS_SEPARATOR_CHAR)
    }

    private def printDirs(dirs, description) {
        println "Using ${description}:"
        dirs.each() {
            println("  ${it.path}" - project.projectDir.path)
        }
    }
}
