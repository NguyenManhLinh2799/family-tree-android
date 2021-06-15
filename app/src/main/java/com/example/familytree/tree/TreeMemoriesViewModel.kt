package com.example.familytree.tree

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.familytree.database.getDatabase
import com.example.familytree.domain.Memory
import com.example.familytree.repository.FamilyTreeRepository
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class TreeMemoriesViewModel(context: Context, treeID: Int) : ViewModel() {

    private val database = getDatabase(context)
    private val familyTreeRepository = FamilyTreeRepository(database)
    private var treeID: Int? = null

    var memoryList = MutableLiveData<List<Memory>>()

    init {
        this.treeID = treeID
        loadMemories(treeID)
    }

    fun loadMemories(treeID: Int) {
        viewModelScope.launch {
            memoryList.value = familyTreeRepository.getMemories(treeID)
        }
    }

    fun deleteMemory(memoryID: Int?) {
        viewModelScope.launch {
            familyTreeRepository.deleteMemory(memoryID)
            memoryList.value = familyTreeRepository.getMemories(treeID!!)
        }
    }

    class Factory(val context: Context, val treeID: Int) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(TreeMemoriesViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return TreeMemoriesViewModel(context, treeID) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}