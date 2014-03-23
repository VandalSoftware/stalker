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
        StalkerPlugin plugin = new StalkerPlugin()
        assert plugin.isVersionNewer('0.9.1', '0.9.0')
        assert plugin.isVersionNewer('0.9.0', '0.9.0')
        assert plugin.isVersionNewer('0.9.0-SNAPSHOT', '0.9.0')
        assert !(plugin.isVersionNewer('0.8.0', '0.9.0'))
    }

    @Test
    public void stalkerExtensionDefaultsForJava() {
        def project = ProjectBuilder.builder().build()
        project.apply plugin: 'java'
        project.apply plugin: 'stalker'
        def StalkerConfiguration config = project.tasks.stalk.ext.configuration()
        def srcRoots = config.srcRoots
        assert 2 == srcRoots.size()
        assert srcRoots.contains(constructFile(project.projectDir.path, 'src', 'main', 'java'))
        assert srcRoots.contains(constructFile(project.projectDir.path, 'src', 'test', 'java'))
        def classpaths = config.srcClassPaths
        assert 2 == classpaths.size()
        assert classpaths.contains(constructFile(project.buildDir.path, 'classes', 'test'))
        assert classpaths.contains(constructFile(project.buildDir.path, 'classes', 'main'))
        def targets = config.targetClassPaths
        assert 1 == targets.size()
        assert classpaths.contains(constructFile(project.buildDir.path, 'classes', 'test'))
    }

    @Test
    public void stalkerExtensionDefaultsForGroovy() {
        def project = ProjectBuilder.builder().build()
        project.apply plugin: 'groovy'
        project.apply plugin: 'stalker'
        def StalkerConfiguration config = project.tasks.stalk.ext.configuration()
        def srcRoots = config.srcRoots
        assert 4 == srcRoots.size()
        assert srcRoots.contains(constructFile(project.projectDir.path, 'src', 'main', 'java'))
        assert srcRoots.contains(constructFile(project.projectDir.path, 'src', 'test', 'java'))
        assert srcRoots.contains(constructFile(project.projectDir.path, 'src', 'main', 'groovy'))
        assert srcRoots.contains(constructFile(project.projectDir.path, 'src', 'test', 'groovy'))
        def classpaths = config.srcClassPaths
        assert 2 == classpaths.size()
        assert classpaths.contains(constructFile(project.buildDir.path, 'classes', 'test'))
        assert classpaths.contains(constructFile(project.buildDir.path, 'classes', 'main'))
        def targets = config.targetClassPaths
        assert 1 == targets.size()
        assert classpaths.contains(constructFile(project.buildDir.path, 'classes', 'test'))
    }
}
