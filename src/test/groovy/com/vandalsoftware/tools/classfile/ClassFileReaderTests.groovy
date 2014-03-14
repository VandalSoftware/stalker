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

    @Test
    public void classInfoReadsAllInterfaces() {
        File file = new File("$TEST_CLASSPATH/com/vandalsoftware/tests/model/Dog.class")
        assertTrue(file.exists())
        ClassFileReader reader = new ClassFileReader()
        ClassInfo classInfo = reader.readFile(file)
        assertNotNull(classInfo)
        def interfaces = classInfo.getInterfaces()
        assertEquals(2, interfaces.size())
        assertTrue(interfaces.contains("com.vandalsoftware.tests.model.Animal"))
        assertTrue(interfaces.contains("com.vandalsoftware.tests.model.FourLegged"))
    }
}