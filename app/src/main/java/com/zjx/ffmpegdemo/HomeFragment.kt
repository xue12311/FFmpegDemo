package com.zjx.ffmpegdemo

import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.navigation.Navigation
import com.zjx.appcommonlibrary.base.viewmodel.BaseVmFragment
import com.zjx.ffmpegdemo.utils.BadgeUtils

class HomeFragment : BaseVmFragment<FFmpegViewModel>() {
    private var tvABI: TextView? = null
    override fun layoutId(): Int = R.layout.fragment_home

    override fun initView(view: View) {
        tvABI = view.findViewById(R.id.tv_abi)
        view.findViewById<Button>(R.id.button_media_info)
            .setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_homeFragment_to_mediaInfoFragment))
        view.findViewById<Button>(R.id.button_ffmpeg_command)
            .setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_homeFragment_to_FFmpegCommandFragment))
        view.findViewById<Button>(R.id.button_ffmpeg_more_command)
            .setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_homeFragment_to_FFmpegCommandMoreFragment))
//        sbuttonMediaInformation?.setOnClickListener { view ->
//            Navigation.findNavController(view).navigate(R.id.action_homeFragment_to_mediaInfoFragment)
//        }
        view.findViewById<Button>(R.id.button_showBadge)?.setOnClickListener { v ->
            BadgeUtils.setCount(10, activity)
        }
    }

    override fun lazyLoadData() {
        tvABI?.text = getString(R.string.currently_abi, mViewModel.getSupportsABIs())
    }

    override fun createObserver() {
    }
}