package com.example.todoreminder.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.todoreminder.R
import com.example.todoreminder.databinding.FragmentUpdateTaskBinding
import com.example.todoreminder.model.Task
import com.example.todoreminder.viewmodel.TaskViewModel

class UpdateTaskFragment : Fragment() {

    private val viewModel: TaskViewModel by viewModels()
    private lateinit var binding: FragmentUpdateTaskBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUpdateTaskBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        val task = arguments?.getParcelable<Task>("task") ?: Task() // Dapatkan task yang dipilih dari argument
        viewModel.currentTask.value = task
        binding.task = task

        val spinner = binding.spinnerStatus
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

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val newStatus = parent.getItemAtPosition(position) as String
                task.status = newStatus
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }

        binding.btnSave.setOnClickListener {
            viewModel.currentTask.value?.let {
                it.title.set(binding.txtTitle.text.toString())
                it.description.set(binding.txtDesc.text.toString())
                it.status = spinner.selectedItem as String
                viewModel.updateTaskStatus(it.id, it.status)
            }
        }

        return binding.root
    }
}
