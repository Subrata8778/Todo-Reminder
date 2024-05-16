package com.example.todoreminder.viewmodel

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.todoreminder.model.Task
import kotlinx.coroutines.launch
import org.json.JSONArray

class TaskViewModel(application: Application) : AndroidViewModel(application) {

    val todoTasks = MutableLiveData<List<Task>>()
    val inProgressTasks = MutableLiveData<List<Task>>()
    val doneTasks = MutableLiveData<List<Task>>()
    private val _tasksByDate = MutableLiveData<List<List<Task>>>()
    val tasksByDate: LiveData<List<List<Task>>> get() = _tasksByDate

    private var queue: RequestQueue = Volley.newRequestQueue(application)

    val currentTask = MutableLiveData<Task>()

    fun fetchTasks(status: String, limit: Int) {
        val url = "http://192.168.1.14/JOB/Inovasi/getTodo.php?status=$status&limit=$limit"
        val stringRequest = StringRequest(Request.Method.GET, url,
            Response.Listener { response ->
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
                Toast.makeText(getApplication(), "Failed to fetch tasks", Toast.LENGTH_SHORT).show()
            })
        queue.add(stringRequest)
    }

    fun updateTaskStatus(taskId: Int, newStatus: String) {
        val url = "http://192.168.1.14/JOB/Inovasi/updateTodo.php"
        val stringRequest = object : StringRequest(Request.Method.POST, url,
            Response.Listener { response ->
                Log.e("TAG", "Response: $response")
                fetchTasks("Todo", Int.MAX_VALUE)
                fetchTasks("InProgress", Int.MAX_VALUE)
                fetchTasks("Done", Int.MAX_VALUE)
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
        return tasks
    }
}
