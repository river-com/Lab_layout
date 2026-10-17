package com.example.labdidong

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.labdidong.databinding.FragmentFirstBinding

/**
 * Fragment hiển thị giao diện gồm CardView, RadioButton, TextView, EditText, Switch...
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Sự kiện khi bấm nút "Lưu thông tin"
        binding.btnSubmit.setOnClickListener {
            val fullName = binding.etFullName.text?.toString()?.trim() ?: ""
            val email = binding.etEmail.text?.toString()?.trim() ?: ""

            // Lấy giới tính từ RadioGroup
            val gender = when (binding.rgGender.checkedRadioButtonId) {
                R.id.rb_male -> getString(R.string.gender_male)
                R.id.rb_female -> getString(R.string.gender_female)
                R.id.rb_other -> getString(R.string.gender_other)
                else -> "Chưa chọn"
            }

            // Lấy trạng thái từ Switch
            val isNotificationEnabled = binding.switchNotification.isChecked
            val isBatterySaverEnabled = binding.switchBatterySaver.isChecked

            // Kiểm tra nhập dữ liệu
            if (fullName.isEmpty()) {
                binding.tilFullName.error = "Vui lòng nhập họ và tên"
                return@setOnClickListener
            } else {
                binding.tilFullName.error = null
            }

            if (email.isEmpty()) {
                binding.tilEmail.error = "Vui lòng nhập email"
                return@setOnClickListener
            } else {
                binding.tilEmail.error = null
            }

            // Tổng hợp thông tin hiển thị lên CardView kết quả
            val resultText = """
                📌 Họ và tên: $fullName
                📧 Email: $email
                👤 Giới tính: $gender
                🔔 Thông báo qua Email: ${if (isNotificationEnabled) "Bật" else "Tắt"}
                🔋 Tiết kiệm pin: ${if (isBatterySaverEnabled) "Bật" else "Tắt"}
            """.trimIndent()

            binding.tvResult.text = resultText

            Toast.makeText(requireContext(), "Lưu thông tin thành công!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}