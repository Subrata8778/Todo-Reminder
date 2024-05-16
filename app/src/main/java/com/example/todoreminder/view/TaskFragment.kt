package com.example.todoreminder.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.todoreminder.adapter.TaskPagerAdapter
import com.example.todoreminder.databinding.FragmentTaskBinding
import com.example.todoreminder.viewmodel.TaskViewModel

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

        val adapter = TaskPagerAdapter(emptyList(), viewModel)
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
}
