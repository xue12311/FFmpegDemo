package com.zjx.appcommonlibrary.widget.notify.entities

import android.app.Notification

data class NotificationModel(
    var notification: Notification? = null,
    var notificationId: Int? = -1
)