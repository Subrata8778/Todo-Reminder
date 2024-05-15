package com.example.todoreminder.viewmodel

import android.app.Application
import android.content.Intent
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.todoreminder.model.User
import com.example.todoreminder.view.LoginFragment

class AccountViewModel(application: Application) : AndroidViewModel(application) {

    val user = MutableLiveData<User>()

    init {
        // Misalnya user data diisi dari SharedPreferences atau database lokal
        user.value = User("Current User Name")
    }

    fun updateName() {
        // Implementasi logika untuk memperbarui nama user
        Toast.makeText(getApplication(), "Name updated to: ${user.value?.name}", Toast.LENGTH_SHORT).show()
    }

    fun logout() {
        // Implementasi logika untuk logout, misalnya menghapus token autentikasi
        val intent = Intent(getApplication(), LoginFragment::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        getApplication<Application>().startActivity(intent)
    }
}
