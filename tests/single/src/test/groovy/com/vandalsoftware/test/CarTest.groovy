package com.vandalsoftware.test

import com.vandalsoftware.test.Car
import org.junit.Test

import static org.junit.Assert.assertTrue

/**
 * @author Jonathan Le
 */
class CarTest {
    @Test
    public void testCarStart() {
        Car car = new Car()
        car.start()
        assertTrue car.isRunning()
    }
}
