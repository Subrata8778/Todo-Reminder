package com.example.todoreminder.viewmodel

import android.app.Application
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.android.volley.toolbox.StringRequest
import org.json.JSONObject
import java.io.File

class CreateTodoViewModel(application: Application) : AndroidViewModel(application) {

    val fileUri = MutableLiveData<Uri>()

    fun setFileUri(uri: Uri) {
        fileUri.value = uri
    }

    fun createTask(username: String, title: String, description: String, dueDate: String) {
        val url = "http://192.168.1.14/JOB/Inovasi/createTodo.php"
        val stringRequest = object : StringRequest(Request.Method.POST, url,
            Response.Listener { response ->
                Log.e("TAG", "Response: $response")
                val json = JSONObject(response)
                val status = json.getString("status")
                if (status == "success") {
                    Toast.makeText(getApplication(), "Todo created successfully", Toast.LENGTH_SHORT).show()
                } else {
                    val message = json.optString("message", "Unknown error")
                    Log.e("TAG", "Creation failed: $message")
                    Toast.makeText(getApplication(), "Failed to create todo", Toast.LENGTH_SHORT).show()
                }
            },
            Response.ErrorListener { error ->
                Log.e("TAG", "Volley error: ${error.message}")
                Toast.makeText(getApplication(), "Error creating todo", Toast.LENGTH_SHORT).show()
            }) {
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["username"] = username
                params["title"] = title
                params["description"] = description
                params["due"] = dueDate
                params["status"] = "Todo" // Default status
                // Handle file upload if needed
                fileUri.value?.let {
                    val file = File(it.path)
                    params["file"] = file.name
                }
                Log.e("TAG", "Params: $params")
                return params
            }
        }
        val queue = Volley.newRequestQueue(getApplication())
        queue.add(stringRequest)
    }
}
