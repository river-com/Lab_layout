package com.example.mophonggiaodien

import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.mophonggiaodien.databinding.ActivityMainBinding
import com.example.mophonggiaodien.databinding.DialogEditAttributeBinding
import com.example.mophonggiaodien.databinding.DialogEditStudentBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.mainContainer) { view, insets ->
            val statusBar = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            val navBar = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
            view.setPadding(0, statusBar.top, 0, navBar.bottom)
            insets
        }

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        setupObservers()
        setupListeners()
        setupCodeSnippetDisplay()
    }

    private fun setupObservers() {
        viewModel.student.observe(this) { student ->
            updateStudentUI(student)
        }

        viewModel.cardElevationDp.observe(this) { elevationDp ->
            val px = elevationDp.dpToPx()
            binding.cardStudent.cardElevation = px
            binding.cardStudent.maxCardElevation = px
            binding.tvElevationLabel.text = "app:cardElevation=\"${elevationDp}dp\" (Đổ bóng 3D)"
        }

        viewModel.cardCornerRadiusDp.observe(this) { radiusDp ->
            binding.cardStudent.radius = radiusDp.dpToPx()
            binding.tvCornerRadiusLabel.text = "app:cardCornerRadius=\"${radiusDp}dp\" (Bo góc)"
        }

        viewModel.contentPaddingDp.observe(this) { paddingDp ->
            val px = paddingDp.dpToPx().toInt()
            binding.cardStudent.setContentPadding(px, px, px, px)
            binding.tvContentPaddingLabel.text = "app:contentPadding=\"${paddingDp}dp\" (Đệm trong)"
        }
    }

    private fun updateStudentUI(student: Student) {
        binding.tvStudentName.text = student.name
        binding.tvGpa.text = String.format("%.2f", student.gpa)
        binding.tvStudentDetails.text = "MSSV: ${student.studentId} • Lớp: ${student.className}"
        binding.tvResult.text = student.resultClassification
    }

    private fun setupListeners() {
        // Touch card to open edit student dialog
        binding.cardStudent.setOnClickListener {
            viewModel.student.value?.let { currentStudent ->
                showEditStudentDialog(currentStudent)
            }
        }

        // Touch elevation label to edit elevation
        binding.btnElevation.setOnClickListener {
            val currentElevation = viewModel.cardElevationDp.value ?: 8
            showEditAttributeDialog("Thay đổi Độ nổi (Elevation - dp)", currentElevation) { newDp ->
                viewModel.setCardElevation(newDp)
            }
        }

        // Touch corner radius label to edit corner radius
        binding.btnCornerRadius.setOnClickListener {
            val currentRadius = viewModel.cardCornerRadiusDp.value ?: 24
            showEditAttributeDialog("Thay đổi Bo góc (Corner Radius - dp)", currentRadius) { newDp ->
                viewModel.setCardCornerRadius(newDp)
            }
        }

        // Touch content padding label to edit content padding
        binding.btnContentPadding.setOnClickListener {
            val currentPadding = viewModel.contentPaddingDp.value ?: 20
            showEditAttributeDialog("Thay đổi Đệm trong (Content Padding - dp)", currentPadding) { newDp ->
                viewModel.setContentPadding(newDp)
            }
        }
    }

    private fun showEditStudentDialog(student: Student) {
        val dialogBinding = DialogEditStudentBinding.inflate(LayoutInflater.from(this))

        dialogBinding.etName.setText(student.name)
        dialogBinding.etStudentId.setText(student.studentId)
        dialogBinding.etClassName.setText(student.className)
        dialogBinding.etGpa.setText(student.gpa.toString())

        val alertDialog = AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .setCancelable(true)
            .create()

        // Set transparent window background so MaterialCardView rounded corners show nicely
        alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialogBinding.btnCancel.setOnClickListener {
            alertDialog.dismiss()
        }

        dialogBinding.btnSave.setOnClickListener {
            val name = dialogBinding.etName.text.toString().trim()
            val studentId = dialogBinding.etStudentId.text.toString().trim()
            val className = dialogBinding.etClassName.text.toString().trim()
            val gpaStr = dialogBinding.etGpa.text.toString().trim()

            if (name.isEmpty() || studentId.isEmpty() || className.isEmpty() || gpaStr.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val gpa = gpaStr.toDoubleOrNull()
            if (gpa == null || gpa < 0.0 || gpa > 4.0) {
                Toast.makeText(this, "GPA phải nằm trong khoảng từ 0.0 đến 4.0!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.updateStudent(name, studentId, className, gpa)
            Toast.makeText(this, "Cập nhật thông tin sinh viên thành công!", Toast.LENGTH_SHORT).show()
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    private fun showEditAttributeDialog(title: String, currentValue: Int, onSave: (Int) -> Unit) {
        val dialogBinding = DialogEditAttributeBinding.inflate(LayoutInflater.from(this))

        dialogBinding.tvAttributeTitle.text = title
        dialogBinding.etAttributeValue.setText(currentValue.toString())

        val alertDialog = AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .setCancelable(true)
            .create()

        alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialogBinding.btnAttrCancel.setOnClickListener {
            alertDialog.dismiss()
        }

        dialogBinding.btnAttrSave.setOnClickListener {
            val valStr = dialogBinding.etAttributeValue.text.toString().trim()
            val newDp = valStr.toIntOrNull()

            if (newDp == null || newDp < 0) {
                Toast.makeText(this, "Giá trị không hợp lệ! Vui lòng nhập số nguyên >= 0.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            onSave(newDp)
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    private fun setupCodeSnippetDisplay() {
        val codeHtml = """
            <font color='#10B981'>// -- ĐIỀU KHIỂN TRONG KOTLIN ACTIVITY ------------</font><br/><br/>
            <font color='#10B981'>// 1. Cập nhật nội dung các View bên trong Card</font><br/>
            <font color='#FFFFFF'>binding.tvStudentName.text = student.name</font><br/>
            <font color='#FFD54F'>binding.tvGpa.text = "${'$'}{student.gpa} / 4.0"</font><br/><br/>
            <font color='#10B981'>// 2. Tùy biến thuộc tính CardView động tại runtime</font><br/>
            <font color='#4FC3F7'>binding.cardStudent.setCardBackgroundColor(getColor(R.color.blue))</font><br/>
            <font color='#4FC3F7'>binding.cardStudent.radius = 32f         <font color='#10B981'>// Đổi bo góc</font></font><br/>
            <font color='#4FC3F7'>binding.cardStudent.cardElevation = 16f <font color='#10B981'>// Tăng độ nổi</font></font>
        """.trimIndent()

        binding.tvCodeSnippet.text = Html.fromHtml(codeHtml, Html.FROM_HTML_MODE_LEGACY)
    }

    private fun Int.dpToPx(): Float {
        return this * resources.displayMetrics.density
    }
}
