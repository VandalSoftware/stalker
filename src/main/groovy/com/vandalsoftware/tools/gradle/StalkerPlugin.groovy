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
        Task changesTask = project.task([type: GetChangedFiles], "changes", {
            def ref = extension.getRevision()
            args "-r"
            if (ref) {
                args ref
            } else {
                args "HEAD"
            }
            standardOutput = new ByteArrayOutputStream()
            ext.output = {
                String out = standardOutput.toString()
                out.split("\n") as Set
            }
        })
        Task stalkTask = project.task([type: Usages, dependsOn: changesTask], "stalk", {
            ext.srcRoots = {
                return extension.getSrcRoots()
            }
            ext.classpaths = {
                return extension.getSrcClassPaths()
            }
            ext.input = {
                return changesTask.output()
            }
            ext.targets = {
                return extension.getTargetClassPaths()
            }
            description = "Analyze class usage"
            group = "Analyze"
        }) << {
            if (extension.afterStalk) {
                extension.afterStalk(classNames)
            } else {
                if (extension.standardOutput != null) {
                    PrintStream out = new PrintStream(extension.standardOutput, true)
                    classNames.each() {
                        out.println(it)
                    }
                    out.close()
                } else {
                    classNames.each() {
                        println(it);
                    }
                }
            }
        }
        stalkTask.onlyIf {
            it.checkInputs()
        }
    }
}
