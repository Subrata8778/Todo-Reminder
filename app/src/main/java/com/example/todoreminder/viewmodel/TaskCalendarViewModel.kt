package com.example.todoreminder.viewmodel

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.todoreminder.model.Task

class TaskCalendarViewModel : ViewModel() {

    private val _selectedDate = MutableLiveData<String>()
    val selectedDate: LiveData<String> get() = _selectedDate

    private val _tasks = MutableLiveData<List<Task>>()
    val tasks: LiveData<List<Task>> get() = _tasks

    fun setSelectedDate(date: String) {
        _selectedDate.value = date
    }

    fun setTasks(tasks: List<Task>) {
        _tasks.value = tasks
    }
}
