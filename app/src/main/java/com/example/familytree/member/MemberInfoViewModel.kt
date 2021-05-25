package com.example.familytree.member

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.familytree.database.getDatabase
import com.example.familytree.my_trees.MyTreesViewModel
import com.example.familytree.network.FamilyTreeApi
import com.example.familytree.network.Member
import com.example.familytree.repository.FamilyTreeRepository
import kotlinx.coroutines.launch
import java.lang.Exception
import java.lang.IllegalArgumentException

class MemberInfoViewModel(context: Context, memberID: Int) : ViewModel() {

    private val database = getDatabase(context)
    private val familyTreeRepository = FamilyTreeRepository(database)

    var member = MutableLiveData<Member>()

    init {
        viewModelScope.launch {
            try {
                member.value = familyTreeRepository.getMember(memberID).data
                Log.e("MemberInfoViewModel", member.value!!.fullName)
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

    class Factory(val context: Context, val memberID: Int) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MemberInfoViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MemberInfoViewModel(context, memberID) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}