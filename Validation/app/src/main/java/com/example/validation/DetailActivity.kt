package com.example.validation

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.validation.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val name = intent.getStringExtra("EXTRA_NAME") ?: "Chưa có thông tin"
        val email = intent.getStringExtra("EXTRA_EMAIL") ?: "Chưa có thông tin"
        val phone = intent.getStringExtra("EXTRA_PHONE") ?: "Chưa có thông tin"
        val studentId = intent.getStringExtra("EXTRA_STUDENT_ID") ?: "Chưa có thông tin"
        val studentClass = intent.getStringExtra("EXTRA_CLASS") ?: "Chưa có thông tin"

        binding.tvFullName.text = "Họ tên: $name"
        binding.tvEmail.text = "Email: $email"
        binding.tvPhone.text = "Số điện thoại: $phone"
        binding.tvStudentId.text = "Mã sinh viên (MSV): $studentId"
        binding.tvClass.text = "Lớp: $studentClass"

        binding.btnGoToRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }
    }
}
