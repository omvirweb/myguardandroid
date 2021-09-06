package com.r.notification.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class LoginData:Serializable {
    @SerializedName("id")
    @Expose
    private var id: Int? = null

    @SerializedName("first_name")
    @Expose
    private var firstName: String? = null

    @SerializedName("last_name")
    @Expose
    private var lastName: Any? = null

    @SerializedName("email")
    @Expose
    private var email: String? = null

    @SerializedName("email_verified_at")
    @Expose
    private var emailVerifiedAt: Any? = null

    @SerializedName("address")
    @Expose
    private var address: Any? = null

    @SerializedName("city_id")
    @Expose
    private var cityId: Any? = null

    @SerializedName("mobile_number")
    @Expose
    private var mobileNumber: String? = null

    @SerializedName("isActive")
    @Expose
    private var isActive: String? = null

    @SerializedName("role")
    @Expose
    private var role: String? = null

    @SerializedName("created_at")
    @Expose
    private var createdAt: String? = null

    @SerializedName("updated_at")
    @Expose
    private var updatedAt: String? = null

    @SerializedName("api_token")
    @Expose
    private var apiToken: String? = null

    fun getId(): Int? {
        return id
    }

    fun setId(id: Int?) {
        this.id = id
    }

    fun getFirstName(): String? {
        return firstName
    }

    fun setFirstName(firstName: String?) {
        this.firstName = firstName
    }

    fun getLastName(): Any? {
        return lastName
    }

    fun setLastName(lastName: Any?) {
        this.lastName = lastName
    }

    fun getEmail(): String? {
        return email
    }

    fun setEmail(email: String?) {
        this.email = email
    }

    fun getEmailVerifiedAt(): Any? {
        return emailVerifiedAt
    }

    fun setEmailVerifiedAt(emailVerifiedAt: Any?) {
        this.emailVerifiedAt = emailVerifiedAt
    }

    fun getAddress(): Any? {
        return address
    }

    fun setAddress(address: Any?) {
        this.address = address
    }

    fun getCityId(): Any? {
        return cityId
    }

    fun setCityId(cityId: Any?) {
        this.cityId = cityId
    }

    fun getMobileNumber(): String? {
        return mobileNumber
    }

    fun setMobileNumber(mobileNumber: String?) {
        this.mobileNumber = mobileNumber
    }

    fun getIsActive(): String? {
        return isActive
    }

    fun setIsActive(isActive: String?) {
        this.isActive = isActive
    }

    fun getRole(): String? {
        return role
    }

    fun setRole(role: String?) {
        this.role = role
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

    fun getApiToken(): String? {
        return apiToken
    }

    fun setApiToken(apiToken: String?) {
        this.apiToken = apiToken
    }
}