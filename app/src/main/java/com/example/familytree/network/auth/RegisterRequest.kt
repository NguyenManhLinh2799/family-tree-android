package com.example.familytree.network.auth

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RegisterRequest(
        val userName: String,
        val email: String,
        val password: String,
        val phone: String,
        val firstName: String,
        val lastName: String,
        val midName: String,
        val getRefreshToken: Boolean
) : Parcelable