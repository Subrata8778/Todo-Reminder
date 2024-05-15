package com.example.todoreminder.view

import android.view.View
import com.example.todoreminder.model.Task

interface FragmentCreateTodoLayoutInterface{
    fun onRadioClick(v: View, priority: Int, obj: Task)
    fun onButtonAddTodo(v: View)
    fun onDateClick(v: View)
}