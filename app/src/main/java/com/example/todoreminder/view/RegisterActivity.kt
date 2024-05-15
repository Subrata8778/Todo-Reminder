package com.example.todoreminder.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.todoreminder.R
import com.example.todoreminder.databinding.ActivityRegisterBinding
import com.example.todoreminder.viewmodel.RegisterViewModel

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val binding: ActivityRegisterBinding = DataBindingUtil.setContentView(this, R.layout.activity_register)
        val viewModel = ViewModelProvider(this).get(RegisterViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
    }
}
