package com.example.musicplayer

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicplayer.databinding.MusicViewBinding

class MusicAdapter(private val context: Context,private val musicList:ArrayList<Music>): RecyclerView.Adapter<MusicAdapter.myHolder>() {
    class myHolder(binding: MusicViewBinding) : RecyclerView.ViewHolder(binding.root) {
        val title = binding.songName
        val song_album = binding.songAlbum
        val song_image = binding.imageView
        val duration = binding.songDuration
        val root = binding.root
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myHolder {
        return  myHolder(MusicViewBinding.inflate(LayoutInflater.from(context),parent,false))
    }

    override fun onBindViewHolder(holder: myHolder, position: Int) {
        holder.title.text = musicList[position].title
        holder.song_album.text = musicList[position].album
        holder.duration.text = formatDuration(musicList[position].duration)
        Glide.with(context)
            .load(musicList[position].artUri)
            .apply(RequestOptions().placeholder(R.drawable.music_icon).centerCrop())
            .into(holder.song_image)
        holder.root.setOnClickListener{
            val intent = Intent(context,Player_Activity::class.java)
            intent.putExtra("index",position)
            intent.putExtra("class","MusicAdapter")
            ContextCompat.startActivity(context,intent,null)
        }
    }

    override fun getItemCount(): Int {
        return musicList.size
    }
}