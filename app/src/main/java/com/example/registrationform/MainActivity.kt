package com.example.registrationform

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.registrationform.data.Student
import com.example.registrationform.databinding.ActivityMainBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private lateinit var dbRef: DatabaseReference
    private var studentId: String? = null  // To track if we are updating or creating a new entry

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        // Firebase initialization
        dbRef = FirebaseDatabase.getInstance().getReference("Students")

        // Year Spinner Setup
        val startYear = 1990
        val endYear = 2029
        val years = (startYear..endYear).map { it.toString() }.toTypedArray()

        binding.passingYearSpinner.adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, years)

        // Check if intent has data (for updating)
        if (intent.hasExtra("id")) {
            loadDataFromIntent()
        }

        // Button Listeners
        binding.buttonSubmit.setOnClickListener { saveOrUpdateStudent() }
    }

    // ✅ Load Student Data from Intent (for Update)
    private fun loadDataFromIntent() {
        studentId = intent.getStringExtra("id")

        // Populate UI with student data
        binding.editTextName.setText(intent.getStringExtra("name"))
        binding.editTextDOB.setText(intent.getStringExtra("dob"))
        binding.editTextEmail.setText(intent.getStringExtra("email"))
        binding.editTextMobile.setText(intent.getStringExtra("contact"))
        binding.editTextAddress.setText(intent.getStringExtra("address"))
        binding.passingYearSpinner.setSelection(getYearPosition(intent.getStringExtra("passingYear")))

        // ✅ Retrieve Fees as Double
        val collegeFees = intent.getDoubleExtra("collegeFees", 0.0)
        val examFees = intent.getDoubleExtra("examFees", 0.0)
        val regFees = intent.getDoubleExtra("regFees", 0.0)
        val totalFees = intent.getDoubleExtra("totalFees", 0.0)

        // Set fees in EditText fields
        binding.editTextCollegeFees.setText(collegeFees.toString())
        binding.editTextExamFees.setText(examFees.toString())
        binding.editTextRegFees.setText(regFees.toString())
        binding.editTextTotalFees.text = totalFees.toString()

        val gender = intent.getStringExtra("gender")
        if (gender == "Male") {
            binding.radioButtonMale.isChecked = true
        } else {
            binding.radioButtonFemale.isChecked = true
        }

        // Change button text to "Update"
        binding.buttonSubmit.text = "Update"
    }


    // Helper function to find year position in the spinner
    private fun getYearPosition(year: String?): Int {
        val years = (1990..2029).map { it.toString() }
        return years.indexOf(year ?: "2024")
    }

    // ✅ Save or Update Student Data
    private fun saveOrUpdateStudent() {
        if (!validateForm()) return

        val id = studentId ?: dbRef.push().key!!  // Use existing ID or generate new one

        val name = binding.editTextName.text.toString().trim()
        val dob = binding.editTextDOB.text.toString().trim()
        val email = binding.editTextEmail.text.toString().trim()
        val regFees = binding.editTextRegFees.text.toString().toDoubleOrNull() ?: 0.0
        val examFees = binding.editTextExamFees.text.toString().toDoubleOrNull() ?: 0.0
        val collegeFees = binding.editTextCollegeFees.text.toString().toDoubleOrNull() ?: 0.0
        val totalFees = regFees + collegeFees + examFees
        val address = binding.editTextAddress.text.toString().trim()
        val passingYear = binding.passingYearSpinner.selectedItem.toString().trim()
        val contact = binding.editTextMobile.text.toString().trim()
        val gender = if (binding.radioButtonMale.isChecked) "Male" else "Female"

        // Update Total Fees in the UI
        binding.editTextTotalFees.text = totalFees.toString()

        // Create Student object
        val student = Student(
            id, name, gender, dob, email, contact,
            regFees, collegeFees, examFees, totalFees, address, passingYear
        )

        // Save or Update in Firebase
        dbRef.child(id).setValue(student)
            .addOnCompleteListener {
                val message = if (studentId != null) "Updated Successfully" else "Saved Successfully"
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                sendToWebAPI(student)  // Send updated data to Web API
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to Save", Toast.LENGTH_SHORT).show()
            }
    }

    // ✅ Form Validation
    private fun validateForm(): Boolean {
        val name = binding.editTextName.text.toString().trim()
        val dob = binding.editTextDOB.text.toString().trim()
        val email = binding.editTextEmail.text.toString().trim()
        val contact = binding.editTextMobile.text.toString().trim()

        if (name.isEmpty() || dob.isEmpty() || email.isEmpty() || contact.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return false
        }

        if (!contact.matches(Regex("\\d{10}"))) {
            Toast.makeText(this, "Invalid Contact Number", Toast.LENGTH_SHORT).show()
            return false
        }

        if (!email.matches(Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.com$"))) {
            Toast.makeText(this, "Invalid Email Format", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    // ✅ Send Data to Web API (Retrofit)
    private fun sendToWebAPI(student: Student) {
        val apiService = RetrofitClient.instance

        apiService.postStudent(student)
            .enqueue(object : Callback<Student> {
                override fun onResponse(call: Call<Student>, response: Response<Student>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@MainActivity, "Data sent to API", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Student>, t: Throwable) {
                    Toast.makeText(this@MainActivity, "Failed to send data to API", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
