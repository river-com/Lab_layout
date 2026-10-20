package com.example.mophonggiaodien

import org.junit.Assert.assertEquals
import org.junit.Test

class StudentUnitTest {

    @Test
    fun studentClassification_isCorrect() {
        val studentExcellent = Student("Nguyễn Văn An", "22110001", "22110CLA", 3.85)
        assertEquals("XUẤT SẮC (Top 5% Khóa)", studentExcellent.resultClassification)

        val studentGood = Student("Lê Văn B", "22110002", "22110CLA", 3.4)
        assertEquals("GIỎI (Top 15% Khóa)", studentGood.resultClassification)

        val studentFair = Student("Trần Thị C", "22110003", "22110CLA", 2.8)
        assertEquals("KHÁ", studentFair.resultClassification)

        val studentAverage = Student("Phạm Văn D", "22110004", "22110CLA", 2.0)
        assertEquals("TRUNG BÌNH", studentAverage.resultClassification)
    }

    @Test
    fun studentGpaValidation_worksCorrectly() {
        fun isValidGpa(gpaStr: String): Boolean {
            val gpa = gpaStr.toDoubleOrNull() ?: return false
            return gpa in 0.0..4.0
        }

        assertEquals(true, isValidGpa("3.85"))
        assertEquals(true, isValidGpa("0.0"))
        assertEquals(true, isValidGpa("4.0"))
        assertEquals(false, isValidGpa("-1.0"))
        assertEquals(false, isValidGpa("4.5"))
        assertEquals(false, isValidGpa("abc"))
    }
}
