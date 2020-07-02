package com.zjx.ffmpegdemo

import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.coder.ffmpeg.call.CommonCallBack
import com.coder.ffmpeg.jni.FFmpegCommand
import com.coder.ffmpeg.utils.FFmpegUtils
import com.zjx.appcommonlibrary.base.viewmodel.BaseVmFragment
import com.zjx.appcommonlibrary.utils.utilcode.util.LogUtils
import com.zjx.appcommonlibrary.utils.utilcode.util.StringUtils
import com.zjx.ffmpegdemo.utils.FileUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FFmpegCommandMoreFragment : BaseVmFragment<FFmpegViewModel>() {
    private val targetAAC = FileUtils.getExternalFilePath("target.aac")
    private val targetAVI = FileUtils.getExternalFilePath("target.avi")
    private val targetJPEG = FileUtils.getExternalFilePath("target.jpeg")

    private lateinit var mTvFfmpegMoreContent: TextView


    override fun layoutId(): Int = R.layout.fragment_ffmpeg_more_command

    override fun initView(view: View) {
        view.findViewById<Button>(R.id.but_command_sync_separate)
            .setOnClickListener(onViewOnClickListener)
        view.findViewById<Button>(R.id.but_command_sync_merge)
            .setOnClickListener(onViewOnClickListener)
        view.findViewById<Button>(R.id.but_command_async_merge)
            .setOnClickListener(onViewOnClickListener)
        mTvFfmpegMoreContent = view.findViewById<TextView>(R.id.tv_ffmpeg_more_content)
    }

    override fun lazyLoadData() {
        mViewModel.setFilePath()
    }

    override fun createObserver() {
        mViewModel.mMediaContent.observe(this, Observer { mContent ->
            mTvFfmpegMoreContent.text = getString(R.string.media_content, mContent)
        })
    }

    private val onViewOnClickListener: View.OnClickListener by lazy {
        View.OnClickListener { view ->
            when (view?.id) {
                //同步多命令行,单独
                R.id.but_command_sync_separate -> {
                    mViewModel.cleanStringBuiler()
                    val cmdList = getCmdLists()
                    FFmpegCommand.runSync(cmdList[0], callback(cmdList[0], "音频转码"))
                    FFmpegCommand.runSync(cmdList[1], callback(cmdList[1], "视频转码"))
                    FFmpegCommand.runSync(cmdList[2], callback(cmdList[2], "视频抽取第一帧"))
                }
                //同步多命令行,合并
                R.id.but_command_sync_merge -> {
                    val cmdList = getCmdLists()
                    FFmpegCommand.runMoreSync(cmdList, callback2(cmdList, "同步多命令行,合并"))
                }
                //异步多命令行,合并
                R.id.but_command_async_merge -> {
                    val cmdList = getCmdLists()
                    FFmpegCommand.runMoreAsync(cmdList, callback(cmdList, "异步多命令行,合并"))
                }
            }
        }
    }

    private fun getCmdLists(): ArrayList<Array<String>> {
        val cmdList = arrayListOf<Array<String>>()
        val cmdAAC = FFmpegUtils.transformAudio(mViewModel.mAudioPath.value, targetAAC)
        val cmdAVI = FFmpegUtils.transformVideo(mViewModel.mVideoPath.value, targetAVI)
        val cmdYUV = FFmpegUtils.screenShot(mViewModel.mVideoPath.value, targetJPEG)
        cmdList.add(cmdAAC)
        cmdList.add(cmdAVI)
        cmdList.add(cmdYUV)
        return cmdList
    }

    private fun callback(cmdList: ArrayList<Array<String>>, msg: String) =
        object : CommonCallBack() {
            var str_ffmpeg_more_command = ""

            init {
                cmdList.toList().forEach { cmds ->
                    var str_ffmpeg_command = ""
                    cmds.toList().forEach {
                        if (StringUtils.isEmpty(str_ffmpeg_command)) {
                            str_ffmpeg_command = it
                        } else {
                            str_ffmpeg_command = "${str_ffmpeg_command} ${it}"
                        }
                    }
                    if (StringUtils.isNoEmpty(str_ffmpeg_command)) {
                        if (StringUtils.isEmpty(str_ffmpeg_more_command)) {
                            str_ffmpeg_command = str_ffmpeg_more_command
                        } else {
                            str_ffmpeg_command = "${str_ffmpeg_more_command}\n${str_ffmpeg_command}"
                        }
                    }
                }
            }

            override fun onStart() {
                showToast("开始执行ffmpeg命令")
                mTvFfmpegMoreContent.setText("FFmpeg命令: ${str_ffmpeg_more_command}\n${msg}\n开始执行了")
            }

            override fun onProgress(progress: Int) {
                LogUtils.i("ffmpeg命令 进度:${progress}")
                mTvFfmpegMoreContent.setText("FFmpeg命令: ${str_ffmpeg_more_command}\n${msg}\n进度:${progress}")
            }

            override fun onComplete() {
                showToast("ffmpeg命令执行完毕")
                mTvFfmpegMoreContent.setText("FFmpeg命令: ${str_ffmpeg_more_command}\n${msg}执行完毕\n路径: ${targetAAC}\n${targetAVI}\n${targetJPEG}")
            }

            override fun onError(t: Throwable?) {
                showToast("ffmpeg命令执行出错")
                mTvFfmpegMoreContent.setText("FFmpeg命令: ${str_ffmpeg_more_command}\n${msg}\n错误: ${t.toString()}")
            }

            override fun onCancel() {
                showToast("ffmpeg命令取消执行")
                mTvFfmpegMoreContent.setText("FFmpeg命令: ${str_ffmpeg_more_command}\n${msg}\n取消了")
            }
        }

    private fun callback2(cmdList: ArrayList<Array<String>>, msg: String) =
        object : FFmpegCommand.OnFFmpegCommandListener {
            var str_ffmpeg_more_command = ""

            init {
                cmdList.toList().forEach { cmds ->
                    var str_ffmpeg_command = ""
                    cmds.toList().forEach {
                        if (StringUtils.isEmpty(str_ffmpeg_command)) {
                            str_ffmpeg_command = it
                        } else {
                            str_ffmpeg_command = "${str_ffmpeg_command} ${it}"
                        }
                    }
                    if (StringUtils.isNoEmpty(str_ffmpeg_command)) {
                        if (StringUtils.isEmpty(str_ffmpeg_more_command)) {
                            str_ffmpeg_command = str_ffmpeg_more_command
                        } else {
                            str_ffmpeg_command = "${str_ffmpeg_more_command}\n${str_ffmpeg_command}"
                        }
                    }
                }
            }

            override fun onProgress(progress: Int) {
                LogUtils.i("ffmpeg命令 进度:${progress}")
                mTvFfmpegMoreContent.setText("FFmpeg命令: ${str_ffmpeg_more_command}\n${msg}\n进度:${progress}")
            }

            override fun onComplete() {
                showToast("ffmpeg命令执行完毕")
                mTvFfmpegMoreContent.setText("FFmpeg命令: ${str_ffmpeg_more_command}\n${msg}执行完毕\n路径: ${targetAAC}\n${targetAVI}\n${targetJPEG}")
            }

            override fun onCancel() {
                showToast("ffmpeg命令取消执行")
                mTvFfmpegMoreContent.setText("FFmpeg命令: ${str_ffmpeg_more_command}\n${msg}\n取消了")
            }
        }

    private fun callback(cmd: Array<String>, msg: String) =
        object : FFmpegCommand.OnFFmpegCommandListener {
            var str_ffmpeg_command = ""

            init {
                cmd.toList().forEach {
                    if (StringUtils.isEmpty(str_ffmpeg_command)) {
                        str_ffmpeg_command = it
                    } else {
                        str_ffmpeg_command = "${str_ffmpeg_command} ${it}"
                    }
                }
            }

            override fun onComplete() {
                showToast("ffmpeg命令执行完毕")

                val strCmd = when (msg) {
                    "视频抽取第一帧" -> {
                        "FFmpeg命令: ${str_ffmpeg_command}\n${msg}执行完毕\n路径: ${targetJPEG}"
                    }
                    "音频转码" -> {
                        "FFmpeg命令: ${str_ffmpeg_command}\n${msg}执行完毕\n路径: ${targetAAC}"
                    }
                    "视频转码" -> {
                        "FFmpeg命令: ${str_ffmpeg_command}\n${msg}执行完毕\n路径: ${targetAVI}"
                    }
                    else -> null
                }
                if (StringUtils.isNoEmpty(strCmd)) {
                    mTvFfmpegMoreContent.setText(strCmd)
                    mViewModel.mVideoContent.append(strCmd)
                }
                if ("视频抽取第一帧".equals(msg)) {
                    mTvFfmpegMoreContent.setText(mViewModel.mVideoContent.toString())
                }
            }

            override fun onCancel() {
                showToast("ffmpeg命令取消执行")
                mTvFfmpegMoreContent.setText("FFmpeg命令: ${str_ffmpeg_command}\n${msg}\n取消了")
            }

            override fun onProgress(progress: Int) {
                LogUtils.i("ffmpeg命令 进度:${progress}")
                mTvFfmpegMoreContent.setText("FFmpeg命令: ${str_ffmpeg_command}\n${msg}\n进度:${progress}")
            }

        }
}