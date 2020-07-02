package com.zjx.appcommonlibrary.ext

import com.google.android.material.tabs.TabLayout

fun TabLayout.addOnTabSelectedListener(
    bodyReselected: ((tabReselected: TabLayout.Tab?) -> Unit)? = null,
    bodyUnselected: ((tabUnselected: TabLayout.Tab?) -> Unit)? = null,
    bodySelected: ((tabSelected: TabLayout.Tab?) -> Unit)? = null
) {
    addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
        //再次选中tab
        override fun onTabReselected(tab: TabLayout.Tab?) {
            if (bodyReselected != null) {
                bodyReselected(tab)
            }
        }

        //未选中tab
        override fun onTabUnselected(tab: TabLayout.Tab?) {
            if (bodyUnselected != null) {
                bodyUnselected(tab)
            }
        }

        //选中了tab
        override fun onTabSelected(tab: TabLayout.Tab?) {
            if (bodySelected != null) {
                bodySelected(tab)
            }
        }
    })
}

fun TabLayout.setOnTabSelectedListener(
    bodySelected: (tabSelected: TabLayout.Tab?) -> Unit
) = addOnTabSelectedListener(null, null, bodySelected)


fun TabLayout.setOnTabSelectedAndReselectedListener(
    bodyReselected: (tabReselected: TabLayout.Tab?) -> Unit,
    bodySelected: (tabSelected: TabLayout.Tab?) -> Unit
) = addOnTabSelectedListener(bodyReselected, null, bodySelected)


fun TabLayout.setOnTabSelectedAndUnselectedListener(
    bodyUnselected: (tabUnselected: TabLayout.Tab?) -> Unit,
    bodySelected: (tabSelected: TabLayout.Tab?) -> Unit
) = addOnTabSelectedListener(null, bodyUnselected, bodySelected)
