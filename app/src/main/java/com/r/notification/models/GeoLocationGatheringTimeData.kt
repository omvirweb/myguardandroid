package com.r.notification.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class GeoLocationGatheringTimeData:Serializable {
    @SerializedName("id")
    @Expose
    private var id: Int? = null

    @SerializedName("location_gathering_interval_time")
    @Expose
    private var locationGatheringIntervalTime: String? = null

    @SerializedName("created_by")
    @Expose
    private var createdBy: Int? = null

    @SerializedName("updated_by")
    @Expose
    private var updatedBy: Any? = null

    @SerializedName("created_at")
    @Expose
    private var createdAt: String? = null

    @SerializedName("updated_at")
    @Expose
    private var updatedAt: String? = null

    fun getId(): Int? {
        return id
    }

    fun setId(id: Int?) {
        this.id = id
    }

    fun getLocationGatheringIntervalTime(): String? {
        return locationGatheringIntervalTime
    }

    fun setLocationGatheringIntervalTime(locationGatheringIntervalTime: String?) {
        this.locationGatheringIntervalTime = locationGatheringIntervalTime
    }

    fun getCreatedBy(): Int? {
        return createdBy
    }

    fun setCreatedBy(createdBy: Int?) {
        this.createdBy = createdBy
    }

    fun getUpdatedBy(): Any? {
        return updatedBy
    }

    fun setUpdatedBy(updatedBy: Any?) {
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
}