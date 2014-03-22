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

import com.vandalsoftware.tools.util.FileUtils
import org.eclipse.jgit.lib.Constants
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.SourceSet

/**
 * @author Jonathan Le
 */
class StalkerPlugin implements Plugin<Project> {
    StalkerExtension extension

    @Override
    void apply(Project project) {
        extension = project.extensions.create('stalker', StalkerExtension)
        def stalkerExtensionDefaults = new StalkerExtension()
        project.configure(project) { Project configProject ->
            if (configProject.extensions.findByName('android') &&
                    canSetStalkerExtensionDefaults(configProject)) {
                configProject.android.sourceSets.each() { sourceSet ->
                    addSrcRoots(sourceSet.java.getSrcDirs(), stalkerExtensionDefaults)
                }

                gradle.taskGraph.whenReady { taskGraph ->
                    if (configProject.android.productFlavors.size() > 0) {
                        for (pf in configProject.android.productFlavors) {
                            println "flavor: ${pf.name}"
                            addAndroidClassPaths(configProject, pf, configProject.android.buildTypes, stalkerExtensionDefaults)
                        }
                    } else {
                        addAndroidClassPaths(configProject, null, configProject.android.buildTypes, stalkerExtensionDefaults)
                    }
                }
            }
            if (configProject.plugins.hasPlugin('java') || configProject.plugins.hasPlugin('groovy')) {
                configProject.sourceSets.each() { SourceSet sourceSet ->
                    addSrcRoots(sourceSet.allJava.srcDirs, stalkerExtensionDefaults)
                    addSrcClassPath(sourceSet.output.classesDir, stalkerExtensionDefaults)
                }
                addSrcClassPath(configProject.sourceSets.test.output.classesDir,
                        stalkerExtensionDefaults)
                addTargetClassPath(configProject.sourceSets.test.output.classesDir,
                        stalkerExtensionDefaults)
            }
        }
        DetectChanges changesTask = project.task([type: DetectChanges], 'changes', {
            ext.revision = {
                def ref = extension.getRevision()
                if (ref) {
                    ref
                } else {
                    Constants.HEAD
                }
            }
        }) as DetectChanges
        Task stalkTask = project.task([type: Inspect, dependsOn: changesTask], 'stalk', {
            ext.srcRoots = {
                def srcRoots
                if (extension.getSrcRoots().size() == 0) {
                    srcRoots = stalkerExtensionDefaults.getSrcRoots()
                } else {
                    srcRoots = extension.getSrcRoots()
                }
                logDirs(project, srcRoots, 'srcRoots')
                return srcRoots
            }
            ext.classpaths = {
                def srcClassPaths
                if (extension.getSrcClassPaths().size() == 0) {
                    srcClassPaths = stalkerExtensionDefaults.getSrcClassPaths()
                } else {
                    srcClassPaths = extension.getSrcClassPaths()
                }
                logDirs(project, srcClassPaths, 'srcClassPaths')
                return srcClassPaths
            }
            ext.input = {
                return changesTask.getChangedFiles()
            }
            ext.targets = {
                def targetClassPaths
                if (extension.getTargetClassPaths().size() == 0) {
                    targetClassPaths = stalkerExtensionDefaults.getTargetClassPaths()
                } else {
                    targetClassPaths = extension.getTargetClassPaths()
                }
                logDirs(project, targetClassPaths, 'targetClassPaths')
                return targetClassPaths
            }
            description = 'Analyze class usage'
            group = 'Analyze'
        }) << {
            if (extension.standardOutput != null) {
                PrintStream out = new PrintStream(extension.standardOutput, true)
                affectedClasses.each() {
                    out.println(it)
                }
                out.close()
            } else {
                if (affectedClasses.size() > 0) {
                    project.logger.lifecycle 'Affected classes:'
                    affectedClasses.each() { className ->
                        project.logger.lifecycle "  $className"
                    }
                } else {
                    project.logger.lifecycle 'No affected classes.'
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
    private static def canSetStalkerExtensionDefaults(project) {
        String androidPluginVersion = ''
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
    static def isVersionNewer(String version1, String version2) {
        def version1Array = version1.split('\\.')
        def version2Array = version2.split('\\.')
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

    static def addSrcRoots(srcDirs, stalkerExt) {
        for (d in srcDirs) {
            stalkerExt.srcRoot d
        }
    }

    static def addAndroidClassPaths(project, productFlavor, buildTypes, stalkerExt) {
        for (bt in buildTypes) {
            def srcClassPath = getAndroidSrcClassPath(project, productFlavor, bt)
            addSrcClassPath(srcClassPath, stalkerExt)

            def targetClassPath = getAndroidTestClassPath(project, productFlavor, bt)
            addSrcClassPath(targetClassPath, stalkerExt)
            addTargetClassPath(targetClassPath, stalkerExt)
        }
    }

    static def addSrcClassPath(srcClassPath, stalkerExt) {
        stalkerExt.srcClassPath srcClassPath
    }

    static def addTargetClassPath(targetClassPath, stalkerExt) {
        stalkerExt.targetClassPath targetClassPath
    }

    static File getAndroidSrcClassPath(project, productFlavor, buildType) {
        if (productFlavor != null) {
            return FileUtils.constructFile(project.buildDir.path, 'classes', productFlavor.name,
                    buildType.name)
        } else {
            return FileUtils.constructFile(project.buildDir.path, 'classes', buildType.name)
        }
    }

    static File getAndroidTestClassPath(project, productFlavor, buildType) {
        if (productFlavor != null) {
            return FileUtils.constructFile(project.buildDir.path, 'classes', 'test',
                    productFlavor.name, buildType.name)
        } else {
            return FileUtils.constructFile(project.buildDir.path, 'classes', 'test', buildType.name)
        }
    }

    static def logDirs(project, dirs, description) {
        project.logger.info("Using ${description}:")
        dirs.each() {
            project.logger.info("\t${it.path}" - project.projectDir.path)
        }
    }
}
