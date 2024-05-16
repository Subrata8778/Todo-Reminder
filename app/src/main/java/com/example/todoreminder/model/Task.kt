package com.example.todoreminder.model

import android.os.Parcelable
import androidx.databinding.ObservableField
import kotlinx.parcelize.Parcelize

@Parcelize
data class Task(
    var id: Int = 0,
    var username: String = "",
    var title: ObservableField<String> = ObservableField(""),
    var description: ObservableField<String> = ObservableField(""),
    var due: ObservableField<String> = ObservableField(""),
    var status: String = "",
    var file: String = ""
) : Parcelable
