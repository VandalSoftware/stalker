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

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task

/**
 * @author Jonathan Le
 */
class StalkerPlugin implements Plugin<Project> {
    StalkerExtension extension

    @Override
    void apply(Project project) {
        extension = project.extensions.create("stalker", StalkerExtension)
        Task changesTask = project.task([type: GetChangedFiles], "changes", {
            def ref = extension.getRevision()
            args "-r"
            if (ref) {
                args ref
            } else {
                args "HEAD"
            }
            standardOutput = new ByteArrayOutputStream()
            ext.output = {
                String out = standardOutput.toString()
                out.split("\n") as Set
            }
        })
        Task stalkTask = project.task([type: Usages, dependsOn: changesTask], "stalk", {
            def stalkerExtensionDefaults = new StalkerExtension()
            project.configure(project) {
                if (it.extensions.findByName('android') &&
                        canSetStalkerExtensionDefaults(project)) {
                    project.android.sourceSets.each() {
                        setSrcRoot(it.java.getSrcDirs(), stalkerExtensionDefaults)
                    }

                    gradle.taskGraph.whenReady { taskGraph ->
                        if (android.productFlavors.size() > 0) {
                            for (pf in android.productFlavors) {
                                setClassPaths(project, pf, android.buildTypes, stalkerExtensionDefaults)
                            }
                        } else {
                            setClassPaths(project, null, android.buildTypes, stalkerExtensionDefaults)
                        }
                    }
                } else if (it.plugins.hasPlugin('java')) {
                    project.sourceSets.each() {
                        setSrcRoot(it.java.getSrcDirs(), stalkerExtensionDefaults)
                        setSrcClassPath(it.output.classesDir, stalkerExtensionDefaults)
                    }
                    setTargetClassPath(project.sourceSets.test.output.classesDir,
                        stalkerExtensionDefaults)
                }
            }

            ext.srcRoots = {
                def srcRoots
                if (extension.getSrcRoots().size() == 0) {
                    srcRoots = stalkerExtensionDefaults.getSrcRoots()
                } else {
                    srcRoots = extension.getSrcRoots()
                }
                project.logger.info("Using srcRoots:")
                srcRoots.each() {
                    project.logger.info("\t${it.path}")
                }
                return srcRoots
            }
            ext.classpaths = {
                def srcClassPaths
                if (extension.getSrcClassPaths().size() == 0) {
                    srcClassPaths = stalkerExtensionDefaults.getSrcClassPaths()
                } else {
                    srcClassPaths = extension.getSrcClassPaths()
                }
                project.logger.info("Using srcClassPaths:")
                srcClassPaths.each() {
                    project.logger.info("\t${it.path}")
                }
                return srcClassPaths
            }
            ext.input = {
                return changesTask.output()
            }
            ext.targets = {
                def targetClassPaths
                if (extension.getTargetClassPaths().size() == 0) {
                    targetClassPaths = stalkerExtensionDefaults.getTargetClassPaths()
                } else {
                    targetClassPaths = extension.getTargetClassPaths()
                }
                project.logger.info("Using targetClassPaths:")
                targetClassPaths.each() {
                    project.logger.info("\t${it.path}")
                }
                return targetClassPaths
            }
            description = "Analyze class usage"
            group = "Analyze"
        }) << {
            if (extension.afterStalk) {
                extension.afterStalk(classNames)
            } else {
                if (extension.standardOutput != null) {
                    PrintStream out = new PrintStream(extension.standardOutput, true)
                    classNames.each() {
                        out.println(it)
                    }
                    out.close()
                } else {
                    classNames.each() {
                        println(it);
                    }
                }
            }
        }
        stalkTask.onlyIf {
            it.checkInputs()
        }
    }

    /**
     * @return true if StalkerExtension defaults can be set on the android project
     */
    def canSetStalkerExtensionDefaults(project) {
        def androidPluginVersion = []
        project.buildscript.configurations.classpath.resolvedConfiguration.firstLevelModuleDependencies.each() {
            if ('com.android.tools.build' == it.moduleGroup) {
                androidPluginVersion = it.moduleVersion
            }
        }

        // StalkerExtension defaults can only be applied if the android project is using
        // plugin version 0.9.0 and above.
        return isVersionNewer(androidPluginVersion, '0.9.0')
    }

    /**
     * @return true if version1 is newer than version2
     */
    def isVersionNewer(version1, version2) {
        def version1Array = version1.split("\\.")
        def version2Array = version2.split("\\.")
        def length = Math.min(version1Array.size(), version2Array.size())

        def isNewer = false
        for (int i = 0; i < length; i++) {
            if (version1Array[i].isInteger() && version2Array[i].isInteger()) {
                if (version1Array[i].toInteger() >= version2Array[i].toInteger()) {
                    isNewer = true
                } else {
                    isNewer = false
                    break
                }
            } else {
                // cannot compare
                break
            }
        }
        return isNewer
    }

    def setSrcRoot(srcDirs, stalkerExt) {
        for (d in srcDirs) {
            stalkerExt.srcRoot d
        }
    }

    def setClassPaths(project, productFlavor, buildTypes, stalkerExt) {
        for (bt in buildTypes) {
            def srcClassPath = getSrcClassPath(project, productFlavor, bt)
            setSrcClassPath(srcClassPath, stalkerExt)

            def targetClassPath = getTargetClassPath(project, productFlavor, bt)
            setTargetClassPath(targetClassPath, stalkerExt)
        }
    }

    def setSrcClassPath(srcClassPath, stalkerExt) {
        stalkerExt.srcClassPath srcClassPath
    }

    def setTargetClassPath(targetClassPath, stalkerExt) {
        stalkerExt.targetClassPath targetClassPath
    }

    def getSrcClassPath(project, productFlavor, buildType) {
        if (productFlavor != null) {
            return constructClassPath([project.buildDir.path, 'classes', productFlavor.name,
                                       buildType.name])
        } else {
            return constructClassPath([project.buildDir.path, 'classes', buildType.name])
        }
    }

    def getTargetClassPath(project, productFlavor, buildType) {
        if (productFlavor != null) {
            return constructClassPath([project.buildDir.path, 'classes', 'test',
                                       productFlavor.name, buildType.name])
        } else {
            return constructClassPath([project.buildDir.path, 'classes', 'test', buildType.name])
        }
    }

    def constructClassPath(names) {
        def path = ''
        names.each {
            path += it
            path += File.separatorChar
        }
        return path.substring(0, path.size() - 1)
    }
}
