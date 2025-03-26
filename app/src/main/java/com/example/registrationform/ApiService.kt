package com.example.registrationform

import com.example.registrationform.data.Student
import retrofit2.Call
import retrofit2.Callback
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @POST("students")
    @GET("students")
    fun getStudent(): Call<List<Student>>
    fun postStudent(@Body student: Student): Call<Student>
}