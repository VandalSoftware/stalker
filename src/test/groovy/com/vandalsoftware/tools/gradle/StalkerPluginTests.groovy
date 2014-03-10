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

import static org.junit.Assert.assertTrue

/**
 * @author Jonathan Le
 */
class StalkerPluginTests {
    @Test
    public void stalkerPluginAddsStalkTaskToProject() {
        Project project = ProjectBuilder.builder().build()
        project.apply plugin: 'stalker'
        assertTrue(project.tasks.stalk instanceof Usages)
    }

    @Test
    public void stalkerPluginAddsChangesTaskToProject() {
        Project project = ProjectBuilder.builder().build()
        project.apply plugin: 'stalker'
        assertTrue(project.tasks.changes instanceof GetChangedFiles)
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
        def project = ProjectBuilder.builder().build()
        testSetSrcRoot(project, ['src/main/java'])
        testSetSrcRoot(project, ['src/main/java', 'src/debug/java'])
    }

    private void testSetSrcRoot(project, srcDirs) {
        def stalkerExt = new StalkerExtension()
        def plugin = new StalkerPlugin()
        plugin.setSrcRoot(project, srcDirs, stalkerExt)
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
        def classPath = plugin.getSrcClassPath(project, null, project.android.buildTypes.debug)
        def expectedNames = [project.buildDir.name, 'classes',
                             project.android.buildTypes.debug.name]
        verifyClassPath(expectedNames, classPath)
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

    private void verifyClassPath(expectedNames, String classPath) {
        assert classPath != null

        expectedNames.reverseEach() {
            def lastIndexOf = classPath.lastIndexOf(it, classPath.size())
            assert lastIndexOf != -1
            def name = classPath.substring(lastIndexOf, classPath.size())
            assert it == name

            // Set up for next iteration, remove file separator
            if (lastIndexOf > 1) {
                classPath = classPath.substring(0, lastIndexOf - 1)
            }
        }
    }

    @Test
    public void getSrcClassPathWithProductFlavor() {
        def project = getAndroidProject(true)
        def plugin = new StalkerPlugin()
        def classPath = plugin.getSrcClassPath(project, project.android.productFlavors.flavor1,
                project.android.buildTypes.debug)
        def expectedNames = [project.buildDir.name, 'classes',
                             project.android.productFlavors.flavor1.name,
                             project.android.buildTypes.debug.name]
        verifyClassPath(expectedNames, classPath)
    }

    @Test
    public void getTargetClassPath() {
        def project = getAndroidProject(false)
        def plugin = new StalkerPlugin()
        def classPath = plugin.getTargetClassPath(project, null, project.android.buildTypes.debug)
        def expectedNames = [project.buildDir.name, 'classes', 'test',
                             project.android.buildTypes.debug.name]
        verifyClassPath(expectedNames, classPath)
    }

    @Test
    public void getTargetClassPathWithProductFlavor() {
        def project = getAndroidProject(true)
        def plugin = new StalkerPlugin()
        def classPath = plugin.getTargetClassPath(project, project.android.productFlavors.flavor1,
                project.android.buildTypes.debug)
        def expectedNames = [project.buildDir.name, 'classes', 'test',
                             project.android.productFlavors.flavor1.name,
                             project.android.buildTypes.debug.name]
        verifyClassPath(expectedNames, classPath)
    }

    @Test
    public void setClassPaths() {
        def project = getAndroidProject(false)
        def stalkerExt = new StalkerExtension()
        def plugin = new StalkerPlugin()
        plugin.setClassPaths(project, null, project.android.buildTypes, stalkerExt)
        assert 2 == stalkerExt.srcClassPaths.size()
        assert 2 == stalkerExt.targetClassPaths.size()
    }

    @Test
    public void setClassPathsWithProductFlavor() {
        def project = getAndroidProject(true)
        def stalkerExt = new StalkerExtension()
        def plugin = new StalkerPlugin()
        plugin.setClassPaths(project, project.android.productFlavors.flavor1,
                project.android.buildTypes, stalkerExt)
        assert 2 == stalkerExt.srcClassPaths.size()
        assert 2 == stalkerExt.targetClassPaths.size()
    }
}
