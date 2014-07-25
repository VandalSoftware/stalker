package com.vandalsoftware.tools.gradle

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test
import static org.junit.Assert.*

/**
 * @author Jonathan Le
 */
class InspectTests {
    @Test
    void groovyFilesCollected() {
        Project project = ProjectBuilder.builder().build()
        Inspect usages = project.task([type: Inspect], "usages", {
            ext.configuration = {
                def config = new StalkerConfiguration()
                config.addSrcRoots([new File("src/main/groovy"), new File("src/test/groovy")])
                config.addSrcClassPaths([new File("build/classes/main"), new File("build/classes/test")])
                config.addTargetClassPaths([new File("build/classes/test")])
                config
            }
            ext.input = {
                [new File("src/main/groovy/com/vandalsoftware/tools/gradle/Inspect.groovy"),
                        new File("src/test/groovy/com/vandalsoftware/tools/gradle/InspectTests.groovy")] as Set
            }
        }) as Inspect
        usages.execute()
        assertNotNull("usages.affectedClasses is not null", usages.affectedClasses)
        assertFalse("usages.affectedClasses is non-empty", usages.affectedClasses.size() == 0)
        assertTrue('InspectTests is an affected file',
                usages.affectedClasses.contains("com.vandalsoftware.tools.gradle.InspectTests"))
    }

    /**
     * Tests that there is an affected file when the input file is in both the source and the
     * target class paths.
     */
    @Test
    void affectedFileInSrcAndTargetClasspath() {
        Project project = ProjectBuilder.builder().build()
        Inspect usages = project.task([type: Inspect], "usages", {
            ext.configuration = {
                def config = new StalkerConfiguration()
                config.addSrcRoots([new File("src/main/groovy"), new File("src/test/groovy")])
                config.addSrcClassPaths([new File("build/classes/main"), new File("build/classes/test")])
                config.addTargetClassPaths([new File("build/classes/test")])
                config
            }
            ext.input = {
                [new File("src/test/groovy/com/vandalsoftware/tests/model/Cat.groovy")] as Set
            }
        }) as Inspect
        usages.execute()
        assertTrue('Cat is an affected file',
                usages.affectedClasses.contains("com.vandalsoftware.tests.model.Cat"))
    }

    /**
     * Tests that there is an affected file when the input file is in both the source and the
     * target class paths.
     */
    @Test
    void skipsFilesWithNoExtension() {
        Project project = ProjectBuilder.builder().build()
        Inspect usages = project.task([type: Inspect], "usages", {
            ext.configuration = {
                def config = new StalkerConfiguration()
                config.addSrcRoots([new File("src/main/groovy"), new File("src/test/groovy")])
                config.addSrcClassPaths([new File("build/classes/main"), new File("build/classes/test")])
                config.addTargetClassPaths([new File("build/classes/test")])
                config
            }
            ext.input = {
                [new File("LICENSE")] as Set
            }
        }) as Inspect
        usages.execute()
        assert usages.affectedClasses.isEmpty()
    }
}
