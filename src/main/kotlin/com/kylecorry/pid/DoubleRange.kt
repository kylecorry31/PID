package com.kylecorry.pid

import java.lang.IllegalArgumentException

data class Range(val min: Double, val max: Double){

    init {
        if (min > max){
            throw IllegalArgumentException("Min must be less than max")
        }
    }

}