package com.example.registrationform

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.registrationform.data.Student
import com.example.registrationform.databinding.ActivityAlluserBinding
import com.google.firebase.database.*

class AlluserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAlluserBinding
    private lateinit var database: DatabaseReference
    private lateinit var studentAdapter: UserAdapter
    private val studentList = mutableListOf<Student>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlluserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        studentAdapter = UserAdapter(studentList, )
        binding.recyclerview.apply {
            layoutManager = LinearLayoutManager(this@AlluserActivity)
            adapter = studentAdapter
        }

        fetchStudents()
    }

    private fun fetchStudents() {
        database = FirebaseDatabase.getInstance().getReference("Students")

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                studentList.clear()
                for (userSnapshot in snapshot.children) {
                    val user = userSnapshot.getValue(Student::class.java)

                    // Add unique ID separately
                    user?.id = userSnapshot.key.toString()

                    user?.let { studentList.add(it) }
                }

                studentAdapter.updateData(studentList)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", "Error: ${error.message}")
            }
        })
    }
}