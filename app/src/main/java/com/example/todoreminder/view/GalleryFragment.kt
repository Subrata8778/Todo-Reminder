package com.example.todoreminder.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todoreminder.R
import com.example.todoreminder.adapter.GalleryAdapter
import com.example.todoreminder.viewmodel.GalleryViewModel

class GalleryFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var galleryAdapter: GalleryAdapter
    private lateinit var galleryViewModel: GalleryViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_gallery, container, false)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        Log.d("GalleryFragment", "onCreateView: Initializing GalleryViewModel")

        galleryViewModel = ViewModelProvider(this).get(GalleryViewModel::class.java)
        galleryViewModel.galleryItems.observe(viewLifecycleOwner, { galleryItems ->
            if (galleryItems != null) {
                Log.d("GalleryFragment", "onCreateView: Gallery items received")
                galleryAdapter = GalleryAdapter(galleryItems, requireContext())
                recyclerView.adapter = galleryAdapter
            } else {
                Log.d("GalleryFragment", "onCreateView: No gallery items found")
                Toast.makeText(context, "No gallery items found", Toast.LENGTH_SHORT).show()
            }
        })

        galleryViewModel.errorMessage.observe(viewLifecycleOwner, { message ->
            Log.e("GalleryFragment", "onCreateView: $message")
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        })

        galleryViewModel.fetchGalleryItems()

        return view
    }

    override fun onStop() {
        super.onStop()
        recyclerView.adapter?.let {
            for (i in 0 until it.itemCount) {
                val viewHolder = recyclerView.findViewHolderForAdapterPosition(i)
                if (viewHolder is GalleryAdapter.VideoViewHolder) {
                    viewHolder.releasePlayer()
                }
            }
        }
    }
}
