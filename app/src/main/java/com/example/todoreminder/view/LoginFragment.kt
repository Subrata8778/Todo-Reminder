package com.example.todoreminder.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.databinding.DataBindingUtil
import com.example.todoreminder.R
import com.example.todoreminder.databinding.FragmentLoginBinding
import com.example.todoreminder.viewmodel.LoginViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

class LoginFragment : Fragment() {

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: android.view.LayoutInflater, container: android.view.ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentLoginBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_login, container, false
        )
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        return binding.root
    }
}
