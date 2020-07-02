package com.zjx.ffmpegdemo

import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.Observer
import com.zjx.appcommonlibrary.base.viewmodel.BaseVmFragment
import kotlinx.android.synthetic.main.fragment_media_info.*

class MediaInfoFragment : BaseVmFragment<FFmpegViewModel>() {
    override fun layoutId(): Int = R.layout.fragment_media_info
    private lateinit var mTvMediaContent: TextView


    override fun initView(view: View) {
        mTvMediaContent = view.findViewById<TextView>(R.id.tv_media_content)
        view.findViewById<Button>(R.id.but_video_duration).setOnClickListener(onViewOnClickListener)
        view.findViewById<Button>(R.id.but_video_width).setOnClickListener(onViewOnClickListener)
        view.findViewById<Button>(R.id.but_video_height).setOnClickListener(onViewOnClickListener)
        view.findViewById<Button>(R.id.but_video_bit_rate).setOnClickListener(onViewOnClickListener)
        view.findViewById<Button>(R.id.but_video_frame_rate)
            .setOnClickListener(onViewOnClickListener)
        view.findViewById<Button>(R.id.but_audio_channels).setOnClickListener(onViewOnClickListener)
        view.findViewById<Button>(R.id.but_audio_sampling_rate)
            .setOnClickListener(onViewOnClickListener)
        view.findViewById<Button>(R.id.but_audio_bit_rate).setOnClickListener(onViewOnClickListener)
    }

    override fun lazyLoadData() {
        mViewModel.setFilePath()
    }

    override fun createObserver() {
        mViewModel.mMediaContent.observe(this, Observer { mMediaContent ->
            mTvMediaContent.text = getString(R.string.media_content, mMediaContent)
        })
    }

    private val onViewOnClickListener: View.OnClickListener by lazy {
        View.OnClickListener { view ->
            when (view?.id) {
                //视频时长
                R.id.but_video_duration -> {
                    mViewModel.getVideoDuration()
                }
                //视频宽度
                R.id.but_video_width -> {
                    mViewModel.getVideoWidth()
                }
                //视频高度
                R.id.but_video_height -> {
                    mViewModel.getVideoHeight()
                }
                //视频比特率
                R.id.but_video_bit_rate -> {
                    mViewModel.getVideoBitRate()
                }
                //视频帧率
                R.id.but_video_frame_rate -> {
                    mViewModel.getVideoFPS()
                }
                //音频声道数
                R.id.but_audio_channels -> {
                    mViewModel.getAudioChannels()
                }
                //音频采样率
                R.id.but_audio_sampling_rate -> {
                    mViewModel.getAudioSampleRate()
                }
                //音频比特率
                R.id.but_audio_bit_rate -> {
                    mViewModel.getAudioBitRate()
                }
            }
        }
    }
}