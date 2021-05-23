package com.example.familytree.tree_members

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.familytree.network.FamilyTreeApi
import com.example.familytree.network.TreeMembers
import kotlinx.coroutines.launch
import java.lang.Exception

class TreeMembersViewModel: ViewModel() {
    var treeMembers = MutableLiveData<TreeMembers>()

    init {
        viewModelScope.launch {
            try {
                treeMembers.value = FamilyTreeApi.retrofitService.getTreeMembers(1).data
                //Log.e("TreeMembersViewModel", treeMembers.value!!.name)
                for (member in treeMembers.value!!.people) {
                    Log.e("TreeMembersViewModel", member.fullName)
                }
            } catch (e: Exception) {
                treeMembers.value = TreeMembers(0, "abc", "xyz", ArrayList())
            }
        }
    }
}