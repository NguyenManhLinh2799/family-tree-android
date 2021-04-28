package com.example.familytree.member

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.familytree.network.FamilyTreeApi
import com.example.familytree.network.Person
import kotlinx.coroutines.launch
import java.lang.Exception

class MemberInfoViewModel: ViewModel() {
    var member = MutableLiveData<Person>()

    init {
        viewModelScope.launch {
            try {
                member.value = FamilyTreeApi.retrofitService.getPerson(1)
                Log.e("MemberInfoViewModel", member.value!!.fullName)
            } catch (e: Exception) {
                member.value = Person(
                        null,
                        0,
                        "abc",
                        "xyz",
                        "",
                        "",
                        0,
                        0,
                        0,
                        null,
                        null)
            }
        }
    }
}