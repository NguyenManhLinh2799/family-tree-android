package com.example.familytree.tree

import android.content.Context
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.familytree.database.getDatabase
import com.example.familytree.member.AddMemberViewModel
import com.example.familytree.network.NetworkMemory
import com.example.familytree.repository.FamilyTreeRepository
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class PostMemoryViewModel(context: Context): ViewModel() {

    private val database = getDatabase(context)
    private val familyTreeRepository = FamilyTreeRepository(database)

    var navigateToTreeMembers = MutableLiveData<Boolean?>()

    fun postMemory(newMemory: NetworkMemory, allImageUris: List<Uri>) {
        viewModelScope.launch {
            val allImageUrls = familyTreeRepository.uploadImages(allImageUris).data
            newMemory.imageUrls = allImageUrls
            familyTreeRepository.postMemory(newMemory)
            navigateToTreeMembers.value = true
        }
    }

    fun doneNavigating() {
        this.navigateToTreeMembers.value = null
    }

    class Factory(val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(PostMemoryViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return PostMemoryViewModel(context) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}