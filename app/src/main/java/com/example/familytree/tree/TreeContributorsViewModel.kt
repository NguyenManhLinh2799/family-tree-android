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
    private var treeID: Int? = null

    var contributorList = MutableLiveData<ContributorList>()

    init {
        this.treeID = treeID

        loadContributors(treeID)
    }

    fun loadContributors(treeID: Int) {
        viewModelScope.launch {
            contributorList.value = familyTreeRepository.getContributors(treeID)
        }
    }

    fun removeContributor(username: String?) {
        viewModelScope.launch {
            familyTreeRepository.removeContributor(treeID!!, username!!)
            loadContributors(treeID!!)
        }
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