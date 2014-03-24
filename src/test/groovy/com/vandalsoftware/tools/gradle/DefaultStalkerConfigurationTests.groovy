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
 * @author Lien Tran Mamitsuka
 * @author Jonathan Le
 */
class DefaultStalkerConfigurationTests {
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
    public void getAndroidSrcClassPath() {
        def project = getAndroidProject(false)
        def config = new DefaultStalkerConfiguration()
        def classPath = config.getAndroidSrcClassPath(project, null, project.android.buildTypes.debug)
        def expectedClassPath = constructFile(project.buildDir.path, 'classes',
                project.android.buildTypes.debug.name)
        assert expectedClassPath == classPath
    }

    @Test
    public void getSrcClassPathWithProductFlavor() {
        def project = getAndroidProject(true)
        def config = new DefaultStalkerConfiguration()
        def classPath = config.getAndroidSrcClassPath(project, project.android.productFlavors.flavor1,
                project.android.buildTypes.debug)
        def expectedClassPath = constructFile(project.buildDir.path, 'classes',
                project.android.productFlavors.flavor1.name,
                project.android.buildTypes.debug.name)
        assert expectedClassPath == classPath
    }

    @Test
    public void getAndroidTestClassPath() {
        def project = getAndroidProject(false)
        def config = new DefaultStalkerConfiguration()
        def classPath = config.getAndroidTestClassPath(project, null, project.android.buildTypes.debug)
        def expectedClassPath = constructFile(project.buildDir.path, 'classes', 'test',
                project.android.buildTypes.debug.name)
        assert expectedClassPath == classPath
    }

    @Test
    public void getTargetClassPathWithProductFlavor() {
        def project = getAndroidProject(true)
        def config = new DefaultStalkerConfiguration()
        def classPath = config.getAndroidTestClassPath(project, project.android.productFlavors.flavor1,
                project.android.buildTypes.debug)
        def expectedClassPath = constructFile(project.buildDir.path, 'classes', 'test',
                project.android.productFlavors.flavor1.name,
                project.android.buildTypes.debug.name)
        assert expectedClassPath == classPath
    }

    @Test
    public void addAndroidClassPaths() {
        def project = getAndroidProject(false)
        def config = new DefaultStalkerConfiguration()
        config.addAndroidClassPaths(project, null, project.android.buildTypes)
        assert 4 == config.srcClassPaths.size()
        assert 2 == config.targetClassPaths.size()
    }

    @Test
    public void addClassPathsWithProductFlavor() {
        def project = getAndroidProject(true)
        def config = new DefaultStalkerConfiguration()
        config.addAndroidClassPaths(project, project.android.productFlavors.flavor1,
                project.android.buildTypes)
        assert 4 == config.srcClassPaths.size()
        assert 2 == config.targetClassPaths.size()
    }
}
