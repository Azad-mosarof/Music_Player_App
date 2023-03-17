package com.example.musicplayer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlin.system.exitProcess

class NotificationReceiver: BroadcastReceiver(){
    override fun onReceive(context: Context?, intent: Intent?) {
        when(intent?.action){
            ApplicationClass.PREVIOUS -> preNextSong(false,context = context!!)
            ApplicationClass.PLAY -> if(Player_Activity.isPlaying) pauseMusic() else playMusic()
            ApplicationClass.NEXT -> preNextSong(true, context = context!!)
            ApplicationClass.EXIT -> {
                Player_Activity.musicService!!.stopForeground(true)
                Player_Activity.musicService = null
                exitProcess(1)
            }
        }
    }

    private fun playMusic(){
        Player_Activity.isPlaying = true
        Player_Activity.musicService!!.mediaPlayer!!.start()
        Player_Activity.musicService!!.showNotification(R.drawable.pause_icon)
        Player_Activity.binding.playPauseBtn.setIconResource(R.drawable.pause_icon)
    }

    private fun pauseMusic(){
        Player_Activity.isPlaying = false
        Player_Activity.musicService!!.mediaPlayer!!.pause()
        Player_Activity.musicService!!.showNotification(R.drawable.play_icon)
        Player_Activity.binding.playPauseBtn.setIconResource(R.drawable.play_icon)
    }

    private fun preNextSong(increment: Boolean,context: Context){
        setSongPosition(increment = increment)
        Player_Activity.musicService!!.mediaPlayer!!.reset()
        Player_Activity.musicService!!.mediaPlayer!!.setDataSource(Player_Activity.musicListPa[Player_Activity.position].path)
        Player_Activity.musicService!!.mediaPlayer!!.prepare()
        Player_Activity.binding.playPauseBtn.setIconResource(R.drawable.pause_icon)
        Player_Activity.musicService!!.showNotification(R.drawable.pause_icon)
        Player_Activity.binding.seekbarStart.text = formatDuration(Player_Activity.musicService!!.mediaPlayer!!.currentPosition.toLong())
        Player_Activity.binding.seekbarEnd.text = formatDuration(Player_Activity.musicService!!.mediaPlayer!!.duration.toLong())
        Player_Activity.binding.seekBar.progress = 0
        Player_Activity.binding.seekBar.max = Player_Activity.musicService!!.mediaPlayer!!.duration
        Glide.with(context)
            .load(Player_Activity.musicListPa[Player_Activity.position].artUri)
            .apply(RequestOptions().placeholder(R.drawable.music_icon).centerCrop())
            .into(Player_Activity.binding.songImage)
        Player_Activity.binding.songName.text = Player_Activity.musicListPa[Player_Activity.position].title
        playMusic()
    }

}
