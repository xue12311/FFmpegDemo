package com.zjx.ffmpegdemo.utils

import com.zjx.appcommonlibrary.utils.utilcode.util.*
import com.zjx.appcommonlibrary.utils.utilcode.util.FileUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

object FileUtils {
    fun isFileExists(filePath: String?) = FileUtils.isFileExists(filePath)
    fun isFileNoExists(filePath: String?) = !isFileExists(filePath)

    /**
     * 将assets文件夹中的内容写入本地
     * @param dirName String 文件夹名称
     * @param fileName String 文件名称及后缀名
     * @return String? 文件目录: SDCard/Android/data/应用包名/files/ 目录/文件
     */
    suspend fun getFileFromAssets(dirName: String, fileName: String): String? =
        withContext(Dispatchers.IO) {
            //创建文件夹
            val mFileDir = Utils.getApp().applicationContext.getExternalFilesDir(dirName)
            if (mFileDir != null) {
                //创建文件
                val mFilePath: String = File(mFileDir, fileName).absolutePath
                if (FileUtils.isFileExists(mFilePath)) {
                    return@withContext mFilePath
                    //将assets文件夹中的内容写入本地
                } else if (ResourceUtils.copyFileFromAssets(fileName, mFilePath)) {
                    return@withContext mFilePath
                } else {
                    return@withContext null
                }
            }
            return@withContext null
        }

    fun getExternalDirFilePath(dirName: String): String? = getExternalDirFile(dirName)?.absolutePath
    fun deleteAllInDir(dirPath: String?) = FileUtils.deleteAllInDir(dirPath)
    fun getExternalDirFile(dirName: String): File? {
        val file = getExternalFile(dirName)
        FileUtils.createOrExistsDir(file)
        return file
    }
//        Utils.getApp().applicationContext.getExternalFilesDir(dirName)

    fun getExternalFile(fileName: String) = getExternalFile("ffmpeg", fileName)
    fun getExternalFile(dirName: String, fileName: String): File? {
        //创建文件夹
        val mFileDir = Utils.getApp().applicationContext.getExternalFilesDir(dirName)
        if (mFileDir != null) {
            //创建文件
            return File(mFileDir, fileName)
        } else {
            return null
        }
    }

    fun getExternalFilePath(fileName: String): String? = getExternalFilePath("ffmpeg", fileName)

    fun getExternalFilePath(dirName: String, fileName: String): String? =
        getExternalFile(dirName, fileName)?.absolutePath

    fun getFileName(filePath: String?): String? {
        return filePath?.substring(filePath.lastIndexOf(File.separator) + 1)
    }

    /**
     * 创建写入内容文件
     * 请注意一定要申请文件读写权限
     * @return
     */
    suspend fun createFFmpegConcatTxtFile(
        vararg filePaths: String?
    ): String? = withContext(Dispatchers.IO) {
        //本文件需要和视频文件再同一个文件夹内
        val file = getExternalFile("video", "filelist.txt")
        if (file == null) {
            return@withContext null
        }
        FileUtils.delete(file)
        var content = ""
        for (filePath in filePaths) {
            content += "file ${getFileName(filePath)}\n"
        }

        try {
            if (StringUtils.isEmpty(content)) {
                return@withContext null
            } else if (FileIOUtils.writeFileFromString(file, content)) {
                return@withContext file?.absolutePath
            } else {
                return@withContext null
            }
        } catch (e: Exception) {
            LogUtils.e("写入 filelist.txt 失败 ${e.toString()}")
            return@withContext null
        }
    }
}