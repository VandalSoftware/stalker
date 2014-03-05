/*
 * Copyright (C) 2013 Vandal LLC
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
