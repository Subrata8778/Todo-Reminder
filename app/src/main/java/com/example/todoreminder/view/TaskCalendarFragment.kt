package com.example.todoreminder.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todoreminder.databinding.FragmentTaskBinding
import com.example.todoreminder.viewmodel.TaskCalendarViewModel
import com.example.todoreminder.viewmodel.TaskViewModel
import java.text.SimpleDateFormat
import java.util.*

class TaskCalendarFragment : Fragment() {

    private lateinit var taskViewModel: TaskViewModel
    private lateinit var taskCalendarViewModel: TaskCalendarViewModel
    private var _binding: FragmentTaskBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        taskViewModel = ViewModelProvider(requireActivity()).get(TaskViewModel::class.java)
        taskCalendarViewModel = ViewModelProvider(this).get(TaskCalendarViewModel::class.java)

        binding.recyclerViewTasks.layoutManager = LinearLayoutManager(requireContext())

        val taskAdapter = TaskAdapter(emptyList()) { task ->
            // Handle edit click
        }
        binding.recyclerViewTasks.adapter = taskAdapter

        binding.calendarView.setOnDateChangedListener { widget, date, selected ->
            val selectedDate = "${date.year}-${date.month + 1}-${date.day}"
            val formattedDate = formatDateString(selectedDate)
            taskCalendarViewModel.setSelectedDate(formattedDate)
        }

        taskCalendarViewModel.tasks.observe(viewLifecycleOwner, Observer { tasks ->
            taskAdapter.updateTasks(tasks)
        })

        taskCalendarViewModel.selectedDate.observe(viewLifecycleOwner, Observer { date ->
            taskViewModel.fetchTasksForDate(date)
        })

        taskViewModel.tasksForDate.observe(viewLifecycleOwner, Observer { tasks ->
            taskCalendarViewModel.setTasks(tasks)
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun formatDateString(date: String): String {
        val inputFormat = SimpleDateFormat("yyyy-M-d", Locale.getDefault())
        val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val parsedDate = inputFormat.parse(date)
        return outputFormat.format(parsedDate)
    }
}
