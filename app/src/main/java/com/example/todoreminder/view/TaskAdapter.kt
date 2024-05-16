package com.example.todoreminder.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.todoreminder.databinding.ItemTaskBinding
import com.example.todoreminder.model.Task

class TaskAdapter(var tasks: List<Task>, private val onEditClick: (Task) -> Unit) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    class TaskViewHolder(val binding: ItemTaskBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.binding.task = task
        holder.binding.executePendingBindings()

        holder.binding.imgEdit.setOnClickListener {
            onEditClick(task)
        }
    }

    override fun getItemCount(): Int {
        return tasks.size
    }
}
