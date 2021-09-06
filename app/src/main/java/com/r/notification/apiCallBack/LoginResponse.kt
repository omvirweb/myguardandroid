package com.r.notification.apiCallBack

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.r.notification.models.LoginData
import java.io.Serializable


class LoginResponse:Serializable {
    @SerializedName("status")
    @Expose
    private var status: String? = null

    @SerializedName("data")
    @Expose
    private var data: LoginData? = null

    @SerializedName("message")
    @Expose
    private var message: String? = null

    fun getStatus(): String? {
        return status
    }

    fun setStatus(status: String?) {
        this.status = status
    }

    fun getData(): LoginData? {
        return data
    }

    fun setData(data: LoginData?) {
        this.data = data
    }

    fun getMessage(): String? {
        return message
    }

    fun setMessage(message: String?) {
        this.message = message
    }
}