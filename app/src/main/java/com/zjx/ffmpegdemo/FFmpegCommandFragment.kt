package com.zjx.ffmpegdemo

import android.view.View
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.coder.ffmpeg.annotation.Direction
import com.coder.ffmpeg.annotation.ImageFormat
import com.coder.ffmpeg.annotation.Transpose
import com.coder.ffmpeg.call.CommonCallBack
import com.coder.ffmpeg.call.IFFmpegCallBack
import com.coder.ffmpeg.jni.FFmpegCommand
import com.coder.ffmpeg.utils.FFmpegUtils
import com.zjx.appcommonlibrary.base.viewmodel.BaseVmFragment
import com.zjx.appcommonlibrary.utils.utilcode.util.LogUtils
import com.zjx.appcommonlibrary.utils.utilcode.util.StringUtils
import com.zjx.ffmpegdemo.model.CommandBean
import com.zjx.ffmpegdemo.utils.FileUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.File
import java.util.*

/**
 * FFmpeg命令界面
 */
class FFmpegCommandFragment : BaseVmFragment<FFmpegViewModel>() {
    private lateinit var mTvFfmpegContent: TextView
    private lateinit var mRvFfmpegData: RecyclerView
    private var mAdapter: FFmpegCommandAdapter? = null

    //目标文件路径
    private var targetPath: String? = null

    override fun layoutId(): Int = R.layout.fragment_ffmpeg_command

    override fun initView(view: View) {
        mTvFfmpegContent = view.findViewById<TextView>(R.id.tv_ffmpeg_content)
        mAdapter = FFmpegCommandAdapter(getList())
        mRvFfmpegData = view.findViewById<RecyclerView>(R.id.rv_ffmpeg_data).apply {
            layoutManager = GridLayoutManager(activity, 3)
            adapter = mAdapter
        }
        mAdapter?.setItemClickListener(object : FFmpegCommandAdapter.ItemClickListener {
            override fun itemClick(id: Int, name: String) {
                when (id) {
                    //音频转码
                    0 -> {
                        targetPath = FileUtils.getExternalFilePath("target_${name}.aac")
                        val cmd =
                            FFmpegUtils.transformAudio(mViewModel.mAudioPath.value, targetPath)
                        runFFmpegCommand(cmd, callback(cmd, name, targetPath))
                    }
                    //视频转码
                    1 -> {
                        targetPath = FileUtils.getExternalFilePath("target_${name}.avi")
                        val cmd =
                            FFmpegUtils.transformVideo(mViewModel.mVideoPath.value, targetPath)
                        runFFmpegCommand(cmd, callback(cmd, name, targetPath))
                    }
                    //音频剪切
                    2 -> {
                        targetPath = FileUtils.getExternalFilePath("target_${name}.mp3")
                        val cmd =
                            FFmpegUtils.cutAudio(mViewModel.mAudioPath.value, 5, 10, targetPath)
                        runFFmpegCommand(cmd, callback(cmd, name, targetPath))
                    }
                    //视频剪切
                    3 -> {
                        targetPath = FileUtils.getExternalFilePath("target_${name}.mp4")
                        val cmd =
                            FFmpegUtils.cutVideo(mViewModel.mVideoPath.value, 5, 10, targetPath)
                        runFFmpegCommand(cmd, callback(cmd, name, targetPath))
                    }
                    //音频拼接
                    4 -> {
                        targetPath = FileUtils.getExternalFilePath("target_${name}.mp3")
                        val cmd = FFmpegUtils.concatAudio(
                            mViewModel.mAudioPath.value,
                            mViewModel.mAudioBgPath.value,
                            targetPath
                        )
                        runFFmpegCommand(cmd, callback(cmd, name, targetPath))
                    }
                    //视频拼接
                    5 -> {
                        targetPath = FileUtils.getExternalFilePath("target_${name}.mp4")
                        //FFmpeg concat 分离器
                        val input_file_array = arrayOf(
                            mViewModel.mVideoPath.value,
                            mViewModel.mVideoPath.value,
                            mViewModel.mVideoPath.value
                        )
                        lifecycleScope.launch(Dispatchers.Main) {
                            var filePath = async {
                                FileUtils.createFFmpegConcatTxtFile(*input_file_array)
                            }.await()
                            val cmd = FFmpegUtils.concatVideo(
                                filePath,
                                targetPath
                            )
                            runFFmpegCommand(cmd, callback(cmd, name, targetPath))
                        }
                    }
                    //从视频文件中抽取音频
                    6 -> {
                        targetPath = FileUtils.getExternalFilePath("target_${name}.aac")
                        mViewModel.mAudioPathExtract.value = targetPath
                        val cmd = FFmpegUtils.extractAudio(mViewModel.mVideoPath.value, targetPath)
                        runFFmpegCommand(cmd, callback(cmd, name, targetPath))
                    }
                    //从视频文件中抽取视频
                    7 -> {
                        targetPath = FileUtils.getExternalFilePath("target_${name}.mp4")
                        mViewModel.mVideoPathExtract.value = targetPath
                        val cmd = FFmpegUtils.extractVideo(mViewModel.mVideoPath.value, targetPath)
                        runFFmpegCommand(cmd, callback(cmd, name, targetPath))
                    }
                    //音视频合成
                    8 -> {
                        if (FileUtils.isFileNoExists(mViewModel.mVideoPathExtract.value)) {
                            showToast("请先执行抽取视频操作")
                        } else if (FileUtils.isFileNoExists(mViewModel.mAudioPathExtract.value)) {
                            showToast("请先执行抽取音频操作")
                        } else {
                            targetPath = FileUtils.getExternalFilePath("target_${name}.mp4")
                            val cmd = FFmpegUtils.mixAudioVideo(
                                mViewModel.mVideoPathExtract.value,
                                mViewModel.mAudioPathExtract.value,
                                targetPath
                            )
                            runFFmpegCommand(cmd, callback(cmd, name, targetPath))
                        }
                    }
                    //获取第一帧
                    9 -> {
                        targetPath = FileUtils.getExternalFilePath("target_${name}.jpeg")
                        val cmd = FFmpegUtils.screenShot(mViewModel.mVideoPath.value, targetPath)
                        runFFmpegCommand(cmd, callback(cmd, name, targetPath))
                    }
                    //视频转图片
                    10 -> {
                        targetPath = FileUtils.getExternalDirFilePath("target_${name}")
                        FileUtils.deleteAllInDir(targetPath)
                        mViewModel.mVideoToImagePath.value = targetPath
                        val cmd = FFmpegUtils.video2Image(
                            mViewModel.mVideoPath.value,
                            targetPath,
                            ImageFormat.PNG
                        )
                        runFFmpegCommand(cmd, callback(cmd, name, targetPath))
                    }
                    //视频转gif
                    11 -> {
                        targetPath = FileUtils.getExternalFilePath("target_${name}.gif")
                        val cmd =
                            FFmpegUtils.video2Gif(mViewModel.mVideoPath.value, 0, 10, targetPath)
                        runFFmpegCommand(cmd, callback(cmd, name, targetPath))
                    }
                    //添加水印
                    12 -> {
                        //这个在测试时，没有进度提示，而且速度很慢。
                        targetPath = FileUtils.getExternalFilePath("target_${name}.mp4")
                        val cmd = FFmpegUtils.addWaterMark(
                            mViewModel.mVideoPath.value,
                            mViewModel.mImagePath.value,
                            targetPath
                        )
                        runFFmpegCommand(cmd, callback(cmd, name, targetPath))
                    }
                    //图片转视频
                    13 -> {
                        if (FileUtils.isFileNoExists(mViewModel.mVideoToImagePath.value)) {
                            showToast("请先执行视频转图片操作")
                        } else {
                            targetPath = FileUtils.getExternalFilePath("target_${name}.mp4")
                            val cmd = FFmpegUtils.image2Video(
                                mViewModel.mVideoToImagePath.value,
                                ImageFormat.PNG,
                                targetPath
                            )
                            runFFmpegCommand(cmd, callback(cmd, name, targetPath))
                        }
                    }
                    //音频解码PCM
                    14 -> {
                        targetPath = FileUtils.getExternalFilePath("target_${name}.pcm")
                        mViewModel.mDecodeAudioPath.value = targetPath
                        val cmd = FFmpegUtils.decodeAudio(
                            mViewModel.mAudioPath.value,
                            targetPath,
                            44100,
                            2
                        )
                        runFFmpegCommand(cmd, callback(cmd, name, targetPath))
                    }
                    //音频编码
                    15 -> {
                        if (FileUtils.isFileNoExists(mViewModel.mDecodeAudioPath.value)) {
                            showToast("请先执行音频解码PCM操作")
                        } else {
                            targetPath = FileUtils.getExternalFilePath("target_${name}.wav")
                            val cmd = FFmpegUtils.encodeAudio(
                                mViewModel.mDecodeAudioPath.value,
                                targetPath,
                                44100,
                                2
                            )
                            runFFmpegCommand(cmd, callback(cmd, name, targetPath))
                        }
                    }
                    //多画面拼接
                    16 -> {
                        targetPath = FileUtils.getExternalFilePath("target_${name}.mp4")
                        val cmd = FFmpegUtils.multiVideo(
                            mViewModel.mVideoPath.value,
                            mViewModel.mVideoPath.value,
                            targetPath,
                            Direction.LAYOUT_HORIZONTAL
                        )
                        runFFmpegCommand(cmd, callback(cmd, name, targetPath))
                    }
                    //反序播放
                    17 -> {
                        targetPath = FileUtils.getExternalFilePath("target_${name}.mp4")
                        val cmd = FFmpegUtils.reverseVideo(mViewModel.mVideoPath.value, targetPath)
                        runFFmpegCommand(cmd, callback(cmd, name, targetPath))
                    }
                    //画中画
                    18 -> {
                        targetPath = FileUtils.getExternalFilePath("target_${name}.mp4")
                        val cmd = FFmpegUtils.picInPicVideo(
                            mViewModel.mVideoPath.value,
                            mViewModel.mVideoPath.value,
                            100,
                            100,
                            targetPath
                        )
                        runFFmpegCommand(cmd, callback(cmd, name, targetPath))
                    }
                    //混音
                    19 -> {
                        targetPath = FileUtils.getExternalFilePath("target_${name}.mp3")
                        val cmd = FFmpegUtils.mixAudio(
                            mViewModel.mAudioPath.value,
                            mViewModel.mAudioBgPath.value,
                            targetPath
                        )
                        runFFmpegCommand(cmd, callback(cmd, name, targetPath))
                    }
                    //视频缩小一倍
                    20 -> {
                        targetPath = FileUtils.getExternalFilePath("target_${name}.mp4")
                        val cmd =
                            FFmpegUtils.videoDoubleDown(mViewModel.mVideoPath.value, targetPath)
                        runFFmpegCommand(cmd, callback(cmd, name, targetPath))
                    }
                    //倍速播放
                    21 -> {
                        targetPath = FileUtils.getExternalFilePath("target_${name}.mp4")
                        val cmd = FFmpegUtils.videoSpeed2(mViewModel.mVideoPath.value, targetPath)
                        runFFmpegCommand(cmd, callback(cmd, name, targetPath))
                    }
                    //视频降噪
                    22 -> {
                        targetPath = FileUtils.getExternalFilePath("target_${name}.mp4")
                        val cmd = FFmpegUtils.denoiseVideo(mViewModel.mVideoPath.value, targetPath)
                        runFFmpegCommand(cmd, callback(cmd, name, targetPath))
                    }
                    //音频降音
                    23 -> {
                        targetPath = FileUtils.getExternalFilePath("target_${name}.mp3")
                        val cmd =
                            FFmpegUtils.changeVolume(mViewModel.mAudioPath.value, 0.5f, targetPath)
                        runFFmpegCommand(cmd, callback(cmd, name, targetPath))
                    }
                    //视频解码YUV
                    24 -> {
                        targetPath = FileUtils.getExternalFilePath("target_${name}.yuv")
                        mViewModel.mVideoYUVPath.value = targetPath
                        val cmd = FFmpegUtils.decode2YUV(mViewModel.mVideoPath.value, targetPath)
                        runFFmpegCommand(cmd, callback(cmd, name, targetPath))
                    }
                    //视频编码H264
                    25 -> {
                        if (FileUtils.isFileNoExists(mViewModel.mVideoYUVPath.value)) {
                            showToast("请先执行视频解码YUV操作")
                        } else {
                            targetPath = FileUtils.getExternalFilePath("target_${name}.h264")
                            val cmd =
                                FFmpegUtils.yuv2H264(mViewModel.mVideoYUVPath.value, targetPath)
                            runFFmpegCommand(cmd, callback(cmd, name, targetPath))
                        }
                    }
                    //音频淡入
                    26 -> {
                        targetPath = FileUtils.getExternalFilePath("target_${name}.mp3")
                        val cmd = FFmpegUtils.audioFadeIn(mViewModel.mAudioPath.value, targetPath)
                        runFFmpegCommand(cmd, callback(cmd, name, targetPath))
                    }
                    //音频淡出
                    27 -> {
                        targetPath = FileUtils.getExternalFilePath("target_${name}.mp3")
                        val cmd =
                            FFmpegUtils.audioFadeOut(mViewModel.mAudioPath.value, targetPath, 20, 5)
                        runFFmpegCommand(cmd, callback(cmd, name, targetPath))
                    }
                    //视频亮度
                    28 -> {
                        targetPath = FileUtils.getExternalFilePath("target_${name}.mp4")
                        val cmd =
                            FFmpegUtils.videoBright(mViewModel.mVideoPath.value, targetPath, 0.35f)
                        runFFmpegCommand(cmd, callback(cmd, name, targetPath))
                    }
                    //视频对比度
                    29 -> {
                        targetPath = FileUtils.getExternalFilePath("target_${name}.mp4")
                        val cmd =
                            FFmpegUtils.videoContrast(
                                mViewModel.mVideoPath.value,
                                targetPath,
                                1.35f
                            )
                        runFFmpegCommand(cmd, callback(cmd, name, targetPath))
                    }
                    //视频旋转
                    30 -> {
                        targetPath = FileUtils.getExternalFilePath("target_${name}.mp4")
                        val cmd =
                            FFmpegUtils.videoRotation(
                                mViewModel.mVideoPath.value,
                                targetPath,
                                Transpose.CLOCKWISE_ROTATION_90
                            )
                        runFFmpegCommand(cmd, callback(cmd, name, targetPath))
                    }
                    //视频缩放
                    31 -> {
                        targetPath = FileUtils.getExternalFilePath("target_${name}.mp4")
                        val cmd =
                            FFmpegUtils.videoScale(
                                mViewModel.mVideoPath.value,
                                targetPath,
                                360, 640
                            )
                        runFFmpegCommand(cmd, callback(cmd, name, targetPath))
                    }
                    //视频获取一帧图片
                    32 -> {
                        targetPath = FileUtils.getExternalFilePath("target_${name}.png")
                        val cmd =
                            FFmpegUtils.frame2Image(
                                mViewModel.mVideoPath.value,
                                targetPath,
                                "00:00:11.234"
                            )
                        runFFmpegCommand(cmd, callback(cmd, name, targetPath))
                    }
                    //音频转fdk_aac
                    33 -> {
                        targetPath = FileUtils.getExternalFilePath("target_${name}.aac")
                        mViewModel.mAudioAACPath.value = targetPath
                        val cmd =
                            FFmpegUtils.audio2Fdkaac(
                                mViewModel.mAudioPath.value,
                                targetPath
                            )
                        runFFmpegCommand(cmd, callback(cmd, name, targetPath))
                    }
                    //音频转mp3lamp
                    34 -> {
                        if (FileUtils.isFileNoExists(mViewModel.mAudioAACPath.value)) {
                            showToast("请先执行音频转FDK_AAC操作")
                        } else {
                            targetPath = FileUtils.getExternalFilePath("target_${name}.mp3")
                            val cmd =
                                FFmpegUtils.audio2Mp3lame(
                                    mViewModel.mAudioAACPath.value,
                                    targetPath
                                )
                            runFFmpegCommand(cmd, callback(cmd, name, targetPath))
                        }
                    }
                    //视频切片-hls
                    35 -> {
                        targetPath = FileUtils.getExternalFilePath("target_${name}.m3u8")
                        mViewModel.mVideoHLSPath.value = targetPath
                        val cmd =
                            FFmpegUtils.video2HLS(
                                mViewModel.mVideoPath.value,
                                targetPath,
                                10
                            )
                        runFFmpegCommand(cmd, callback(cmd, name, targetPath))
                    }
                    //合成hls-视频
                    36 -> {
                        if (FileUtils.isFileNoExists(mViewModel.mVideoHLSPath.value)) {
                            showToast("请先执行视频切换HLS操作")
                        } else {
                            targetPath = FileUtils.getExternalFilePath("target_${name}.mp4")
                            val cmd =
                                FFmpegUtils.hls2Video(
                                    mViewModel.mVideoHLSPath.value,
                                    targetPath
                                )
                            runFFmpegCommand(cmd, callback(cmd, name, targetPath))
                        }
                    }
                }
            }
        })
    }

    override fun lazyLoadData() {
        mViewModel.setFilePath()
    }

    override fun createObserver() {
        mViewModel.mMediaContent.observe(this, Observer { mMediaContent ->
            mTvFfmpegContent.text = getString(R.string.media_content, mMediaContent)
        })
    }

    fun getList(): MutableList<CommandBean> {
        val commands = this.resources.getStringArray(R.array.commands)
        val beans: MutableList<CommandBean> = ArrayList()
        for (i in commands.indices) {
            beans.add(CommandBean(commands[i], i))
        }
        return beans
    }

    private fun callback(cmd: Array<String>, msg: String, targetPath: String?) =
        object : CommonCallBack() {
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

            override fun onStart() {
                showToast("开始执行ffmpeg命令")
                mTvFfmpegContent.setText("FFmpeg命令: ${str_ffmpeg_command}\n${msg}\n开始执行了")
            }

            override fun onProgress(progress: Int) {
                LogUtils.i("ffmpeg命令 进度:${progress}")
                mTvFfmpegContent.setText("FFmpeg命令: ${str_ffmpeg_command}\n${msg}\n进度:${progress}")
            }

            override fun onComplete() {
                showToast("ffmpeg命令执行完毕")
                mTvFfmpegContent.setText("FFmpeg命令: ${str_ffmpeg_command}\n${msg}执行完毕\n路径: ${targetPath}")
            }

            override fun onError(t: Throwable?) {
                showToast("ffmpeg命令执行出错")
                mTvFfmpegContent.setText("FFmpeg命令: ${str_ffmpeg_command}\n${msg}\n错误: ${t.toString()}")
            }

            override fun onCancel() {
                showToast("ffmpeg命令取消执行")
                mTvFfmpegContent.setText("FFmpeg命令: ${str_ffmpeg_command}\n${msg}\n取消了")
            }
        }

    private fun runFFmpegCommand(cmd: Array<String>, callBack: IFFmpegCallBack) {
        FFmpegCommand.runAsync(cmd, callBack)
    }
}