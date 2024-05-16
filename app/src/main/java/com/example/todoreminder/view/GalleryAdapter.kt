package com.example.todoreminder.adapter

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.todoreminder.R
import com.example.todoreminder.model.Gallery
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.google.android.exoplayer2.ui.PlayerView
import com.squareup.picasso.Picasso

class GalleryAdapter(private val items: List<Gallery>, private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_IMAGE = 0
        private const val TYPE_VIDEO = 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (items[position].type == "Image") TYPE_IMAGE else TYPE_VIDEO
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_IMAGE) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
            ImageViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_video, parent, false)
            VideoViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ImageViewHolder) {
            holder.bind(items[position])
        } else if (holder is VideoViewHolder) {
            holder.bind(items[position])
        }
    }

    override fun getItemCount(): Int = items.size

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.imageView)

        fun bind(item: Gallery) {
            Picasso.get().load(item.url).into(imageView)
            itemView.setOnClickListener {
                showImagePopup(item.url)
            }
        }

        private fun showImagePopup(imageUrl: String) {
            val builder = AlertDialog.Builder(context)
            val inflater = LayoutInflater.from(context)
            val view = inflater.inflate(R.layout.popup_image, null)
            val imageView: ImageView = view.findViewById(R.id.popupImageView)
            val closeButton: ImageButton = view.findViewById(R.id.closeButton)

            Picasso.get().load(imageUrl).into(imageView)

            builder.setView(view)
            val dialog = builder.create()

            closeButton.setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()
        }
    }

    inner class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val playerView: PlayerView = itemView.findViewById(R.id.playerView)
        private val exoPlayer: SimpleExoPlayer = SimpleExoPlayer.Builder(itemView.context).build()

        fun bind(item: Gallery) {
            playerView.player = exoPlayer
            val mediaItem = MediaItem.fromUri(item.url)

            val dataSourceFactory = DefaultDataSourceFactory(
                itemView.context,
                Util.getUserAgent(itemView.context, "TodoReminder")
            )

            val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(mediaItem)

            exoPlayer.setMediaSource(mediaSource)
            exoPlayer.prepare()
            exoPlayer.playWhenReady = false

            itemView.setOnClickListener {
                showVideoPopup(item.url)
            }

//            exoPlayer.addListener(object : Player.Listener {
//                override fun onPlayerError(error: ExoPlaybackException) {
//                    Toast.makeText(itemView.context, "Error playing video: ${error.message}", Toast.LENGTH_SHORT).show()
//                }
//            })
        }

        private fun showVideoPopup(videoUrl: String) {
            val builder = AlertDialog.Builder(context)
            val inflater = LayoutInflater.from(context)
            val view = inflater.inflate(R.layout.popup_video, null)
            val playerView: PlayerView = view.findViewById(R.id.popupPlayerView)
            val closeButton: ImageButton = view.findViewById(R.id.closeButton)

            val exoPlayer = SimpleExoPlayer.Builder(context).build()
            playerView.player = exoPlayer
            val mediaItem = MediaItem.fromUri(videoUrl)

            val dataSourceFactory = DefaultDataSourceFactory(
                context,
                Util.getUserAgent(context, "TodoReminder")
            )

            val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(mediaItem)

            exoPlayer.setMediaSource(mediaSource)
            exoPlayer.prepare()
            exoPlayer.playWhenReady = true

            builder.setView(view)
            val dialog = builder.create()

            closeButton.setOnClickListener {
                exoPlayer.release()
                dialog.dismiss()
            }

            dialog.setOnDismissListener {
                exoPlayer.release()
            }

            dialog.show()
        }

        fun releasePlayer() {
            exoPlayer.release()
        }
    }
}
