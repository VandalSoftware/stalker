package com.vandalsoftware.tools.gradle

import org.gradle.api.tasks.Exec

/**
 * @author Jonathan Le
 */
class GetChangedFiles extends Exec {
    GetChangedFiles() {
        super()
        commandLine("git")
        args("diff-tree")
        args("--no-commit-id")
        args("--name-only")
    }
}
