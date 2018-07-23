package com.example.linh.vietkitchen.extension

import org.hamcrest.CoreMatchers
import org.hamcrest.core.IsNot.not
import org.junit.Assert.assertThat
import org.junit.Assert
import org.junit.Test

class CollectionExtensionTest{
    @Test fun testToVarargArray(){
        val map = mapOf<Int, String>(
                Pair(1, "one"),
                Pair(2, "two"),
                Pair(3, "three")
        )
        val actual = map.toVarargArray()
//        assertThat(actual, not(IsEmptyCollection.empty()))
    }
}