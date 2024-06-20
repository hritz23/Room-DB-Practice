package com.example.roompractice

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.roompractice.databinding.ActivityMainBinding
import com.example.roompractice.model.data.Student
import com.example.roompractice.model.repository.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var appDb : AppDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        appDb = AppDatabase.getDatabase(this)

        binding.btnRead.setOnClickListener { readData() }
        binding.btnWrite.setOnClickListener { writeData() }
    }

    private fun writeData() {
        if (binding.etFirstName.text.toString().isNotEmpty() &&
            binding.etLastName.text.toString().isNotEmpty() &&
            binding.etRollNo.text.toString().isNotEmpty())
        {
            val  student = Student(
                null,
                binding.etFirstName.text.toString(),
                binding.etLastName.text.toString(),
                binding.etRollNo.text.toString().toInt())

            GlobalScope.launch(Dispatchers.IO) {
                appDb.studentDao().insert(student)
            }
            binding.etFirstName.text.clear()
            binding.etLastName.text.clear()
            binding.etRollNo.text.clear()
            Toast.makeText(this@MainActivity, "Successfully written", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this@MainActivity, "please Enter Data", Toast.LENGTH_SHORT).show()
        }
    }

    private fun readData() {
        if (binding.etRollNoGet.text.isNotEmpty()) {
            lateinit var student : Student

            GlobalScope.launch {
                student = appDb.studentDao().findByRoll(binding.etRollNoGet.text.toString().toInt())
                display(student)
            }
        }
    }

    private suspend fun display(student: Student) {

        withContext(Dispatchers.Main){
            binding.etFirstName.setText(student.firstName)
            binding.etLastName.setText(student.lastName)
            binding.etRollNo.setText(student.rollNo.toString())
        }

    }
}