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

/**
 * @author Jonathan Le
 */
class DefaultStalkerConfiguration extends StalkerConfiguration {
    File getAndroidSrcClassPath(project, productFlavor, buildType) {
        if (productFlavor != null) {
            return FileUtils.constructFile(project.buildDir.path, 'classes', productFlavor.name,
                    buildType.name)
        } else {
            return FileUtils.constructFile(project.buildDir.path, 'classes', buildType.name)
        }
    }

    File getAndroidTestClassPath(project, productFlavor, buildType) {
        if (productFlavor != null) {
            return FileUtils.constructFile(project.buildDir.path, 'classes', 'test',
                    productFlavor.name, buildType.name)
        } else {
            return FileUtils.constructFile(project.buildDir.path, 'classes', 'test', buildType.name)
        }
    }

    def addAndroidClassPaths(project, productFlavor, buildTypes) {
        for (bt in buildTypes) {
            def srcClassPath = getAndroidSrcClassPath(project, productFlavor, bt)
            addSrcClassPath(srcClassPath)

            def targetClassPath = getAndroidTestClassPath(project, productFlavor, bt)
            addSrcClassPath(targetClassPath)
            addTargetClassPath(targetClassPath)
        }
    }
}
