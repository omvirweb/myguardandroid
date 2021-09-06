package com.r.notification.apiCallBack

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName




class StoreGEOLocationResponse {
    @SerializedName("status")
    @Expose
    private var status: String? = null

    @SerializedName("data")
    @Expose
    private var data: String? = null

    @SerializedName("message")
    @Expose
    private var message: String? = null

    fun getStatus(): String? {
        return status
    }

    fun setStatus(status: String?) {
        this.status = status
    }

    fun getData(): String? {
        return data
    }

    fun setData(data: String?) {
        this.data = data
    }

    fun getMessage(): String? {
        return message
    }

    fun setMessage(message: String?) {
        this.message = message
    }
}