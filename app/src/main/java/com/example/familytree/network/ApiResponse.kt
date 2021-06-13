package com.example.familytree.network

import android.os.Parcel
import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@JsonClass(generateAdapter = true)
data class ApiResponse<T>(
    val data: T,
    val message: String,
    val errors: String?)