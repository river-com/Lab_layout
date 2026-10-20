package com.example.mophonggiaodien

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    private val _student = MutableLiveData(
        Student(
            name = "Nguyễn Văn An",
            studentId = "22110001",
            className = "22110CLA",
            gpa = 3.85
        )
    )
    val student: LiveData<Student> = _student

    private val _cardElevationDp = MutableLiveData(8)
    val cardElevationDp: LiveData<Int> = _cardElevationDp

    private val _cardCornerRadiusDp = MutableLiveData(24)
    val cardCornerRadiusDp: LiveData<Int> = _cardCornerRadiusDp

    private val _contentPaddingDp = MutableLiveData(20)
    val contentPaddingDp: LiveData<Int> = _contentPaddingDp

    fun updateStudent(name: String, studentId: String, className: String, gpa: Double) {
        _student.value = Student(name, studentId, className, gpa)
    }

    fun setCardElevation(elevationDp: Int) {
        _cardElevationDp.value = elevationDp
    }

    fun setCardCornerRadius(radiusDp: Int) {
        _cardCornerRadiusDp.value = radiusDp
    }

    fun setContentPadding(paddingDp: Int) {
        _contentPaddingDp.value = paddingDp
    }
}
