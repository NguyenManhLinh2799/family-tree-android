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

class SharedTreesViewModel(context: Context): ViewModel() {

    private val database = getDatabase(context)
    private val familyTreeRepository = FamilyTreeRepository(database)

    var sharedTrees = MutableLiveData<List<Tree>>()

    init {
        viewModelScope.launch {
            try {
                sharedTrees.value = familyTreeRepository.getSharedTrees()
            } catch (e: Exception) {
                sharedTrees.value = listOf(
                    Tree(1, "Tree1", "", null, null, null),
                    Tree(2, "Tree2", "", null, null, null),
                    Tree(3, "Tree3", "", null, null, null),
                    Tree(4, "Tree4", "", null, null, null),
                    Tree(5, "Tree5", "", null, null, null)
                )
            }
        }
    }

    class Factory(val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SharedTreesViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return SharedTreesViewModel(context) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}