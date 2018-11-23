package com.example.linh.vietkitchen.extension

import com.example.linh.vietkitchen.util.toDateString
import org.junit.Assert.assertEquals
import org.junit.Test
import java.text.DateFormat

class ExtensionUtilsTest{
    @Test fun testLongToDate(){
        val expected = "Oct 20, 2015"
        val actual = 1445275635000L.toDateString()
        assertEquals(expected, actual)
    }

    @Test fun testLongToDateFullFormat(){
        val expected = "Tuesday, October 20, 2015"
        val actual = 1445275635000L.toDateString(DateFormat.FULL)
        assertEquals(expected, actual)
    }
}