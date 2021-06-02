package com.example.familytree.tree

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.familytree.database.getDatabase
import com.example.familytree.domain.ContributorList
import com.example.familytree.domain.User
import com.example.familytree.repository.FamilyTreeRepository
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class TreeContributorsViewModel(context: Context, treeID: Int): ViewModel() {

    private val database = getDatabase(context)
    private val familyTreeRepository = FamilyTreeRepository(database)

    var contributorList = MutableLiveData<ContributorList>()
    var editorList = MutableLiveData<List<User>>() // Temp

    init {
        loadContributors(treeID)
    }

    fun loadContributors(treeID: Int) {
        viewModelScope.launch {
            contributorList.value = familyTreeRepository.getContributors(treeID)
            Log.e("TreeMembersViewModel", contributorList.value!!.owner.userName)
            editorList.value = listOf(
                contributorList.value!!.owner,
                contributorList.value!!.owner,
                contributorList.value!!.owner,
            )
        }
    }

    fun removeContributor(id: String?) {

    }

    class Factory(val context: Context, val treeID: Int) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(TreeContributorsViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return TreeContributorsViewModel(context, treeID) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}