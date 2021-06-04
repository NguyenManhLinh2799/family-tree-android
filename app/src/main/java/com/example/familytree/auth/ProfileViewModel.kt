package com.example.familytree.auth

import android.content.Context
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.familytree.database.getDatabase
import com.example.familytree.domain.User
import com.example.familytree.network.auth.EditProfileRequest
import com.example.familytree.network.auth.NetworkUser
import com.example.familytree.repository.FamilyTreeRepository
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class ProfileViewModel(context: Context): ViewModel() {

    private val database = getDatabase(context)
    private val familyTreeRepository = FamilyTreeRepository(database)

    var userProfile = MutableLiveData<User>()

    init {
        viewModelScope.launch {
            userProfile.value = familyTreeRepository.getProfile()
        }
    }

    fun editProfile(editedProfile: EditProfileRequest, imgUri: Uri?) {
        viewModelScope.launch {
            if (imgUri != null) {
                val imgUrl = familyTreeRepository.uploadImage(imgUri).data
                editedProfile.avatarUrl = imgUrl
            }

            familyTreeRepository.editProfile(editedProfile)
        }
    }

    class Factory(val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ProfileViewModel(context) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}