package com.vandalsoftware.tools.classfile

import org.junit.Test

import static org.junit.Assert.assertTrue

/**
 * @author Jonathan Le
 */
class ClassCollectorTests {
    public static final String TEST_CLASSPATH = "build/classes/test"
    public static final String[] TEST_FILES = [
        "$TEST_CLASSPATH/com/vandalsoftware/tests/model/Animal.class",
        "$TEST_CLASSPATH/com/vandalsoftware/tools/classfile/ClassCollectorTests.class"
    ]

    @Test
    void listClassFiles() {
        Collection<File> files = ClassCollector.listFiles(new File(TEST_CLASSPATH))
        TEST_FILES.each { String path ->
            assertTrue(files.contains(new File(path)))
        }
    }
}
