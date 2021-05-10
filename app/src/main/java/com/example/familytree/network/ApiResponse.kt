package com.example.familytree.network

import android.os.Parcel
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
data class ApiResponse<T>(
        val data: @RawValue T,
        val message: String,
        val errors: String?) : Parcelable {

        }