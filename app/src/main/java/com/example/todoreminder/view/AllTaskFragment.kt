package com.example.todoreminder.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todoreminder.databinding.FragmentAllTaskBinding
import com.example.todoreminder.model.Task
import com.example.todoreminder.viewmodel.TaskViewModel

class AllTaskFragment : Fragment() {

    private val viewModel: TaskViewModel by viewModels()
    private lateinit var binding: FragmentAllTaskBinding
    private lateinit var status: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAllTaskBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        status = arguments?.getString("status") ?: "Todo" // Default to "Todo" if no status passed

        val adapter = TaskAdapter(emptyList()) { task ->
            // Handle edit click here
            // Example: navigate to another fragment or show a dialog
            // You can replace the following line with your desired action
            println("Edit task: ${task.title}")
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        viewModel.fetchTasks(status, Int.MAX_VALUE) // Fetch all tasks for the given status
        viewModel.getTasksForStatus(status).observe(viewLifecycleOwner, { tasks ->
            adapter.tasks = tasks
            adapter.notifyDataSetChanged()
        })

        return binding.root
    }
}
