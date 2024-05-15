package com.example.todoreminder.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todoreminder.R
import com.example.todoreminder.databinding.FragmentHomeBinding
import com.example.todoreminder.viewmodel.TaskViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView

class HomeFragment : Fragment() {

    private val viewModel: TaskViewModel by viewModels()
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        setupRecyclerView()
        setupFab()

        viewModel.fetchTasks()

        return binding.root
    }

    private fun setupRecyclerView() {
        val todoAdapter = TaskAdapter(emptyList())
        val inProgressAdapter = TaskAdapter(emptyList())
        val doneAdapter = TaskAdapter(emptyList())

        binding.todoRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.inProgressRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.doneRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        binding.todoRecyclerView.adapter = todoAdapter
        binding.inProgressRecyclerView.adapter = inProgressAdapter
        binding.doneRecyclerView.adapter = doneAdapter

        viewModel.todoTasks.observe(viewLifecycleOwner, Observer { tasks ->
            todoAdapter.tasks = tasks
            todoAdapter.notifyDataSetChanged()
        })

        viewModel.inProgressTasks.observe(viewLifecycleOwner, Observer { tasks ->
            inProgressAdapter.tasks = tasks
            inProgressAdapter.notifyDataSetChanged()
        })

        viewModel.doneTasks.observe(viewLifecycleOwner, Observer { tasks ->
            doneAdapter.tasks = tasks
            doneAdapter.notifyDataSetChanged()
        })

        binding.viewAllTodoButton.setOnClickListener {
            // Navigate to TodoFragment
        }

        binding.viewAllInProgressButton.setOnClickListener {
            // Navigate to InProgressFragment
        }

        binding.viewAllDoneButton.setOnClickListener {
            // Navigate to DoneFragment
        }
    }

    private fun setupFab() {
        binding.fabAddTodo.setOnClickListener {
            // Navigate to CreateTodoFragment
            findNavController().navigate(R.id.actionCreateTodo)
        }
    }
}
