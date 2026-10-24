package com.example.validation

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
import com.example.validation.databinding.ActivityRegisterBinding
import com.google.android.material.snackbar.Snackbar

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegister.setOnClickListener {
            if (validateInputs()) {
                val fullName = binding.etFullName.text.toString().trim()
                val email = binding.etEmail.text.toString().trim()
                val phone = binding.etPhone.text.toString().trim()
                val studentId = binding.etStudentId.text.toString().trim()
                val studentClass = binding.etClass.text.toString().trim()

                Snackbar.make(binding.root, "Đăng ký thành công!", Snackbar.LENGTH_LONG)
                    .setAction("Chi tiết") {
                        navigateToDetail(fullName, email, phone, studentId, studentClass)
                    }
                    .show()
            }
        }

        binding.btnGoToDetail.setOnClickListener {
            val fullName = binding.etFullName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val phone = binding.etPhone.text.toString().trim()
            val studentId = binding.etStudentId.text.toString().trim()
            val studentClass = binding.etClass.text.toString().trim()
            navigateToDetail(fullName, email, phone, studentId, studentClass)
        }
    }

    private fun validateInputs(): Boolean {
        var isValid = true

        val fullName = binding.etFullName.text.toString().trim()
        if (fullName.isEmpty()) {
            binding.tilFullName.error = "Họ tên không được để trống"
            isValid = false
        } else if (fullName.length < 2) {
            binding.tilFullName.error = "Họ tên phải có ít nhất 2 ký tự"
            isValid = false
        } else {
            binding.tilFullName.error = null
        }

        val email = binding.etEmail.text.toString().trim()
        if (email.isEmpty()) {
            binding.tilEmail.error = "Email không được để trống"
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.error = "Email không hợp lệ"
            isValid = false
        } else {
            binding.tilEmail.error = null
        }

        val phone = binding.etPhone.text.toString().trim()
        if (phone.isEmpty()) {
            binding.tilPhone.error = "Số điện thoại không được để trống"
            isValid = false
        } else if (!Patterns.PHONE.matcher(phone).matches() || phone.length < 10) {
            binding.tilPhone.error = "Số điện thoại không hợp lệ (tối thiểu 10 số)"
            isValid = false
        } else {
            binding.tilPhone.error = null
        }

        val studentId = binding.etStudentId.text.toString().trim()
        if (studentId.isEmpty()) {
            binding.tilStudentId.error = "Mã sinh viên không được để trống"
            isValid = false
        } else {
            binding.tilStudentId.error = null
        }

        val studentClass = binding.etClass.text.toString().trim()
        if (studentClass.isEmpty()) {
            binding.tilClass.error = "Lớp không được để trống"
            isValid = false
        } else {
            binding.tilClass.error = null
        }

        return isValid
    }

    private fun navigateToDetail(name: String, email: String, phone: String, studentId: String, studentClass: String) {
        val intent = Intent(this, DetailActivity::class.java).apply {
            putExtra("EXTRA_NAME", name)
            putExtra("EXTRA_EMAIL", email)
            putExtra("EXTRA_PHONE", phone)
            putExtra("EXTRA_STUDENT_ID", studentId)
            putExtra("EXTRA_CLASS", studentClass)
        }
        startActivity(intent)
    }
}
