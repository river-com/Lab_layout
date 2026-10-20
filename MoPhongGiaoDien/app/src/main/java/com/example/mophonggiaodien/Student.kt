package com.example.mophonggiaodien

import java.io.Serializable

data class Student(
    var name: String,
    var studentId: String,
    var className: String,
    var gpa: Double
) : Serializable {
    val resultClassification: String
        get() = when {
            gpa >= 3.6 -> "XUẤT SẮC (Top 5% Khóa)"
            gpa >= 3.2 -> "GIỎI (Top 15% Khóa)"
            gpa >= 2.5 -> "KHÁ"
            else -> "TRUNG BÌNH"
        }
}
