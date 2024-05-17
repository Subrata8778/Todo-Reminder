package com.example.todoreminder.viewmodel

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.todoreminder.NotificationReceiver
import com.example.todoreminder.model.Task
import org.json.JSONArray
import org.json.JSONObject
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class TaskViewModel(application: Application) : AndroidViewModel(application) {

    val todoTasks = MutableLiveData<List<Task>>()
    val inProgressTasks = MutableLiveData<List<Task>>()
    val doneTasks = MutableLiveData<List<Task>>()
    private val _tasksByDate = MutableLiveData<List<List<Task>>>()
    val tasksByDate: LiveData<List<List<Task>>> get() = _tasksByDate

    private var queue: RequestQueue = Volley.newRequestQueue(application)

    val currentTask = MutableLiveData<Task>()
    private val sharedPreferences = application.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    private val _tasksForDate = MutableLiveData<List<Task>>()
    val tasksForDate: LiveData<List<Task>> get() = _tasksForDate

    fun fetchTasks(status: String, limit: Int) {
        val username = sharedPreferences.getString("username", null)
        if (username == null) {
            Toast.makeText(getApplication(), "No logged-in user", Toast.LENGTH_SHORT).show()
            return
        }

        val url = "http://192.168.1.14/JOB/Inovasi/getTodo.php?username=$username&status=$status&limit=$limit"
        Log.d("TaskViewModel", "Request URL: $url")

        val stringRequest = StringRequest(Request.Method.GET, url,
            Response.Listener { response ->
                Log.d("TaskViewModel", "Response: $response")
                val tasks = parseTasks(response)
                when (status) {
                    "Todo" -> todoTasks.value = tasks
                    "InProgress" -> inProgressTasks.value = tasks
                    "Done" -> doneTasks.value = tasks
                }
                // Update tasksByDate for grouping tasks by date
                val allTasks = todoTasks.value.orEmpty() + inProgressTasks.value.orEmpty() + doneTasks.value.orEmpty()
                _tasksByDate.value = allTasks.groupBy { it.due.get()?.split(" ")!![0] }.values.toList()
            },
            Response.ErrorListener {
                Log.e("TaskViewModel", "Failed to fetch tasks", it)
                Toast.makeText(getApplication(), "Failed to fetch tasks", Toast.LENGTH_SHORT).show()
            })
        queue.add(stringRequest)
    }

    fun fetchTasksForDate(date: String) {
        val username = sharedPreferences.getString("username", null)
        if (username == null) {
            Toast.makeText(getApplication(), "No logged-in user", Toast.LENGTH_SHORT).show()
            return
        }
        // Format tanggal ke YYYY-MM-DD
        val formattedDate = formatDateString(date)

        val url = "http://192.168.1.14/JOB/Inovasi/getTodoByDate.php?username=$username&date=$formattedDate"
        val stringRequest = StringRequest(Request.Method.GET, url,
            Response.Listener { response ->
                Log.d("TaskViewModel", "Response: $response")
                val tasks = parseTasks(response)
                _tasksForDate.value = tasks
                tasks.forEach { task ->
                    scheduleNotification(task)
                }
            },
            Response.ErrorListener {
                Log.e("TaskViewModel", "Failed to fetch tasks", it)
                Toast.makeText(getApplication(), "Failed to fetch tasks", Toast.LENGTH_SHORT).show()
            })
        queue.add(stringRequest)
    }

    private fun formatDateString(date: String): String {
        // Expecting date in format YYYY-M-D
        val inputFormat = SimpleDateFormat("yyyy-M-d", Locale.getDefault())
        val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val parsedDate = inputFormat.parse(date)
        return outputFormat.format(parsedDate)
    }

    fun updateTaskStatus(taskId: Int, newStatus: String) {
        val url = "http://192.168.1.14/JOB/Inovasi/updateTodo.php"
        val stringRequest = object : StringRequest(Request.Method.POST, url,
            Response.Listener { response ->
                Log.e("TAG", "Response: $response")
                fetchTasks("Todo", 3)
                fetchTasks("InProgress", 3)
                fetchTasks("Done", 3)
            },
            Response.ErrorListener {
                Toast.makeText(getApplication(), "Failed to update task status", Toast.LENGTH_SHORT).show()
            }) {
            override fun getParams(): Map<String, String?> {
                return mapOf(
                    "id" to taskId.toString(),
                    "status" to newStatus
                )
            }
        }
        queue.add(stringRequest)
    }

    fun getTasksForStatus(status: String): LiveData<List<Task>> {
        return when (status) {
            "Todo" -> todoTasks
            "InProgress" -> inProgressTasks
            "Done" -> doneTasks
            else -> MutableLiveData(emptyList())
        }
    }

    private fun parseTasks(response: String): List<Task> {
        val tasks = mutableListOf<Task>()
        try {
            if (response.isNotEmpty()) {
                val jsonArray = JSONArray(response)
                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    val task = Task(
                        id = jsonObject.getInt("id"),
                        username = jsonObject.getString("username"),
                        title = ObservableField(jsonObject.getString("title")),
                        description = ObservableField(jsonObject.getString("description")),
                        due = ObservableField(jsonObject.getString("due")),
                        status = jsonObject.getString("status"),
                        file = jsonObject.getString("file")
                    )
                    tasks.add(task)
                }
            }
        } catch (e: Exception) {
            Log.e("TaskViewModel", "Error parsing tasks", e)
        }
        return tasks
    }

    private fun scheduleNotification(task: Task) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()) // WIB time zone
        try {
            val dueDateString = task.due.get()
            Log.e("TaskViewModel", "$dueDateString")
            if (dueDateString != null) {
                val dueDate = dateFormat.parse(dueDateString)
                Log.e("TaskViewModel", "$dueDate")
                if (dueDate != null) {
                    val calendar = Calendar.getInstance().apply {
                        time = dueDate
                        add(Calendar.HOUR, -3)  // Reminder 3 hours before the due date
                    }

                    val alarmManager = getApplication<Application>().getSystemService(Context.ALARM_SERVICE) as AlarmManager
                    val intent = Intent(getApplication(), NotificationReceiver::class.java).apply {
                        putExtra("title", task.title.get())
                        putExtra("description", task.description.get())
                    }
                    val pendingIntent = PendingIntent.getBroadcast(
                        getApplication(),
                        task.id,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )

                    // Check if the app has the required permission
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                        if (getApplication<Application>().checkSelfPermission("android.permission.SCHEDULE_EXACT_ALARM") == PackageManager.PERMISSION_GRANTED) {
                            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
                        } else {
                            // Handle the case where permission is not granted
                            Log.e("TaskViewModel", "Permission SCHEDULE_EXACT_ALARM not granted")
                        }
                    } else {
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
                    }
                } else {
                    Log.e("TaskViewModel", "Due date is null for task: ${task.title.get()}")
                }
            } else {
                Log.e("TaskViewModel", "Due date string is null for task: ${task.title.get()}")
            }
        } catch (e: ParseException) {
            Log.e("TaskViewModel", "Error parsing due date for task: ${task.title.get()}", e)
        }
    }
}
