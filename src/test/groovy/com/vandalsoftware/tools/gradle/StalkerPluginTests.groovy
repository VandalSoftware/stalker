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

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test

import static com.vandalsoftware.tools.util.FileUtils.constructFile

/**
 * @author Jonathan Le
 */
class StalkerPluginTests {
    @Test
    public void stalkerPluginAddsStalkTaskToProject() {
        Project project = ProjectBuilder.builder().build()
        project.apply plugin: 'stalker'
        assert project.tasks.stalk instanceof Inspect
    }

    @Test
    public void stalkerPluginAddsChangesTaskToProject() {
        Project project = ProjectBuilder.builder().build()
        project.apply plugin: 'stalker'
        assert project.tasks.changes instanceof DetectChanges
    }

    @Test
    public void isVersionNewer() {
        def plugin = new StalkerPlugin()
        assert plugin.isVersionNewer('0.9.1', '0.9.0')
        assert plugin.isVersionNewer('0.9.0', '0.9.0')
        assert plugin.isVersionNewer('0.9.0-SNAPSHOT', '0.9.0')
        assert !(plugin.isVersionNewer('0.8.0', '0.9.0'))
    }

    @Test
    public void setSrcRoot() {
        testSetSrcRoot(['src/main/java'])
        testSetSrcRoot(['src/main/java', 'src/debug/java'])
    }

    private static void testSetSrcRoot(List srcDirs) {
        def stalkerExt = new StalkerExtension()
        def plugin = new StalkerPlugin()
        plugin.addSrcRoots(srcDirs, stalkerExt)
        assert srcDirs.size() == stalkerExt.srcRoots.size()
        def i = 0
        for (f in stalkerExt.srcRoots) {
            assert srcDirs[i] == f.path
            i += 1
        }
    }

    @Test
    public void getSrcClassPath() {
        def project = getAndroidProject(false)
        def plugin = new StalkerPlugin()
        def classPath = plugin.getAndroidSrcClassPath(project, null, project.android.buildTypes.debug)
        def expectedClassPath = constructFile(project.buildDir.path, 'classes',
                             project.android.buildTypes.debug.name)
        assert expectedClassPath == classPath
    }

    private Project getAndroidProject(boolean withProductFlavor) {
        def project = ProjectBuilder.builder().build()
        project.apply plugin: 'android'
        if (withProductFlavor) {
            project.android {
                productFlavors {
                    flavor1 {
                        packageName "com.example.flavor1"
                    }
                }
            }
        }
        return project
    }

    @Test
    public void getSrcClassPathWithProductFlavor() {
        def project = getAndroidProject(true)
        def plugin = new StalkerPlugin()
        def classPath = plugin.getAndroidSrcClassPath(project, project.android.productFlavors.flavor1,
                project.android.buildTypes.debug)
        def expectedClassPath = constructFile(project.buildDir.path, 'classes',
                             project.android.productFlavors.flavor1.name,
                             project.android.buildTypes.debug.name)
        assert expectedClassPath == classPath
    }

    @Test
    public void getAndroidTestClassPath() {
        def project = getAndroidProject(false)
        def plugin = new StalkerPlugin()
        def classPath = plugin.getAndroidTestClassPath(project, null, project.android.buildTypes.debug)
        def expectedClassPath = constructFile(project.buildDir.path, 'classes', 'test',
                             project.android.buildTypes.debug.name)
        assert expectedClassPath == classPath
    }

    @Test
    public void getTargetClassPathWithProductFlavor() {
        def project = getAndroidProject(true)
        def plugin = new StalkerPlugin()
        def classPath = plugin.getAndroidTestClassPath(project, project.android.productFlavors.flavor1,
                project.android.buildTypes.debug)
        def expectedClassPath = constructFile(project.buildDir.path, 'classes', 'test',
                             project.android.productFlavors.flavor1.name,
                             project.android.buildTypes.debug.name)
        assert expectedClassPath == classPath
    }

    @Test
    public void setClassPaths() {
        def project = getAndroidProject(false)
        def stalkerExt = new StalkerExtension()
        def plugin = new StalkerPlugin()
        plugin.addAndroidClassPaths(project, null, project.android.buildTypes, stalkerExt)
        assert 4 == stalkerExt.srcClassPaths.size()
        assert 2 == stalkerExt.targetClassPaths.size()
    }

    @Test
    public void setClassPathsWithProductFlavor() {
        def project = getAndroidProject(true)
        def stalkerExt = new StalkerExtension()
        def plugin = new StalkerPlugin()
        plugin.addAndroidClassPaths(project, project.android.productFlavors.flavor1,
                project.android.buildTypes, stalkerExt)
        assert 4 == stalkerExt.srcClassPaths.size()
        assert 2 == stalkerExt.targetClassPaths.size()
    }

    @Test
    public void stalkerExtensionDefaultsForJava() {
        def project = ProjectBuilder.builder().build()
        project.apply plugin: 'java'
        project.apply plugin: 'stalker'
        def srcRoots = project.tasks.stalk.ext.srcRoots.call()
        assert 2 == srcRoots.size()
        assert srcRoots.contains(constructFile(project.projectDir.path, 'src', 'main', 'java'))
        assert srcRoots.contains(constructFile(project.projectDir.path, 'src', 'test', 'java'))
        def classpaths = project.tasks.stalk.ext.classpaths.call()
        assert 2 == classpaths.size()
        assert classpaths.contains(constructFile(project.buildDir.path, 'classes', 'test'))
        assert classpaths.contains(constructFile(project.buildDir.path, 'classes', 'main'))
        def targets = project.tasks.stalk.ext.targets.call()
        assert 1 == targets.size()
        assert classpaths.contains(constructFile(project.buildDir.path, 'classes', 'test'))
    }

    @Test
    public void stalkerExtensionDefaultsForGroovy() {
        def project = ProjectBuilder.builder().build()
        project.apply plugin: 'groovy'
        project.apply plugin: 'stalker'
        def srcRoots = project.tasks.stalk.ext.srcRoots.call()
        assert 4 == srcRoots.size()
        assert srcRoots.contains(constructFile(project.projectDir.path, 'src', 'main', 'java'))
        assert srcRoots.contains(constructFile(project.projectDir.path, 'src', 'test', 'java'))
        assert srcRoots.contains(constructFile(project.projectDir.path, 'src', 'main', 'groovy'))
        assert srcRoots.contains(constructFile(project.projectDir.path, 'src', 'test', 'groovy'))
        def classpaths = project.tasks.stalk.ext.classpaths.call()
        assert 2 == classpaths.size()
        assert classpaths.contains(constructFile(project.buildDir.path, 'classes', 'test'))
        assert classpaths.contains(constructFile(project.buildDir.path, 'classes', 'main'))
        def targets = project.tasks.stalk.ext.targets.call()
        assert 1 == targets.size()
        assert classpaths.contains(constructFile(project.buildDir.path, 'classes', 'test'))
    }
}
