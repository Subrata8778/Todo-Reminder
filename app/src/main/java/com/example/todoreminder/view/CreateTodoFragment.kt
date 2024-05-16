package com.example.todoreminder.view

import android.app.Activity.RESULT_OK
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.todoreminder.databinding.FragmentCreateTodoBinding
import com.example.todoreminder.viewmodel.CreateTodoViewModel
import java.text.SimpleDateFormat
import java.util.*

class CreateTodoFragment : Fragment(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private val viewModel: CreateTodoViewModel by viewModels()
    private lateinit var binding: FragmentCreateTodoBinding
    private var calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCreateTodoBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        binding.btnAdd.setOnClickListener {
            val sharedPreferences = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            val username = sharedPreferences.getString("username", "") ?: ""
            val title = binding.txtTitle.text.toString()
            val description = binding.txtDesc.text.toString()
            val dueDate = binding.txtDate.text.toString() + " " + binding.txtTime.text.toString()
            viewModel.createTask(username, title, description, dueDate)
        }

        binding.txtDate.setOnClickListener {
            showDatePicker()
        }

        binding.txtTime.setOnClickListener {
            showTimePicker()
        }

        binding.btnFile.setOnClickListener {
            chooseFile()
        }

        return binding.root
    }

    private fun showDatePicker() {
        val datePickerDialog = DatePickerDialog(requireContext(), this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
        datePickerDialog.show()
    }

    private fun showTimePicker() {
        val timePickerDialog = TimePickerDialog(requireContext(), this, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true)
        timePickerDialog.show()
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        updateDateLabel()
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
        calendar.set(Calendar.MINUTE, minute)
        updateTimeLabel()
    }

    private fun updateDateLabel() {
        val dateFormat = "yyyy-MM-dd"
        val sdf = SimpleDateFormat(dateFormat, Locale.getDefault())
        binding.txtDate.setText(sdf.format(calendar.time))
    }

    private fun updateTimeLabel() {
        val timeFormat = "HH:mm"
        val sdf = SimpleDateFormat(timeFormat, Locale.getDefault())
        binding.txtTime.setText(sdf.format(calendar.time))
    }

    private fun chooseFile() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "*/*"
        startActivityForResult(intent, REQUEST_CODE_FILE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_FILE && resultCode == RESULT_OK) {
            val uri: Uri? = data?.data
            uri?.let {
                binding.txtFilename.text = it.path
                viewModel.setFileUri(it)
            }
        }
    }

    companion object {
        const val REQUEST_CODE_FILE = 1
    }
}
