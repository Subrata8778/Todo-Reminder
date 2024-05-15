package com.example.todoreminder.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.databinding.DataBindingUtil
import com.example.todoreminder.R
import com.example.todoreminder.databinding.FragmentSignupBinding
import com.example.todoreminder.viewmodel.SignupViewModel

class SignupFragment : Fragment() {

    private val viewModel: SignupViewModel by viewModels()

    override fun onCreateView(
        inflater: android.view.LayoutInflater, container: android.view.ViewGroup?,
        savedInstanceState: Bundle?
    ): android.view.View? {
        val binding: FragmentSignupBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_signup, container, false
        )
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        return binding.root
    }
}
