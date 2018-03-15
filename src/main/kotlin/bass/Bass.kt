package studio.rndr.demo.bass

import jouvieje.bass.Bass
import jouvieje.bass.BassInit
import jouvieje.bass.defines.BASS_POS
import jouvieje.bass.defines.BASS_SAMPLE

class Channel(val channel:Int) {
    fun setPosition(seconds:Double) {
        val offset = Bass.BASS_ChannelSeconds2Bytes(channel, seconds)
        Bass.BASS_ChannelSetPosition(channel, offset, BASS_POS.BASS_POS_BYTE)
    }

    fun getPosition():Double {
        val currentOffset = Bass.BASS_ChannelGetPosition(channel, BASS_POS.BASS_POS_BYTE)
        return Bass.BASS_ChannelBytes2Seconds(channel, currentOffset)
    }

    fun play() {
        Bass.BASS_ChannelPlay(channel, false)
    }
}

fun initBass() {
    BassInit.loadLibraries()
    Bass.BASS_Init(-1, 44100, 0, null, null)
}

fun openStream(path:String):Channel {
    val stream = Bass.BASS_StreamCreateFile(false, path, 0, 0, BASS_SAMPLE.BASS_SAMPLE_LOOP)
    return Channel(stream.asInt())

}