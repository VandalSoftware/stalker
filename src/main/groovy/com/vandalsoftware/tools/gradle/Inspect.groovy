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
        StalkerConfiguration config = configuration()
        Set<File> srcRoots = config.srcRoots
        Set<File> srcClassPaths = config.srcClassPaths
        Set<File> inputs = input() as Set
        if (logger.isInfoEnabled()) {
            printDirs(srcRoots, 'srcRoots')
            printDirs(srcClassPaths, 'srcClassPaths')
        }
        // Keep track of unique classes being examined
        Set<File> inputClasses = new LinkedHashSet(inputs)
        LinkedList<File> classesToExamine = new LinkedList(inputs)
        Set<String> inputClassNames = new LinkedHashSet()
        final ClassCollector sourceReader = new ClassCollector()
        srcClassPaths.each() { File dir ->
            sourceReader.collect(dir)
        }
        def examined = [] as HashSet
        while (!classesToExamine.isEmpty()) {
            File fileToExamine = classesToExamine.remove()
            if (!fileToExamine.isFile()) {
                continue
            }
            String filePath = fileToExamine.absolutePath
            srcRoots.each() { File srcRoot ->
                if (filePath.startsWith(srcRoot.absolutePath)) {
                    logger.info "Examining $fileToExamine"
                    def fileName = fileToExamine.name
                    def fileExt = fileName.substring(fileName.lastIndexOf('.'))
                    String relFilePath = filePath.substring(srcRoot.absolutePath.length() + 1,
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
                    examined.add(fileToExamine)
                }
            }
        }
        final ClassCollector targetReader = new ClassCollector()
        def targetClassPaths = config.targetClassPaths
        if (logger.isInfoEnabled()) {
            printDirs(targetClassPaths, 'targetClassPaths')
        }
        targetClassPaths.each() { File dir ->
            targetReader.collect(dir)
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

        affectedClasses = new LinkedHashSet<>()

        // Check each file for usage of each input
        File[] used = targetReader.findUsages(inputClassNames)
        used.each() { f ->
            targetClassPaths.each() { File target ->
                if (f.absolutePath.startsWith(target.absolutePath)) {
                    def className = f.absolutePath.substring(target.absolutePath.length() + 1,
                            f.absolutePath.indexOf(".class")).replace(File.separatorChar, CLASS_SEPARATOR_CHAR)
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
            File classFile = new File(srcRoot.absolutePath, className.replace(CLASS_SEPARATOR_CHAR,
                    File.separatorChar) + fileExt)
            if (!inputClasses.contains(classFile)) {
                inputClasses.add(classFile)
                classesToExamine.add(classFile)
            }
        }
    }

    boolean checkInputs() {
        StringBuilder msg = new StringBuilder()
        StalkerConfiguration config = configuration()
        def hasClasspaths = checkPathsExist(config.srcClassPaths, msg)
        if (!hasClasspaths) {
            msg.insert(0, "  No such classpaths exist:\n")
        }
        def hasTargets = checkPathsExist(config.targetClassPaths, msg)
        if (!hasTargets) {
            msg.insert(0, "  No such targets exist:\n")
        }
        def hasInput = input()
        if (!hasInput) {
            msg.insert(0, "  No inputs.")
        }
        def hasSrcRoots = checkPathsExist(config.srcRoots, msg)
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


    private def printDirs(dirs, description) {
        println "Using ${description}:"
        dirs.each() { File f ->
            println("  ${f.absolutePath}" - (project.projectDir.absolutePath + File.separator))
        }
    }
}
