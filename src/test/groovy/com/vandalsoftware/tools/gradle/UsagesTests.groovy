package com.vandalsoftware.tools.gradle

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test
import static org.junit.Assert.*

/**
 * @author Jonathan Le
 */
class UsagesTests {
    @Test
    void testGroovyFilesCollected() {
        Project project = ProjectBuilder.builder().build()
        Usages usages = project.task([type: Usages], "usages", {
            ext.srcRoots = {
                [new File("src/main/groovy"), new File("src/test/groovy")] as Set
            }
            ext.classpaths = {
                [new File("build/classes/main"), new File("build/classes/test")] as Set
            }
            ext.targets = {
                [new File("build/classes/test")] as Set
            }
            ext.input = {
                ["src/main/groovy/com/vandalsoftware/tools/gradle/Usages.groovy",
                        "src/test/groovy/com/vandalsoftware/tools/gradle/UsagesTests.groovy"] as Set
            }
        }) as Usages
        usages.execute()
        assertNotNull("usages.classNames is not null", usages.classNames)
        assertFalse("usages.classNames is non-empty", usages.classNames.size() == 0)
        assertTrue('UsagesTests is an affected file',
                usages.classNames.contains("com.vandalsoftware.tools.gradle.UsagesTests"))
    }

    /**
     * Tests that there is an affected file when the input file is in both the source and the
     * target class paths.
     */
    @Test
    void testAffectedFileInSrcAndTargetClasspath() {
        Project project = ProjectBuilder.builder().build()
        Usages usages = project.task([type: Usages], "usages", {
            ext.srcRoots = {
                [new File("src/main/groovy"), new File("src/test/groovy")] as Set
            }
            ext.classpaths = {
                [new File("build/classes/main"), new File("build/classes/test")] as Set
            }
            ext.targets = {
                [new File("build/classes/test")] as Set
            }
            ext.input = {
                ["src/test/groovy/com/vandalsoftware/tests/model/Cat.groovy"] as Set
            }
        }) as Usages
        usages.execute()
        assertTrue('Cat is an affected file',
                usages.classNames.contains("com.vandalsoftware.tests.model.Cat"))
    }
}
