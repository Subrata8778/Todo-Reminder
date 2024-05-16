package com.example.todoreminder.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.todoreminder.adapter.TaskPagerAdapter
import com.example.todoreminder.databinding.FragmentTaskBinding
import com.example.todoreminder.model.Task
import com.example.todoreminder.viewmodel.TaskViewModel
import android.app.AlertDialog
import com.example.todoreminder.R

class TaskFragment : Fragment() {

    private val viewModel: TaskViewModel by viewModels()
    private lateinit var binding: FragmentTaskBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTaskBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        val adapter = TaskPagerAdapter(emptyList()) { task ->
            showEditStatusDialog(task)
        }
        binding.viewPager.adapter = adapter

        viewModel.tasksByDate.observe(viewLifecycleOwner, Observer { tasksByDate ->
            (binding.viewPager.adapter as TaskPagerAdapter).let {
                it.tasksByDate = tasksByDate
                it.notifyDataSetChanged()
            }
        })

        viewModel.fetchTasks("Todo", 3)
        viewModel.fetchTasks("InProgress", 3)
        viewModel.fetchTasks("Done", 3)
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
