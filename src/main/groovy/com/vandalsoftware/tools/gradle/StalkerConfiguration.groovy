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
class StalkerConfiguration {
    final Set<File> srcRoots
    final Set<File> srcClassPaths
    final Set<File> targetClassPaths

    StalkerConfiguration() {
        srcRoots = [] as HashSet
        targetClassPaths = [] as HashSet
        srcClassPaths = [] as HashSet
    }

    void addSrcRoot(File dir) {
        srcRoots.add(dir)
    }

    void addSrcRoots(Collection dirs) {
        srcRoots.addAll(dirs)
    }

    void addSrcClassPath(File dir) {
        srcClassPaths.add(dir)
    }

    void addSrcClassPaths(Collection dir) {
        srcClassPaths.addAll(dir)
    }

    void addTargetClassPath(File dir) {
        targetClassPaths.add(dir)
    }

    void addTargetClassPaths(Collection dir) {
        targetClassPaths.addAll(dir)
    }
}
