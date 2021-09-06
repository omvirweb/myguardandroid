package com.r.notification.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class NotificationIntervalTimeData:Serializable {
    @SerializedName("id")
    @Expose
    private var id: Int? = null

    @SerializedName("title")
    @Expose
    private var title: String? = null

    @SerializedName("description")
    @Expose
    private var description: Any? = null

    @SerializedName("interval_time")
    @Expose
    private var intervalTime: String? = null

    @SerializedName("created_by")
    @Expose
    private var createdBy: Int? = null

    @SerializedName("updated_by")
    @Expose
    private var updatedBy: Int? = null

    @SerializedName("created_at")
    @Expose
    private var createdAt: String? = null

    @SerializedName("updated_at")
    @Expose
    private var updatedAt: String? = null

    @SerializedName("duty_start_time")
    @Expose
    private var dutyStartTime: String? = null

    @SerializedName("duty_end_time")
    @Expose
    private var dutyEndTime: String? = null

    fun getId(): Int? {
        return id
    }

    fun setId(id: Int?) {
        this.id = id
    }

    fun getTitle(): String? {
        return title
    }

    fun setTitle(title: String?) {
        this.title = title
    }

    fun getDescription(): Any? {
        return description
    }

    fun setDescription(description: Any?) {
        this.description = description
    }

    fun getIntervalTime(): String? {
        return intervalTime
    }

    fun setIntervalTime(intervalTime: String?) {
        this.intervalTime = intervalTime
    }

    fun getCreatedBy(): Int? {
        return createdBy
    }

    fun setCreatedBy(createdBy: Int?) {
        this.createdBy = createdBy
    }

    fun getUpdatedBy(): Int? {
        return updatedBy
    }

    fun setUpdatedBy(updatedBy: Int?) {
        this.updatedBy = updatedBy
    }

    fun getCreatedAt(): String? {
        return createdAt
    }

    fun setCreatedAt(createdAt: String?) {
        this.createdAt = createdAt
    }

    fun getUpdatedAt(): String? {
        return updatedAt
    }

    fun setUpdatedAt(updatedAt: String?) {
        this.updatedAt = updatedAt
    }




    fun getDutyStartTime(): String? {
        return dutyStartTime
    }

    fun setDutyStartTime(dutyStartTime: String?) {
        this.dutyStartTime = dutyStartTime
    }

    fun getDutyEndTime(): String? {
        return dutyEndTime
    }

    fun setDutyEndTime(dutyEndTime: String?) {
        this.dutyEndTime = dutyEndTime
    }
}