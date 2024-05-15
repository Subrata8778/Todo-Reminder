package com.example.todoreminder.viewmodel

import android.app.Application
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
import org.json.JSONObject

class SignupViewModel(application: Application) : AndroidViewModel(application) {

    val user = MutableLiveData<User>()
    private var queue: RequestQueue = Volley.newRequestQueue(application)

    init {
        user.value = User()
    }

    fun onButtonRegister() {
        val url = "http://localhost/JOB/KasirPintar/register.php"
        val stringRequest = object : StringRequest(Request.Method.POST, url,
            Response.Listener { response ->
                val json = JSONObject(response)
                val status = json.getString("status")
                if (status == "success") {
                    Toast.makeText(getApplication(), "Registration successful", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(getApplication(), "Registration failed", Toast.LENGTH_SHORT).show()
                }
            },
            Response.ErrorListener {
                Toast.makeText(getApplication(), "Error during registration", Toast.LENGTH_SHORT).show()
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
}
