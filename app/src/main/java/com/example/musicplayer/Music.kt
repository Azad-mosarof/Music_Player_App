package com.example.musicplayer

import android.media.MediaMetadataRetriever
import java.util.concurrent.TimeUnit

data class Music (val id:String,val title:String,val album:String,val duration:Long = 0,val path:String,val artist:String,
    val artUri: String)

fun formatDuration(duration: Long) : String{
    val minutes = TimeUnit.MINUTES.convert(duration,TimeUnit.MILLISECONDS)
    val seconds = TimeUnit.SECONDS.convert(duration,TimeUnit.MILLISECONDS) -
            minutes*TimeUnit.SECONDS.convert(1,TimeUnit.MINUTES)
    return  String.format("%2d:%2d",minutes,seconds)
}

fun getImageArt(path: String): ByteArray? {
    val retriever = MediaMetadataRetriever()
    retriever.setDataSource(path)
    return retriever.embeddedPicture
}

fun setSongPosition(increment: Boolean){
    if(increment){
        if(Player_Activity.musicListPa.size - 1 == Player_Activity.position){
            Player_Activity.position = 0
        }
        else ++Player_Activity.position
    }
    else{
        if(Player_Activity.position == 0){
            Player_Activity.position = Player_Activity.musicListPa.size - 1
        }
        else --Player_Activity.position
    }
}