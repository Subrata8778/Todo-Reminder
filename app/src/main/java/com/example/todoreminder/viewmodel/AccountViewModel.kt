package com.example.todoreminder.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.navigation.Navigation
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.todoreminder.model.User
import com.example.todoreminder.view.LoginActivity
import org.json.JSONObject

class AccountViewModel(application: Application) : AndroidViewModel(application) {

    val user = MutableLiveData<User>()

    init {
        loadUserFromPreferences()
    }

    private fun loadUserFromPreferences() {
        val sharedPreferences: SharedPreferences = getApplication<Application>().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("username", "") ?: ""
        val name = sharedPreferences.getString("name", "") ?: ""
        user.value = User(username, name)
    }

    fun updateName(newName: String) {
        user.value?.let {
            it.name = newName
            user.value = it
            saveNameToPreferences(newName)

            // Update nama di database menggunakan Volley
            val url = "http://192.168.1.14/JOB/Inovasi/updateUser.php"
            val stringRequest = object : StringRequest(Request.Method.POST, url,
                Response.Listener { response ->
                    Log.e("TAG", "Response: $response")
                    val json = JSONObject(response)
                    val status = json.getString("status")
                    if (status == "success") {
                        Toast.makeText(getApplication(), "Name updated to: ${user.value?.name}", Toast.LENGTH_SHORT).show()
                    } else {
                        val message = json.optString("message", "Unknown error")
                        Log.e("TAG", "Update failed: $message")
                        Toast.makeText(getApplication(), "Failed to update name in database", Toast.LENGTH_SHORT).show()
                    }
                },
                Response.ErrorListener { error ->
                    Log.e("TAG", "Volley error: ${error.message}")
                    Toast.makeText(getApplication(), "Error updating name", Toast.LENGTH_SHORT).show()
                }) {
                override fun getParams(): Map<String, String> {
                    val params = HashMap<String, String>()
                    params["username"] = user.value?.username.toString()
                    params["newName"] = newName
                    Log.e("TAG", "Params: $params")
                    return params
                }
            }

            // Add the request to the RequestQueue.
            val queue = Volley.newRequestQueue(getApplication())
            queue.add(stringRequest)
        }
    }

    private fun saveNameToPreferences(newName: String) {
        val sharedPreferences: SharedPreferences = getApplication<Application>().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("name", newName)
        editor.apply()
    }

    fun logout() {
        // Implementasi logika untuk logout, misalnya menghapus token autentikasi
        clearUserPreferences()

        // Menampilkan pesan logout
        Toast.makeText(getApplication(), "Logged out", Toast.LENGTH_SHORT).show()

        // Navigasi ke halaman login
        val intent = Intent(getApplication(), LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        getApplication<Application>().startActivity(intent)
    }

    private fun clearUserPreferences() {
        val sharedPreferences: SharedPreferences = getApplication<Application>().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor?.putString("username","")
        editor?.commit()
//        editor.clear()
//        editor.commit()
    }
}
