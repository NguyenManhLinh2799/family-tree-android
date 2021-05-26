package com.example.familytree.member

import android.content.Context
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

    init {
        Log.e("AddMemberViewModel", memberID.toString())
        viewModelScope.launch {
            try {
                member.value = familyTreeRepository.getMember(memberID).data!!
                Log.e("AddMemberViewModel", member.value!!.fullName)
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

    fun addChildMember(newChildMember: Member) {
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
            familyTreeRepository.addChildMember(
                fatherId, motherId, newChildMember
            )
        }
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