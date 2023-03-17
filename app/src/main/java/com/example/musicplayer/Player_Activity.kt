package com.example.musicplayer

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.media.MediaPlayer
import android.os.Bundle
import android.os.IBinder
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicplayer.databinding.ActivityPlayerBinding

class Player_Activity : AppCompatActivity(),ServiceConnection {

    companion object{
        lateinit var musicListPa: ArrayList<Music>
        var position:Int = 0
        var isPlaying:Boolean = false
        var musicService:MusicService ? = null
        @SuppressLint("StaticFieldLeak")
        lateinit var binding: ActivityPlayerBinding
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.coolPink)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //start the background service
        val intent = Intent(this,MusicService::class.java)
        bindService(intent,this, BIND_AUTO_CREATE)
        startService(intent)

        supportActionBar?.hide()
        initializeLayout()

        binding.playPauseBtn.setOnClickListener{
            if(isPlaying) pauseMusic()
            else playMusic()
        }

        binding.back.setOnClickListener{
            finish()
            startActivity(Intent(this@Player_Activity,MainActivity::class.java))
        }

        binding.previousBtn.setOnClickListener{
            prevNexSong(increment = false)
        }

        binding.nextBtn.setOnClickListener{
            prevNexSong(increment = true)
        }

        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if(fromUser) musicService!!.mediaPlayer!!.seekTo(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit

            override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit

        })
    }

    private  fun setLayout(){
        Glide.with(this@Player_Activity)
            .load(musicListPa[position].artUri)
            .apply(RequestOptions().placeholder(R.drawable.music_icon).centerCrop())
            .into(binding.songImage)
        binding.songName.text = musicListPa[position].title
    }

    private fun createMediaPlayer(){
        try{
            if(musicService!!.mediaPlayer == null) musicService!!.mediaPlayer = MediaPlayer()
            musicService!!.mediaPlayer!!.reset()
            musicService!!.mediaPlayer!!.setDataSource(musicListPa[position].path)
            musicService!!.mediaPlayer!!.prepare()
            musicService!!.mediaPlayer!!.start()
            isPlaying = true
            binding.playPauseBtn.setIconResource(R.drawable.pause_icon)
            musicService!!.showNotification(R.drawable.pause_icon)
            binding.seekbarStart.text = formatDuration(musicService!!.mediaPlayer!!.currentPosition.toLong())
            binding.seekbarEnd.text = formatDuration(musicService!!.mediaPlayer!!.duration.toLong())
            binding.seekBar.progress = 0
            binding.seekBar.max = musicService!!.mediaPlayer!!.duration
        }catch (e:Exception) {return}
    }

    private fun initializeLayout(){
        position = intent.getIntExtra("index",0)
        when(intent.getStringExtra("class")){
            "MusicAdapter" -> {
                musicListPa = ArrayList()
                musicListPa.addAll(MainActivity.MusicList)
                setLayout()
            }
            "MainActivity" -> {
                musicListPa = ArrayList()
                musicListPa.addAll(MainActivity.MusicList)
                musicListPa.shuffle()
                setLayout()
            }
        }
    }

    private fun playMusic(){
        binding.playPauseBtn.setIconResource(R.drawable.pause_icon)
        musicService!!.showNotification(R.drawable.pause_icon)
        isPlaying = true
        musicService!!.mediaPlayer!!.start()
    }

    private fun pauseMusic(){
        binding.playPauseBtn.setIconResource(R.drawable.play_icon)
        musicService!!.showNotification(R.drawable.play_icon)
        isPlaying = false
        musicService!!.mediaPlayer!!.pause()
    }

    private fun prevNexSong(increment : Boolean){
        if(increment){
            setSongPosition(increment)
            setLayout()
            createMediaPlayer()
        }
        else{
            setSongPosition(increment)
            setLayout()
            createMediaPlayer()
        }
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        val binder = service as MusicService.MyBinder
        musicService = binder.currentService()
        createMediaPlayer()
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        musicService = null
    }
}