package com.example.familytree.member

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.familytree.database.getDatabase
import com.example.familytree.network.member.Member
import com.example.familytree.repository.FamilyTreeRepository
import kotlinx.coroutines.launch
import java.lang.Exception
import java.lang.IllegalArgumentException

class EditMemberViewModel(context: Context, memberID: Int) : ViewModel() {

    private val database = getDatabase(context)
    private val familyTreeRepository = FamilyTreeRepository(database)

    var member = MutableLiveData<Member>()

    init {
        viewModelScope.launch {
            try {
                member.value = familyTreeRepository.getMember(memberID).data!!
            } catch (e: Exception) {
                member.value = Member(
                    null,
                    0,
                    "",
                    "",
                    "",
                    "",
                    null,
                    null,
                    0,
                    null,
                    null,
                    null)
            }
        }
    }

    fun editMember(editedMember: Member, imgUri: Uri?) {
        viewModelScope.launch {
            if (imgUri != null) {
                val imgUrl = familyTreeRepository.uploadImage(imgUri).data
                editedMember.imageUrl = imgUrl
            }

            familyTreeRepository.editMember(editedMember)
        }
    }

    class Factory(val context: Context, val memberID: Int) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(EditMemberViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return EditMemberViewModel(context, memberID) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}