package com.vandalsoftware.tools.gradle

import org.gradle.api.tasks.Exec

/**
 * @author Jonathan Le
 */
class GitLsTree extends Exec {
    GitLsTree() {
        super()
        commandLine("git")
        args("ls-tree")
        args("--name-only")
        args("-r")
        args("HEAD")
    }
}
