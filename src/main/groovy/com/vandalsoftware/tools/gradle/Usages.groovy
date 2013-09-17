package com.vandalsoftware.tools.gradle

import com.vandalsoftware.tools.ClassFileReader
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * @author Jonathan Le
 */
class Usages extends DefaultTask {
    String classPath

    File[] files

    @TaskAction
    void usages() {
        def classes = input().split("\n")
        final ClassFileReader reader = new ClassFileReader();
        reader.read(classPath);
        // Check each file for usage of each input
        def classNames = classes as Set
        files = reader.usages(classNames);
    }
}
