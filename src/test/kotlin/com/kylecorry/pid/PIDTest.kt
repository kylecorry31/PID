package com.kylecorry.pid

import org.junit.Assert.*
import org.junit.Test

class PIDTest {

    @Test
    fun assignsPID(){
        val pid = PID(1.0, 2.0, 3.0)

        assertEquals(1.0, pid.P, 0.001)
        assertEquals(2.0, pid.I, 0.001)
        assertEquals(3.0, pid.D, 0.001)
    }

    @Test
    fun calculatesP(){
        val pid = PID(0.1, 0.0, 0.0)

        val calculated = pid.calculate(0.0, 10.0)
        assertEquals(1.0, calculated, 0.001)
        val calculated2 = pid.calculate(1.0, 10.0)
        assertEquals(0.9, calculated2, 0.001)
    }

    @Test
    fun getsPositionError(){
        val pid = PID(0.1, 0.0, 0.0)

        pid.calculate(0.0, 10.0)
        assertEquals(10.0, pid.positionError, 0.001)
        pid.calculate(1.0, 10.0)
        assertEquals(9.0, pid.positionError, 0.001)
    }

    @Test
    fun getsVelocityError(){
        val pid = PID(0.1, 0.0, 0.0)
        pid.period = 0.1

        pid.calculate(0.0, 10.0)
        pid.calculate(1.0, 10.0)
        assertEquals(-10.0, pid.velocityError, 0.001)
    }

    @Test
    fun calculatesD(){
        val pid = PID(0.0, 0.0, 0.1)
        pid.period = 0.1

        val initialOutput = pid.calculate(0.0, 10.0)
        assertEquals(0.0, initialOutput, 0.001)
        val output = pid.calculate(1.0, 10.0)
        assertEquals(-1.0, output, 0.001)
    }

    @Test
    fun calculatesI(){
        val pid = PID(0.0, 0.1, 0.0)
        pid.period = 0.1

        val initialOutput = pid.calculate(0.0, 10.0)
        assertEquals(0.1, initialOutput, 0.001)
        val output = pid.calculate(1.0, 10.0)
        assertEquals(0.19, output, 0.001)
    }

    @Test
    fun calculatesPID(){
        val pid = PID(0.1, 0.2, 0.01)
        pid.period = 0.1

        val initialOutput = pid.calculate(0.0, 10.0)
        assertEquals(1.2, initialOutput, 0.001)
        val output = pid.calculate(1.0, 10.0)
        assertEquals(1.18, output, 0.001)
    }

    @Test
    fun usesPositionToleranceInAtSetpoint(){
        val pid = PID(0.1, 0.0, 0.0)
        pid.period = 0.1
        pid.positionTolerance = 0.1

        pid.calculate(0.0, 10.0)
        assertFalse(pid.atSetpoint())

        pid.calculate(9.9, 10.0)
        assertTrue(pid.atSetpoint())
    }

    @Test
    fun usesVelocityToleranceInAtSetpoint(){
        val pid = PID(0.1, 0.0, 0.0)
        pid.period = 0.1
        pid.positionTolerance = 0.1
        pid.velocityTolerance = 0.1

        pid.calculate(0.0, 10.0)
        assertFalse(pid.atSetpoint())

        pid.calculate(9.9, 10.0)
        assertFalse(pid.atSetpoint())

        pid.calculate(9.9, 10.0)
        assertTrue(pid.atSetpoint())
    }

    @Test
    fun canReset(){
        val pid = PID(0.0, 0.1, 0.0)
        pid.period = 0.1
        pid.calculate(0.0, 10.0)
        pid.calculate(0.0, 10.0)

        assertFalse(pid.positionError.isNaN())
        assertFalse(pid.velocityError.isNaN())

        pid.reset()

        assertTrue(pid.positionError.isNaN())
        assertTrue(pid.velocityError.isNaN())
        assertEquals(0.1, pid.calculate(0.0, 10.0), 0.001)
    }

    @Test
    fun usesIntegratorRange(){
        val pid = PID(0.0, 0.1, 0.0)
        pid.period = 0.1
        pid.integratorRange = Range(-0.1, 0.1)

        val o1 = pid.calculate(0.0, 10.0)
        assertEquals(0.1, o1, 0.001)
        val o2 = pid.calculate(1.0, 10.0)
        assertEquals(0.1, o2, 0.001)

        pid.reset()

        val o3 = pid.calculate(0.0, -10.0)
        assertEquals(-0.1, o3, 0.001)
        val o4 = pid.calculate(-1.0, -10.0)
        assertEquals(-0.1, o4, 0.001)
    }
}