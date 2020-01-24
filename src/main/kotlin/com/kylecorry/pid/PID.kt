package com.kylecorry.pid

import java.lang.IllegalArgumentException
import kotlin.math.abs

/**
 * A PID controller
 * @param p the proportional constant
 * @param i the integral constant
 * @param d the differential constant
 */
class PID(p: Double, i: Double, d: Double) {

    /**
     * The proportional constant
     */
    var P = p
        set(value) {
            if (value < 0){
                throw IllegalArgumentException("P must be non-negative")
            }
            field = value
        }

    /**
     * The integral constant
     */
    var I = i
        set(value) {
            if (value < 0){
                throw IllegalArgumentException("I must be non-negative")
            }
            field = value
        }

    /**
     * The differential constant
     */
    var D = d
        set(value) {
            if (value < 0){
                throw IllegalArgumentException("D must be non-negative")
            }
            field = value
        }

    /**
     * The tolerance for position error
     * Defaults to 0.05
     */
    var positionTolerance = 0.05
        set(value) {
            if (value < 0){
                throw IllegalArgumentException("Position tolerance must be non-negative")
            }
            field = value
        }

    /**
     * The tolerance for velocity error
     * Defaults to infinity
     */
    var velocityTolerance = Double.POSITIVE_INFINITY
        set(value) {
            if (value < 0){
                throw IllegalArgumentException("Velocity tolerance must be non-negative")
            }
            field = value
        }

    /**
     * The maximum amount the integral can contribute to the PID output
     * Defaults to +/- infinity
     */
    var integratorRange = Range(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY)

    /**
     * The time between calls to calculate in second
     * Defaults to 0.01 seconds
     */
    var period = 0.01
        set(value) {
            if (value <= 0){
                throw IllegalArgumentException("Period must be positive")
            }
            field = value
        }

    /**
     * The current error in position
     */
    var positionError = Double.NaN
        private set

    /**
     * The current error in velocity
     */
    var velocityError = Double.NaN
        private set

    private var totalError = 0.0

    /**
     * Calculate the PID
     * @param actualPosition the actual position
     * @param desiredPosition the desired position
     * @param deltaTime the time since the last calculate call in seconds. Defaults to the period.
     * @return the calculated PID value
     */
    fun calculate(actualPosition: Double, desiredPosition: Double, deltaTime: Double = period): Double {
        val lastError = positionError
        positionError = desiredPosition - actualPosition

        var dTerm = 0.0

        if (!lastError.isNaN()){
            velocityError = (positionError - lastError) / deltaTime
            dTerm = velocityError * D
        }

        if (I != 0.0) {
            totalError = clamp(totalError + positionError * deltaTime, integratorRange.min / I, integratorRange.max / I)
        }

        return P * positionError + I * totalError + dTerm
    }

    fun atSetpoint(): Boolean {
        return abs(positionError) <= positionTolerance && abs(velocityError) <= velocityTolerance
    }

    fun reset() {
        positionError = Double.NaN
        velocityError = Double.NaN
        totalError = 0.0
    }

    private fun clamp(value: Double, min: Double, max: Double): Double {
        if (value < min) return min
        if (value > max) return max
        return value
    }
}