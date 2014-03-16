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

import org.eclipse.jgit.lib.Constants
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test

import static org.junit.Assert.*;

/**
 * @author Jonathan Le
 */
class GetChangedFilesTests {
    /**
     * Default working directory for this project.
     */
    public static final File WORKING_DIR = new File('.')

    @Test
    void testGetChangedFileListNotEmpty() {
        Project project = ProjectBuilder.builder().withProjectDir(WORKING_DIR).build()
        GetChangedFiles changes = project.task([type: GetChangedFiles], "changes", {
            ext.revision = {
                Constants.HEAD
            }
        }) as GetChangedFiles
        changes.execute()
        assertNotEquals 'changed files is not empty', 0, changes.files.size()
    }

    @Test
    void testGetChangedSourceFiles() {
        Project project = ProjectBuilder.builder().withProjectDir(WORKING_DIR).build()
        GetChangedFiles changes = project.task([type: GetChangedFiles], "changes", {
            ext.revision = {
                '3ffc420595c4c81d951f1dd56038c55ae49edf9f'
            }
        }) as GetChangedFiles
        changes.execute()
        def expectedFiles = [
                new File("src/test/groovy/com/vandalsoftware/tests/model/Cat.groovy"),
                new File("src/test/groovy/com/vandalsoftware/tools/gradle/UsagesTests.groovy"),
                new File("src/main/java/com/vandalsoftware/tools/classfile/ClassCollector.java")
        ]
        assertTrue 'contains files', changes.files.containsAll(expectedFiles)
    }

    @Test
    void testGetChangesInRange() {
        Project project = ProjectBuilder.builder().withProjectDir(WORKING_DIR).build()
        GetChangedFiles changes = project.task([type: GetChangedFiles], "changes", {
            ext.fromRevision = {
                'd8b7a7a096dd419e07f561a198f1445d4d263dd6'
            }
            ext.revision = {
                '3ffc420595c4c81d951f1dd56038c55ae49edf9f'
            }
        }) as GetChangedFiles
        changes.execute()
        def expectedFiles = [
                new File("src/test/groovy/com/vandalsoftware/tests/model/Cat.groovy"),
                new File("src/main/groovy/com/vandalsoftware/tools/gradle/Usages.groovy"),
                new File("src/test/groovy/com/vandalsoftware/tools/gradle/UsagesTests.groovy"),
                new File("src/main/java/com/vandalsoftware/tools/classfile/ClassCollector.java")
        ]
        assertTrue 'contains files', changes.files.containsAll(expectedFiles)
    }

    @Test
    void testSameFromAndToRevision() {
        Project project = ProjectBuilder.builder().withProjectDir(WORKING_DIR).build()
        GetChangedFiles changes = project.task([type: GetChangedFiles], "changes", {
            ext.fromRevision = {
                Constants.HEAD
            }
            ext.revision = {
                Constants.HEAD
            }
        }) as GetChangedFiles
        changes.execute()
        assertEquals 'empty', 0, changes.files.size()
    }

    @Test
    void testNoExtensionPropertiesAssigned() {
        Project project = ProjectBuilder.builder().withProjectDir(WORKING_DIR).build()
        GetChangedFiles changes =
                project.task([type: GetChangedFiles], "changes") as GetChangedFiles
        changes.execute()
        assertNotEquals 'empty', 0, changes.files.size()
    }
}
