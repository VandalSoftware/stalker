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

/**
 * @author Jonathan Le
 */
class StalkerExtension {
    Set<File> srcRoots
    Set<File> targetClassPaths
    Set<File> srcClassPaths
    String revision
    OutputStream standardOutput

    StalkerExtension() {
        srcRoots = new LinkedHashSet<>()
        targetClassPaths = new LinkedHashSet<>()
        srcClassPaths = new LinkedHashSet<>()
    }

    void srcRoot(String path) {
        srcRoots.add(new File(path))
    }

    void srcRoot(File dir) {
        srcRoots.add(dir)
    }

    void targetClassPath(String path) {
        targetClassPaths.add(new File(path))
    }

    void targetClassPath(File dir) {
        targetClassPaths.add(dir)
    }

    void srcClassPath(String path) {
        srcClassPaths.add(new File(path))
    }

    void srcClassPath(File dir) {
        srcClassPaths.add(dir)
    }
}
