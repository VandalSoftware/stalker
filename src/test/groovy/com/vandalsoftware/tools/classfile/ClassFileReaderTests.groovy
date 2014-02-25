package com.vandalsoftware.tools.classfile

import org.junit.Test

import static org.junit.Assert.*

/**
 * @author Jonathan Le
 */
class ClassFileReaderTests {
    private static final String TEST_CLASSPATH = "build/classes/test"

    @Test
    public void readInterface() {
        File file = new File("$TEST_CLASSPATH/com/vandalsoftware/tests/model/Animal.class")
        assertTrue file.exists()
        ClassFileReader reader = new ClassFileReader()
        ClassInfo classInfo = reader.readFile(file)
        assertNotNull(classInfo)
        assertEquals("com.vandalsoftware.tests.model.Animal", classInfo.thisClassName)
        assertTrue(classInfo.isInterface())
        assertFalse(classInfo.isSubclass())
    }
}