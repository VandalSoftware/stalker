package com.vandalsoftware.example.single

import com.vandalsoftware.example.single.Car
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
