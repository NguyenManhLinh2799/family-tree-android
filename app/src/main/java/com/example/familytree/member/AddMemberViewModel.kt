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

class AddMemberViewModel(context: Context, memberID: Int) : ViewModel() {

    private val database = getDatabase(context)
    private val familyTreeRepository = FamilyTreeRepository(database)

    var member = MutableLiveData<Member>()
    var navigateToTreeMembers = MutableLiveData<Boolean?>()

    var addMessage = MutableLiveData<String>()

    init {
        viewModelScope.launch {
            try {
                member.value = familyTreeRepository.getMember(memberID).data!!
            } catch (e: Exception) {

            }
        }
    }

    fun addParentMember(parentMember: Member, imgUri: Uri?) {
        viewModelScope.launch {
            // Check if can add parent
            if (member.value?.parent1Id != null || member.value?.parent2Id != null) {
                addMessage.value = "Cannot add parent to the current member"
                return@launch
            }
            val memberPartner = familyTreeRepository.getMember(member.value?.spouses?.get(0)?.id!!).data
            if (memberPartner.parent1Id != null || memberPartner.parent2Id != null) {
                addMessage.value = "Cannot add parent to the current member"
                return@launch
            }

            if (imgUri != null) {
                val imgUrl = familyTreeRepository.uploadImage(imgUri).data
                parentMember.imageUrl = imgUrl
            }

            //familyTreeRepository.addParentMember(member.value?.id!!, parentMember)
            navigateToTreeMembers.value = true
            addMessage.value = "Added"
        }
    }

    fun addPartnerMember(partnerMember: Member, imgUri: Uri?) {
        viewModelScope.launch {
            // Check if already have partner
            if (member.value?.spouses?.isNotEmpty() == true) {
                addMessage.value = "Add multiple partner is not available on Android"
                return@launch
            }

            if (imgUri != null) {
                val imgUrl = familyTreeRepository.uploadImage(imgUri).data
                partnerMember.imageUrl = imgUrl
            }

            familyTreeRepository.addPartnerMember(member.value?.id!!, partnerMember)
            navigateToTreeMembers.value = true
        }
    }

    fun addChildMember(newChildMember: Member, imgUri: Uri?) {
        val fatherId: Int?
        val motherId: Int?
        if (member.value?.isMale == true) {
            fatherId = member.value?.id
            motherId = member.value?.spouses?.get(0)?.id
        } else {
            motherId = member.value?.id
            fatherId = member.value?.spouses?.get(0)?.id
        }

        viewModelScope.launch {
            if (imgUri != null) {
                val imgUrl = familyTreeRepository.uploadImage(imgUri).data
                newChildMember.imageUrl = imgUrl
            }

            familyTreeRepository.addChildMember(
                fatherId, motherId, newChildMember
            )
            navigateToTreeMembers.value = true
        }
    }

    fun doneNavigating() {
        this.navigateToTreeMembers.value = null
    }

    class Factory(val context: Context, val memberID: Int) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AddMemberViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return AddMemberViewModel(context, memberID) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}