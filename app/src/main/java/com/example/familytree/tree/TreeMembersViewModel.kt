package com.example.familytree.tree

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.familytree.database.getDatabase
import com.example.familytree.domain.ContributorList
import com.example.familytree.network.TreeMembers
import com.example.familytree.repository.FamilyTreeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.jagar.mindmappingandroidlibrary.Views.Item
import java.lang.Exception
import java.lang.IllegalArgumentException
import java.util.*
import kotlin.collections.ArrayList

class TreeMembersViewModel(context: Context, treeID: Int): ViewModel() {

    private val database = getDatabase(context)
    private val familyTreeRepository = FamilyTreeRepository(database)

    var treeMembers = MutableLiveData<TreeMembers>()
    var searchResult = MutableLiveData<List<Item>>()
    var selectedMember = MutableLiveData<Item>()

    var deleteSuccess = MutableLiveData<Boolean>()

    init {
        loadTreeMembers(treeID)
    }

    fun loadTreeMembers(treeID: Int) {
        viewModelScope.launch {
            try {
                treeMembers.value = familyTreeRepository.getTreeMembers(treeID).data!!
            } catch (e: Exception) {
                treeMembers.value = TreeMembers(0, "abc", "xyz", true, emptyList())
            }
        }
    }

    fun deleteMember(memberID: Int) {
        viewModelScope.launch {
            // Check if this member is deletable
            var isDeletable = true
            val member = familyTreeRepository.getMemberDetails(memberID)
            if (member.father != null && member.mother != null && member.spouses?.isNotEmpty() == true) {
                isDeletable = false
            }
            if (member.father == null && member.mother == null && member.children?.isNotEmpty() == true) {
                isDeletable = false
            }

            if (isDeletable) {
                familyTreeRepository.deleteMember(memberID)
                loadTreeMembers(treeMembers.value!!.id)
            }
            deleteSuccess.value = isDeletable
        }
    }

    fun search(query: String?, allNodes: List<Item>) {
        viewModelScope.launch {
            val result = ArrayList<Item>(0)
            if (query != null && query != "") {
                val queryUpper = query.toUpperCase(Locale.ROOT)
                for (node in allNodes) {
                    val nodeNameUpper = node.title.text.toString().toUpperCase(Locale.ROOT)
                    if (nodeNameUpper.contains(queryUpper)) {
                        result.add(node)
                    }
                }
            }
            searchResult.value = result
        }
    }

    fun select(member: Item?) {
        viewModelScope.launch {
            delay(500)
            selectedMember.value = member!!
        }
    }

    class Factory(val context: Context, val treeID: Int) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(TreeMembersViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return TreeMembersViewModel(context, treeID) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}