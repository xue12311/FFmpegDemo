/*
 * MIT License
 *
 * Copyright (c) 2018 Karn Saheb
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.zjx.appcommonlibrary.widget.notify

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.zjx.appcommonlibrary.utils.utilcode.util.Utils
import com.zjx.appcommonlibrary.widget.notify.entities.NotificationModel
import com.zjx.appcommonlibrary.widget.notify.entities.NotifyConfig
import com.zjx.appcommonlibrary.widget.notify.internal.NotificationChannelInterop
import com.zjx.appcommonlibrary.widget.notify.internal.NotificationInterop
import com.zjx.appcommonlibrary.widget.notify.internal.RawNotification

/**
 * Simplified Notification delivery for Android.
 */
class Notify internal constructor(internal var context: Context) {

    companion object {
        /**
         * The default CHANNEL_ID for a notification on Android O.
         */
        const val CHANNEL_DEFAULT_KEY = "application_notification"

        /**
         * The default CHANNEL_NAME for a notification on Android O.
         */
        const val CHANNEL_DEFAULT_NAME = "Application notifications."

        /**
         * The default CHANNEL_DESCRIPTION for a notification on Android O.
         */
        const val CHANNEL_DEFAULT_DESCRIPTION = "General application notifications."

        /**
         * Lowest priority for a notification. These notifications might not be shown to the user
         * except under special circumstances, such as detailed notification logs.
         */
        //用于后台消息 (例如天气或者位置信息)。只有用户下拉通知抽屉才能看到内容
        const val IMPORTANCE_MIN = NotificationCompat.PRIORITY_MIN

        /**
         * Lower priority for notifications that are deemed less important. The UI may choose to
         * show these items smaller, or at a different position in the list, compared to
         * notifications with normal importance.
         */
        //低优先级可以通知用户但又不是很紧急的事件。只显示状态栏图标
        const val IMPORTANCE_LOW = NotificationCompat.PRIORITY_LOW

        /**
         * Default priority for notifications. If your application does not prioritize its own
         * notifications, use this value for all notifications.
         */
        //默认优先级用于没有特殊优先级分类的通知
        const val IMPORTANCE_NORMAL = NotificationCompat.PRIORITY_DEFAULT

        /**
         * Higher priority for notifications, for more important notifications or alerts. The UI may
         * choose to show these items larger, or at a different position in notification lists,
         * compared with your app's notifications of normal importance.
         */
        //高优先级用于重要的通信内容，例如短消息或者聊天，这些都是对用户来说比较有兴趣的
        const val IMPORTANCE_HIGH = NotificationCompat.PRIORITY_HIGH

        /**
         * Highest priority for notifications, use for notifications that require the user's prompt
         * attention or input.
         */
        //重要而紧急的通知，通知用户这个事件是时间上紧迫的或者需要立即处理的。
        const val IMPORTANCE_MAX = NotificationCompat.PRIORITY_MAX

        /**
         * The flag to disable notification lights.
         */
        const val NO_LIGHTS = 0

        // This is the initial configuration of the Notify NotifyCreator.
        internal var defaultConfig = NotifyConfig()

        /**
         * Modify the default configuration.
         *
         * Takes a receiver with the NotifyConfig immutable object which has mutable fields.
         */
        fun defaultConfig(init: NotifyConfig.() -> Unit) {
            defaultConfig.init()
        }

        /**
         * A new {@see Notify} and {@see NotifyCreator} instance.
         *
         * This object is automatically initialized with the singleton default configuration which
         * can be modified using {@see Notify#defaultConfig((NotifyConfig) -> Unit)}.
         */
        fun with(context: Context): NotifyCreator {
            return NotifyCreator(Notify(context))
        }

        /**
         * Cancel an existing notification with a particular id.
         */
//        @Deprecated(
//            message = "NotificationManager might not have been initialized and can throw a NullPointerException -- provide a context.",
//            replaceWith = ReplaceWith("Notify.cancelNotification(context, id)")
//        )
//        @Throws(NullPointerException::class)
//        fun cancelNotification(id: Int) {
//            if (defaultConfig.notificationManager == null) {
//                defaultConfig.notificationManager =
//                    Utils.getApp().applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//            }
//            return NotificationInterop.cancelNotification(
//                defaultConfig.notificationManager!!,
//                id
//            )
//        }

        /**
         * Cancel an existing notification with a particular id.
         */
        fun cancelNotification(context: Context, id: Int) {
            if (defaultConfig.notificationManager == null) {
                defaultConfig.notificationManager =
                    context.applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            }
            return NotificationInterop.cancelNotification(defaultConfig.notificationManager!!, id)
        }

        /**
         * Cancel an existing notification with a particular id.
         */
        fun cancelNotification(id: Int) {
            if (defaultConfig.notificationManager == null) {
                defaultConfig.notificationManager =
                    Utils.getApp().applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            }

            return NotificationInterop.cancelNotification(defaultConfig.notificationManager!!, id)
        }

        /**
         * Cancel an existing notification with a particular id.
         */
        fun cancelAllNotification() {
            if (defaultConfig.notificationManager == null) {
                defaultConfig.notificationManager =
                    Utils.getApp().applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            }

            return NotificationInterop.cancelAllNotification(defaultConfig.notificationManager!!)
        }
    }


    init {
        this.context = context.applicationContext
        // Initialize notification manager instance.
        if (defaultConfig.notificationManager == null) {
            defaultConfig.notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }

        NotificationChannelInterop.with(defaultConfig.defaultAlerting)
    }

    /**
     * Return the standard {@see NotificationCompat.Builder} after applying fluent API
     * transformations (if any) from the {@see NotifyCreator} builder object.
     */
    internal fun asBuilder(payload: RawNotification): NotificationCompat.Builder {
        return NotificationInterop.buildNotification(this, payload)
    }

    /**
     * Delegate a {@see Notification.Builder} object to the Notify NotificationInterop class which
     * builds and displays the notification.
     *
     * This is a terminal operation.
     *
     * @param id    An optional integer which will be used as the ID for the notification that is
     *              shown. This argument is ignored if the notification is a NotifyCreator#stackable
     *              receiver is set.
     * @return An integer corresponding to the ID of the system notification. Any updates should use
     * this returned integer to make updates or to cancel the notification.
     */
    internal fun showModel(id: Int?, builder: NotificationCompat.Builder): NotificationModel {
        return NotificationInterop.showNotificationModel(
            Notify.defaultConfig.notificationManager!!,
            id,
            builder
        )
    }

    internal fun show(id: Int?, builder: NotificationCompat.Builder): Int {
        return NotificationInterop.showNotification(
            Notify.defaultConfig.notificationManager!!,
            id,
            builder
        )
    }
}
