package com.r.notification.apiCallBack

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.r.notification.models.NotificationIntervalTimeData
import java.io.Serializable


class NotificationIntervalTimeResponse:Serializable {
    @SerializedName("status")
    @Expose
    private var status: String? = null

    @SerializedName("data")
    @Expose
    private var data: List<NotificationIntervalTimeData?>? = null

    @SerializedName("message")
    @Expose
    private var message: String? = null

    fun getStatus(): String? {
        return status
    }

    fun setStatus(status: String?) {
        this.status = status
    }

    fun getData(): List<NotificationIntervalTimeData?>? {
        return data
    }

    fun setData(data: List<NotificationIntervalTimeData?>?) {
        this.data = data
    }

    fun getMessage(): String? {
        return message
    }

    fun setMessage(message: String?) {
        this.message = message
    }

}