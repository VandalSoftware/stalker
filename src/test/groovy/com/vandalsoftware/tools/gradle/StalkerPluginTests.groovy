package com.vandalsoftware.tools.gradle

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test

import static org.junit.Assert.assertTrue

/**
 * @author Jonathan Le
 */
class StalkerPluginTests {
    @Test
    public void stalkerPluginAddsStalkTaskToProject() {
        Project project = ProjectBuilder.builder().build()
        project.apply plugin: 'stalker'
        assertTrue(project.tasks.stalk instanceof Usages)
    }

    @Test
    public void stalkerPluginAddsChangesTaskToProject() {
        Project project = ProjectBuilder.builder().build()
        project.apply plugin: 'stalker'
        assertTrue(project.tasks.changes instanceof GetChangedFiles)
    }
}
