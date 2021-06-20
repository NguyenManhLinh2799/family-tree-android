package com.example.familytree.tree

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.familytree.database.getDatabase
import com.example.familytree.domain.User
import com.example.familytree.repository.FamilyTreeRepository
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class AddContributorViewModel(context: Context, treeID: Int): ViewModel() {

    private val database = getDatabase(context)
    private val familyTreeRepository = FamilyTreeRepository(database)
    private var treeID: Int? = null

    private lateinit var notContributors: ArrayList<User>
    var searchResult = MutableLiveData<ArrayList<User>>()
    var searchQuery = ""

    init {
        this.treeID = treeID
    }

    fun search(query: String?) {
        viewModelScope.launch {
            if (query != null) {
                searchQuery = query
            }
            // Find all users with provided query
            notContributors = if (query == "") {
                familyTreeRepository.filterUsers(null) as ArrayList<User>
            } else {
                familyTreeRepository.filterUsers(query) as ArrayList<User>
            }

            // Remove users that have been contributor
            val contributorList = familyTreeRepository.getContributors(treeID!!)
            val allContributors = ArrayList<User>(0)
            allContributors.add(contributorList.owner)
            if (contributorList.editors != null) {
                for (editor in contributorList.editors){
                    allContributors.add(editor)
                }
            }
            notContributors.removeAll(allContributors)
            // Update search result
            searchResult.value = notContributors
        }
    }

    fun addContributor(username: String?) {
        viewModelScope.launch {
            familyTreeRepository.addContributor(treeID!!, username!!)
            search(searchQuery)
        }
    }

    class Factory(val context: Context, val treeID: Int) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AddContributorViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return AddContributorViewModel(context, treeID) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}