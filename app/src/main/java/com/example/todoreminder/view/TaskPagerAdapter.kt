package com.example.todoreminder.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todoreminder.databinding.ItemTaskPageBinding
import com.example.todoreminder.model.Task
import com.example.todoreminder.view.TaskAdapter
import com.example.todoreminder.viewmodel.TaskViewModel
import android.app.AlertDialog
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.example.todoreminder.R

class TaskPagerAdapter(
    var tasksByDate: List<List<Task>>,
    private val viewModel: TaskViewModel
) : RecyclerView.Adapter<TaskPagerAdapter.TaskViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemTaskPageBinding.inflate(inflater, parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val tasks = tasksByDate[position]
        holder.bind(tasks)
    }

    override fun getItemCount(): Int = tasksByDate.size

    inner class TaskViewHolder(private val binding: ItemTaskPageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(tasks: List<Task>) {
            val taskAdapter = TaskAdapter(tasks) { task ->
                showEditStatusDialog(task)
            }
            binding.recyclerView.layoutManager = LinearLayoutManager(binding.root.context)
            binding.recyclerView.adapter = taskAdapter
        }

        private fun showEditStatusDialog(task: Task) {
            val dialogView = LayoutInflater.from(binding.root.context).inflate(R.layout.edit_status, null)
            val spinner = dialogView.findViewById<Spinner>(R.id.spinnerStatus)

            ArrayAdapter.createFromResource(
                binding.root.context,
                R.array.status_array,
                android.R.layout.simple_spinner_item
            ).also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner.adapter = adapter
            }

            val currentStatusIndex = binding.root.context.resources.getStringArray(R.array.status_array).indexOf(task.status)
            if (currentStatusIndex >= 0) {
                spinner.setSelection(currentStatusIndex)
            }

            AlertDialog.Builder(binding.root.context)
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
}
