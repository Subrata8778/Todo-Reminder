package com.example.todoreminder.view

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todoreminder.R
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
            showEditStatusDialog(task)
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

    private fun showEditStatusDialog(task: Task) {
        val dialogView = layoutInflater.inflate(R.layout.edit_status, null)
        val spinner = dialogView.findViewById<Spinner>(R.id.spinnerStatus)

        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.status_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        val currentStatusIndex = resources.getStringArray(R.array.status_array).indexOf(task.status)
        if (currentStatusIndex >= 0) {
            spinner.setSelection(currentStatusIndex)
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Edit Task Status")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val newStatus = spinner.selectedItem as String
                viewModel.updateTaskStatus(task.id, newStatus)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
