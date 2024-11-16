package com.aylas.moreno.laboratoriocalificado03
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.aylas.moreno.laboratoriocalificado03.databinding.ActivityEjercicio01Binding
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import okhttp3.*
import java.io.IOException

class Ejercicio01 : AppCompatActivity() {

    private lateinit var binding: ActivityEjercicio01Binding
    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEjercicio01Binding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerViewTeachers.layoutManager = LinearLayoutManager(this)

        fetchTeachers()
    }

    private fun fetchTeachers() {
        val request = Request.Builder()
            .url("https://private-effe28-tecsup1.apiary-mock.com/list/teacher")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@Ejercicio01, "Error al cargar datos. Verifica tu conexión.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                if (body != null) {
                    try {
                        val jsonObject = Gson().fromJson(body, JsonObject::class.java)
                        val teachersJsonArray = jsonObject.getAsJsonArray("teachers")
                        val teacherListType = object : TypeToken<List<Teacher>>() {}.type
                        val teachers = Gson().fromJson<List<Teacher>>(teachersJsonArray, teacherListType)

                        runOnUiThread {
                            binding.recyclerViewTeachers.adapter = TeacherAdapter(this@Ejercicio01, teachers)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        runOnUiThread {
                            Toast.makeText(this@Ejercicio01, "Error al procesar los datos", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        })
    }
}
