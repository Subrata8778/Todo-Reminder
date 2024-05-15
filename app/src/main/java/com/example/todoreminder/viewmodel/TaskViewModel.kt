package com.example.todoreminder.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.todoreminder.model.Task
import org.json.JSONArray
import org.json.JSONObject

class TaskViewModel(application: Application) : AndroidViewModel(application) {

    val todoTasks = MutableLiveData<List<Task>>()
    val inProgressTasks = MutableLiveData<List<Task>>()
    val doneTasks = MutableLiveData<List<Task>>()
    private var queue: RequestQueue = Volley.newRequestQueue(application)

    fun fetchTasks() {
        val url = "http://YOUR_SERVER_IP/get_tasks.php"
        val stringRequest = StringRequest(Request.Method.GET, url,
            Response.Listener { response ->
                val tasks = parseTasks(response)
                todoTasks.value = tasks.filter { it.status == "Todo" }.take(3)
                inProgressTasks.value = tasks.filter { it.status == "InProgress" }.take(3)
                doneTasks.value = tasks.filter { it.status == "Done" }.take(3)
            },
            Response.ErrorListener {
                Toast.makeText(getApplication(), "Failed to fetch tasks", Toast.LENGTH_SHORT).show()
            })
        queue.add(stringRequest)
    }

    private fun parseTasks(response: String): List<Task> {
        val tasks = mutableListOf<Task>()
        val jsonArray = JSONArray(response)
        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val task = Task(
                id = jsonObject.getInt("id"),
                username = jsonObject.getString("username"),
                title = jsonObject.getString("title"),
                description = jsonObject.getString("description"),
                due = jsonObject.getString("due"),
                status = jsonObject.getString("status"),
                file = jsonObject.getString("file")
            )
            tasks.add(task)
        }
        return tasks
    }
}
