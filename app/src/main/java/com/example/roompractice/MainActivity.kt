package com.example.roompractice

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.example.roompractice.databinding.ActivityMainBinding
import com.example.roompractice.model.data.Student
import com.example.roompractice.model.repository.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var appDb : AppDatabase
    lateinit var notificationManager : NotificationManager
    lateinit var notificationChannel : NotificationChannel
    private val channelId = "ADD_DATA"
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
            showNotification()
            binding.etFirstName.text.clear()
            binding.etLastName.text.clear()
            binding.etRollNo.text.clear()
            Toast.makeText(this@MainActivity, "Successfully written", Toast.LENGTH_SHORT).show()

        } else {
            Toast.makeText(this@MainActivity, "please Enter Data", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showNotification() {
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

/** Set Notification tap's action **/
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_IMMUTABLE)

        /** NotificationChannel can only be created for api 26+ **/
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            notificationChannel =
                NotificationChannel(channelId, "description", NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableVibration(true)
            notificationManager.createNotificationChannel(notificationChannel)
        }

        /** create notification **/
        val notifyBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notify)
            .setContentTitle("Student ${binding.etFirstName.text}")
            .setContentText("roll no ${binding.etRollNo.text} data added successfully!!")
            /** expandable notification **/
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Student with roll no ${binding.etRollNo.text}, name ${binding.etFirstName.text} ${binding.etLastName.text} data added successfully!!")
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            /** on tap move to next intent and clear notification **/
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        /** show the notification **/
        notificationManager.notify(Random.nextInt(100), notifyBuilder.build())
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
            binding.tvFirstName.text = student.firstName
            binding.tvLastName.text = student.lastName
            binding.tvRollNo.text = student.rollNo.toString()
        }

    }
}