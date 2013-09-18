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
            Task changesTask = project.task([type: GetChangedFiles], "changes", {
                def ref = extension.getRefId()
                args "-r"
                if (ref) {
                    args ref
                } else {
                    args "HEAD"
                }
                standardOutput = new ByteArrayOutputStream()
                ext.output = {
                    return standardOutput.toString()
                }
            })
            Task usagesTask = project.task([type: Usages, dependsOn: changesTask], "usages", {
                ext.srcRoots = {
                    return extension.getSrcRoots()
                }
                ext.input = {
                    return changesTask.output()
                }
                ext.targets = {
                    return extension.getTargetClassPaths()
                }
            }) << {
                classNames.each() {
                    println(it);
                }
            }
            // Only execute if at least one class path exists
            usagesTask.onlyIf {
                boolean run = false
                it.targets().each() { File dir ->
                    if (dir.exists()) {
                        run = true
                    }
                }
                return run
            }
        }
    }
}
