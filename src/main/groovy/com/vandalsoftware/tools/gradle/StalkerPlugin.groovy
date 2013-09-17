package com.vandalsoftware.tools.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task

/**
 * @author Jonathan Le
 */
class StalkerPlugin implements Plugin<Project> {
    StalkerExtension extension

    @Override
    void apply(Project project) {
        extension = project.extensions.create("stalker", StalkerExtension)
        project.afterEvaluate {
        Task changesTask = project.task([type: GitLsTree], "changes", {
            workingDir extension.getSrcRoot()
            standardOutput = new ByteArrayOutputStream()
            ext.output = {
                return standardOutput.toString().replace('/', '.').replace(".java", "")
            }
        })
        Task usagesTask = project.task([type: Usages, dependsOn: changesTask], "usages", {
            ext.input = {
                return changesTask.output()
            }
            classPath = extension.getClassPath()
        }) << {
            for (File f : files) {
                println(f);
            }
        }
        }
    }
}
