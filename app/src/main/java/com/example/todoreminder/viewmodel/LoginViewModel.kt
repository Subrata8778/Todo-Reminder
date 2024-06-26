package com.example.todoreminder.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
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
import com.example.todoreminder.view.MainActivity
import com.example.todoreminder.view.RegisterActivity
import org.json.JSONObject

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    val user = MutableLiveData<User>()
    private var queue: RequestQueue = Volley.newRequestQueue(application)

    init {
        user.value = User()
    }

    fun onButtonLogin() {
        val url = "http://192.168.1.14/JOB/Inovasi/login.php"
        val stringRequest = object : StringRequest(Request.Method.POST, url,
            Response.Listener { response ->
                Log.e("TAG", "Response: $response")
                val json = JSONObject(response)
                val status = json.getString("status")
                if (status == "success") {
                    val userData = json.getJSONObject("user")
                    val username = userData.getString("username")
                    val name = userData.getString("name")

                    // Simpan data pengguna ke SharedPreferences
                    saveUserToPreferences(username, name)

                    val intent = Intent(getApplication(), MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    getApplication<Application>().startActivity(intent)
                } else {
                    val message = json.optString("message", "Unknown error")
                    Log.e("TAG", "Login failed: $message")
                    Toast.makeText(getApplication(), "Invalid login credentials", Toast.LENGTH_SHORT).show()
                }
            },
            Response.ErrorListener { error ->
                Log.e("TAG", "Volley error: ${error.message}")
                Toast.makeText(getApplication(), "Login failed", Toast.LENGTH_SHORT).show()
            }) {
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["username"] = user.value?.username ?: ""
                params["password"] = user.value?.password ?: ""
                Log.e("TAG", "Params: $params")
                return params
            }
        }
        queue.add(stringRequest)
    }

    private fun saveUserToPreferences(username: String, name: String) {
        val sharedPreferences: SharedPreferences = getApplication<Application>().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("username", username)
        editor.putString("name", name)
        editor.apply()
    }

    fun onButtonRegister() {
        val intent = Intent(getApplication(), RegisterActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        getApplication<Application>().startActivity(intent)
    }
}
