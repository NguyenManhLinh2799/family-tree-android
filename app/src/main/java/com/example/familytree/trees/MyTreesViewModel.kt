package com.example.familytree.trees

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.familytree.database.getDatabase
import com.example.familytree.domain.Tree
import com.example.familytree.network.asDomainModel
import com.example.familytree.repository.FamilyTreeRepository
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class MyTreesViewModel(context: Context) : ViewModel() {

    private val database = getDatabase(context)
    private val familyTreeRepository = FamilyTreeRepository(database)

    var myTrees = MutableLiveData<List<Tree>>()

    init {
        viewModelScope.launch {
            try {
                myTrees.value = familyTreeRepository.getMyTrees()
            } catch (e: Exception) {
                myTrees.value = listOf(
                        Tree(1, "Tree1", "", null, null, null),
                        Tree(2, "Tree2", "", null, null, null),
                        Tree(3, "Tree3", "", null, null, null),
                        Tree(4, "Tree4", "", null, null, null),
                        Tree(5, "Tree5", "", null, null, null)
                )
            }
        }
    }

    fun addTree(name: String, description: String) {
        viewModelScope.launch {
            familyTreeRepository.addTree(name, description)
            myTrees.value = familyTreeRepository.getMyTrees()
        }
    }

    fun deleteTree(id: Int?) {
        viewModelScope.launch {
            familyTreeRepository.deleteTree(id)
        }
    }

    fun editTree(id: Int?, name: String, description: String) {
        viewModelScope.launch {
            familyTreeRepository.upadateTree(id, name, description)
            myTrees.value = familyTreeRepository.getMyTrees()
        }
    }

    class Factory(val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MyTreesViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MyTreesViewModel(context) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}