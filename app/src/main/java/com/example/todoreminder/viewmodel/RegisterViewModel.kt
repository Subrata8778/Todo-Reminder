package com.example.todoreminder.viewmodel

import android.app.Application
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.todoreminder.model.User
import com.example.todoreminder.view.LoginActivity
import com.example.todoreminder.view.RegisterActivity
import org.json.JSONObject

class RegisterViewModel(application: Application) : AndroidViewModel(application) {

    val user = MutableLiveData<User>()
    private var queue: RequestQueue = Volley.newRequestQueue(application)

    init {
        user.value = User()
    }
    fun onButtonRegister() {
        val url = "http://192.168.1.14/JOB/Inovasi/register.php" // Ganti localhost dengan IP komputer Anda jika perlu
        val stringRequest = object : StringRequest(Request.Method.POST, url,
            Response.Listener { response ->
                Log.e("TAG", "Response: $response")
                val json = JSONObject(response)
                val status = json.getString("status")
                if (status == "success") {
                    val intent = Intent(getApplication(), LoginActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    getApplication<Application>().startActivity(intent)
                } else {
                    val message = json.optString("message", "Unknown error")
                    Log.e("TAG", "Registration failed: $message")
                    Toast.makeText(getApplication(), "Registration failed: $message", Toast.LENGTH_SHORT).show()
                }
            },
            Response.ErrorListener { error ->
                Log.e("TAG", "Volley error: ${error.message}")
                Toast.makeText(getApplication(), "Registration failed: ${error.message}", Toast.LENGTH_SHORT).show()
            }) {
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["username"] = user.value?.username ?: ""
                params["name"] = user.value?.name ?: ""
                params["password"] = user.value?.password ?: ""
                return params
            }
        }
        queue.add(stringRequest)
    }
    fun onButtonLogin() {
        val intent = Intent(getApplication(), LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        getApplication<Application>().startActivity(intent)
    }
}
