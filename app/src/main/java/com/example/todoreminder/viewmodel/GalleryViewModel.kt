package com.example.todoreminder.viewmodel

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.todoreminder.model.Gallery
import org.json.JSONArray

class GalleryViewModel(application: Application) : AndroidViewModel(application) {

    val galleryItems = MutableLiveData<List<Gallery>>()
    val errorMessage = MutableLiveData<String>()

    fun fetchGalleryItems() {
        val url = "http://192.168.1.14/JOB/Inovasi/getGallery.php"
        val stringRequest = StringRequest(Request.Method.GET, url,
            Response.Listener { response ->
                try {
                    val jsonArray = JSONArray(response)
                    val items = mutableListOf<Gallery>()

                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)
                        val id = jsonObject.getInt("id")
                        val type = jsonObject.getString("type")
                        val url = jsonObject.getString("url")
                        items.add(Gallery(id, type, url))
                    }

                    galleryItems.postValue(items)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                Log.e("GalleryViewModel", "Volley error: ${error.message}")
                errorMessage.postValue("Error fetching gallery items")
            })

        val queue = Volley.newRequestQueue(getApplication())
        queue.add(stringRequest)
    }
}
