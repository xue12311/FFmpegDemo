package com.zjx.ffmpegdemo

import android.os.Build
import androidx.lifecycle.MediatorLiveData
import com.coder.ffmpeg.annotation.Attribute
import com.coder.ffmpeg.jni.FFmpegCommand
import com.zjx.appcommonlibrary.base.BaseViewModel
import com.zjx.appcommonlibrary.utils.utilcode.util.StringUtils
import com.zjx.appcommonlibrary.utils.utilcode.util.TimeUtils
import com.zjx.ffmpegdemo.utils.FileUtils
import kotlinx.coroutines.async
import java.lang.StringBuilder

class FFmpegViewModel : BaseViewModel() {
    //视频路径
    val mVideoPath = MediatorLiveData<String>()

    //音频文件
    val mAudioPath = MediatorLiveData<String>()

    //音频背景文件
    val mAudioBgPath = MediatorLiveData<String>()

    //图片文件
    val mImagePath = MediatorLiveData<String>()

    //视频路径(从视频文件中抽取的视频文件)
    val mVideoPathExtract = MediatorLiveData<String>()

    //音频文件(从视频文件中抽取的音频文件)
    val mAudioPathExtract = MediatorLiveData<String>()

    //图片文件夹（视频文件转图片，图片文件夹保存路径）
    val mVideoToImagePath = MediatorLiveData<String>()

    //音频解码后的文件
    val mDecodeAudioPath = MediatorLiveData<String>()

    //视频解码为YUV
    val mVideoYUVPath = MediatorLiveData<String>()

    //视频切换 HLS
    val mVideoHLSPath = MediatorLiveData<String>()

    //显示内容
    val mMediaContent = MediatorLiveData<String>()

    //音频文件转FDK_aac
    val mAudioAACPath = MediatorLiveData<String>()

    val mVideoContent = StringBuilder()

    fun cleanStringBuiler() {
        mVideoContent.delete(0, mVideoContent.length)
    }

    fun getSupportsABIs(): String = if (Build.VERSION.SDK_INT >= 21) {
        Build.SUPPORTED_ABIS.asList().toString()
    } else {
        Build.CPU_ABI
    }

    fun setFilePath() {
        launchUI {
            //视频
            mVideoPath.value = async {
                FileUtils.getFileFromAssets("video", "test.mp4")
            }.await()
            //音频
            mAudioPath.value = async {
                FileUtils.getFileFromAssets("audio", "test.mp3")
            }.await()
            //音频-背景
            mAudioBgPath.value = async {
                FileUtils.getFileFromAssets("audio", "testbg.mp3")
            }.await()
            //图片
            mImagePath.value = async {
                FileUtils.getFileFromAssets("image", "water.png")
            }.await()

        }
    }


    //获取视频时长
    fun getVideoDuration() {
        if (StringUtils.isNoEmpty(mVideoPath.value)) {
            //时长(毫秒)
            val duration = FFmpegCommand.getInfoSync(mVideoPath.value, Attribute.DURATION) / 1000
            val time = TimeUtils.getTimeDurationString(duration, true)
            mMediaContent.value = "视频时长: ${time}"
        }
    }

    //视频宽度
    fun getVideoWidth() {
        if (StringUtils.isNoEmpty(mVideoPath.value)) {
            //视频宽度
            val width = FFmpegCommand.getInfoSync(mVideoPath.value, Attribute.WIDTH)
            mMediaContent.value = "视频宽度: ${width}"
        }
    }

    //视频高度
    fun getVideoHeight() {
        if (StringUtils.isNoEmpty(mVideoPath.value)) {
            val height = FFmpegCommand.getInfoSync(mVideoPath.value, Attribute.HEIGHT)
            mMediaContent.value = "视频高度: ${height}"
        }
    }

    //视频比特率
    fun getVideoBitRate() {
        if (StringUtils.isNoEmpty(mVideoPath.value)) {
            val bitRate = FFmpegCommand.getInfoSync(mVideoPath.value, Attribute.VIDEO_BIT_RATE)
            mMediaContent.value = "视频比特率: ${bitRate}"
        }
    }

    //视频帧率
    fun getVideoFPS() {
        if (StringUtils.isNoEmpty(mVideoPath.value)) {
            val fps = FFmpegCommand.getInfoSync(mVideoPath.value, Attribute.FPS)
            mMediaContent.value = "视频帧率: ${fps}"
        }
    }

    //音频声道数
    fun getAudioChannels() {
        if (StringUtils.isNoEmpty(mVideoPath.value)) {
            val channels = FFmpegCommand.getInfoSync(mVideoPath.value, Attribute.CHANNELS)
            mMediaContent.value = "音频声道数: ${channels}"
        }
    }

    //音频采样率
    fun getAudioSampleRate() {
        if (StringUtils.isNoEmpty(mVideoPath.value)) {
            val sampleRate = FFmpegCommand.getInfoSync(mVideoPath.value, Attribute.SAMPLE_RATE)
            mMediaContent.value = "音频采样率: ${sampleRate}"
        }
    }

    //音频比特率
    fun getAudioBitRate() {
        if (StringUtils.isNoEmpty(mVideoPath.value)) {
            val bitRate = FFmpegCommand.getInfoSync(mVideoPath.value, Attribute.AUDIO_BIT_RATE)
            mMediaContent.value = "音频比特率: ${bitRate}"
        }
    }

}