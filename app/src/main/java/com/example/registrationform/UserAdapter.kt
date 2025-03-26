package com.example.registrationform

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.registrationform.data.Student
import com.example.registrationform.databinding.AllUsersBinding

class UserAdapter(private var studentList: List<Student>) :
    RecyclerView.Adapter<UserAdapter.StudentViewHolder>() {

    inner class StudentViewHolder(val binding: AllUsersBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(student: Student) {
            binding.tvName.text = student.name
            binding.tvEmail.text = "Email: ${student.email}"
            binding.tvContact.text = "Contact: ${student.contact}"
            binding.tvDOB.text = "DOB: ${student.dob}"
            binding.tvGender.text = "Gender: ${student.gender}"
            binding.tvAddress.text = "Address: ${student.address}"
            binding.tvPassingYear.text = "Passing Year: ${student.passingYear}"
            binding.tvCollegeFees.text = "College Fees: ₹${student.collegeFees}"
            binding.tvExamFees.text = "Exam Fees: ₹${student.examFees}"
            binding.tvRegFees.text = "Reg Fees: ₹${student.regFees}"
            binding.tvTotalFees.text = "Total Fees: ₹${student.totalFees}"

            // Handle button click inside the bind method
            binding.btnUpdate.setOnClickListener {
                val context = binding.root.context
                val intent = Intent(context, MainActivity::class.java).apply {
                    putExtra("id", student.id)
                    putExtra("name", student.name)
                    putExtra("email", student.email)
                    putExtra("contact", student.contact)
                    putExtra("dob", student.dob)
                    putExtra("gender", student.gender)
                    putExtra("address", student.address)
                    putExtra("passingYear", student.passingYear)
                    putExtra("collegeFees", student.collegeFees?.toDouble())
                    putExtra("examFees", student.examFees?.toDouble())
                    putExtra("regFees", student.regFees?.toDouble())
                    putExtra("totalFees", student.totalFees?.toDouble())
                }
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val binding = AllUsersBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StudentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        holder.bind(studentList[position])
    }

    override fun getItemCount(): Int = studentList.size

    fun updateData(newList: List<Student>) {
        studentList = newList
        notifyDataSetChanged()
    }
}
