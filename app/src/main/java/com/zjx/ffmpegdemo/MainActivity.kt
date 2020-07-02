package com.zjx.ffmpegdemo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
    }

    override fun onSupportNavigateUp(): Boolean {
//        return super.onSupportNavigateUp()
        //拦截系统的“返回”按钮
        return findNavController(R.id.nav_host_fragment).navigateUp()
    }

    private fun initView() {
        setTitle()
    }

    private fun setTitle() {
        //设置标题
        NavigationUI.setupActionBarWithNavController(
            this,
            findNavController(R.id.nav_host_fragment)
        )
    }
}