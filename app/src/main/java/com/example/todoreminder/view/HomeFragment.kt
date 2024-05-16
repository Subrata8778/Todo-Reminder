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
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todoreminder.R
import com.example.todoreminder.databinding.FragmentHomeBinding
import com.example.todoreminder.model.Task
import com.example.todoreminder.viewmodel.TaskViewModel

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

        // Fetch tasks with different statuses
        viewModel.fetchTasks("Todo", 3)
        viewModel.fetchTasks("InProgress", 3)
        viewModel.fetchTasks("Done", 3)

        return binding.root
    }

    private fun setupRecyclerView() {
        val todoAdapter = TaskAdapter(emptyList()) { task -> navigateToUpdateTask(task) }
        val inProgressAdapter = TaskAdapter(emptyList()) { task -> navigateToUpdateTask(task) }
        val doneAdapter = TaskAdapter(emptyList()) { task -> navigateToUpdateTask(task) }

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
            navigateToAllTasks("Todo")
        }

        binding.viewAllInProgressButton.setOnClickListener {
            navigateToAllTasks("InProgress")
        }

        binding.viewAllDoneButton.setOnClickListener {
            navigateToAllTasks("Done")
        }
    }

    private fun setupFab() {
        binding.fabAddTodo.setOnClickListener {
            // Navigate to CreateTodoFragment
            findNavController().navigate(R.id.actionCreateTodo)
        }
    }

    private fun navigateToAllTasks(status: String) {
        val action = HomeFragmentDirections.actionAllTask(status)
        findNavController().navigate(action)
    }

    private fun navigateToUpdateTask(task: Task) {
        showEditStatusDialog(task);
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
