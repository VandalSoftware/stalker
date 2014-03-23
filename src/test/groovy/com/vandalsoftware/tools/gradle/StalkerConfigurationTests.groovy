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

import org.junit.Test

/**
 * @author Jonathan Le
 */
class StalkerConfigurationTests {
    @Test
    public void addSrcRoots() {
        def config = new StalkerConfiguration()
        config.addSrcRoots([new File('src/main/java'), new File('src/debug/java')])
        assert config.srcRoots.contains(new File('src/main/java'))
        assert config.srcRoots.contains(new File('src/debug/java'))
    }

    @Test
    public void addSrcRoot() {
        def config = new StalkerConfiguration()
        config.addSrcRoot(new File('src/main/java'))
        assert config.srcRoots.contains(new File('src/main/java'))
    }
}
