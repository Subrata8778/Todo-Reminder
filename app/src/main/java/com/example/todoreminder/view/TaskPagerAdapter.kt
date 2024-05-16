package com.example.todoreminder.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todoreminder.databinding.ItemTaskPageBinding
import com.example.todoreminder.model.Task
import com.example.todoreminder.view.TaskAdapter

class TaskPagerAdapter(var tasksByDate: List<List<Task>>, private val onEditClick: (Task) -> Unit) :
    RecyclerView.Adapter<TaskPagerAdapter.TaskViewHolder>() {

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
            val taskAdapter = TaskAdapter(tasks, onEditClick)
            binding.recyclerView.layoutManager = LinearLayoutManager(binding.root.context)
            binding.recyclerView.adapter = taskAdapter
        }
    }
}
