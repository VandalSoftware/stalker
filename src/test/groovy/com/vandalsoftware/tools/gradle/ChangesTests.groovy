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

import static org.junit.Assert.*

/**
 * @author Jonathan Le
 */
class ChangesTests {
    /**
     * Default working directory for this project.
     */
    public static final File WORKING_DIR = new File('.')

    @Test
    void returnsNonEmptyListForHEAD() {
        Project project = ProjectBuilder.builder().withProjectDir(WORKING_DIR).build()
        DetectChanges changes = project.task([type: DetectChanges], "changes", {
            ext.revision = {
                Constants.HEAD
            }
        }) as DetectChanges
        changes.execute()
        assertNotEquals 'changed files is not empty', 0, changes.changedFiles.size()
    }

    @Test
    void returnsSourceFilesForValidCommitId() {
        Project project = ProjectBuilder.builder().withProjectDir(WORKING_DIR).build()
        DetectChanges changes = project.task([type: DetectChanges], "changes", {
            ext.revision = {
                '3ffc420595c4c81d951f1dd56038c55ae49edf9f'
            }
        }) as DetectChanges
        changes.execute()
        def expectedFiles = [
                new File("src/test/groovy/com/vandalsoftware/tests/model/Cat.groovy"),
                new File("src/test/groovy/com/vandalsoftware/tools/gradle/UsagesTests.groovy"),
                new File("src/main/java/com/vandalsoftware/tools/classfile/ClassCollector.java")
        ]
        assertTrue 'contains files', changes.changedFiles.containsAll(expectedFiles)
    }

    @Test
    void returnsSourceFilesForCommitIdRange() {
        Project project = ProjectBuilder.builder().withProjectDir(WORKING_DIR).build()
        DetectChanges changes = project.task([type: DetectChanges], "changes", {
            ext.fromRevision = {
                'd8b7a7a096dd419e07f561a198f1445d4d263dd6'
            }
            ext.revision = {
                '3ffc420595c4c81d951f1dd56038c55ae49edf9f'
            }
        }) as DetectChanges
        changes.execute()
        def expectedFiles = [
                new File("src/test/groovy/com/vandalsoftware/tests/model/Cat.groovy"),
                new File("src/main/groovy/com/vandalsoftware/tools/gradle/Usages.groovy"),
                new File("src/test/groovy/com/vandalsoftware/tools/gradle/UsagesTests.groovy"),
                new File("src/main/java/com/vandalsoftware/tools/classfile/ClassCollector.java")
        ]
        assertTrue 'contains files', changes.changedFiles.containsAll(expectedFiles)
    }

    @Test
    void returnsEmptyListForSameToAndFromRevision() {
        Project project = ProjectBuilder.builder().withProjectDir(WORKING_DIR).build()
        DetectChanges changes = project.task([type: DetectChanges], "changes", {
            ext.fromRevision = {
                Constants.HEAD
            }
            ext.revision = {
                Constants.HEAD
            }
        }) as DetectChanges
        changes.execute()
        assertEquals 'empty', 0, changes.changedFiles.size()
    }

    @Test
    void returnsNonEmptyListForUnassignedExtensionProperties() {
        Project project = ProjectBuilder.builder().withProjectDir(WORKING_DIR).build()
        DetectChanges changes = project.task([type: DetectChanges], "changes") as DetectChanges
        changes.execute()
        assertNotEquals 'empty', 0, changes.changedFiles.size()
    }

    @Test(expected = Exception)
    void throwsExceptionForInvalidGitRepo() {
        Project project = ProjectBuilder.builder().build()
        DetectChanges changes = project.task([type: DetectChanges], "changes") as DetectChanges
        changes.execute()
    }

    @Test(expected = Exception)
    void throwsExceptionForInvalidRevStr() {
        Project project = ProjectBuilder.builder().withProjectDir(WORKING_DIR).build()
        String invalidRevision = "vandal was here";
        DetectChanges changes = project.task([type: DetectChanges], "changes", {
            ext.revision = {
                invalidRevision
            }
        }) as DetectChanges
        changes.execute()
    }
}
