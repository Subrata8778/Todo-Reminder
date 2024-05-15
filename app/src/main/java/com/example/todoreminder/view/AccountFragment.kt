package com.example.todoreminder.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.todoreminder.databinding.FragmentAccountBinding
import com.example.todoreminder.viewmodel.AccountViewModel

class AccountFragment : Fragment() {
    private lateinit var binding: FragmentAccountBinding
    private lateinit var viewModel: AccountViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment using ViewBinding
        binding = FragmentAccountBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this  // Set the lifecycle owner

        // Initialize ViewModel
        viewModel = ViewModelProvider(this).get(AccountViewModel::class.java)

        // Set ViewModel variable in the layout file
        binding.viewModel = viewModel

        // Set click listener for logout button
        binding.btnLogOut.setOnClickListener {
            viewModel.logout()
        }

        // Set click listener for edit button
        binding.btnEdit.setOnClickListener {
            val newName = binding.txtName.text.toString()
            viewModel.updateName(newName)
        }

        // Observe the user LiveData
        viewModel.user.observe(viewLifecycleOwner, Observer { user ->
            user?.let {
                binding.txtName.setText(it.name)
            }
        })

        return binding.root
    }
}
