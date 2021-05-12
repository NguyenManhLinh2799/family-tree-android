package com.example.familytree.my_trees

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.familytree.network.ApiResponse
import com.example.familytree.network.FamilyTreeApi
import com.example.familytree.network.Tree
import kotlinx.coroutines.launch

class MyTreesViewModel : ViewModel() {
    var myTrees = MutableLiveData<List<Tree>>()

    init {
        viewModelScope.launch {
            try {
                myTrees.value = FamilyTreeApi.retrofitService.getTrees().data
                Log.e("MyTreesViewModel", myTrees.value!![0].name)
            } catch (e: Exception) {
                myTrees.value = listOf(
                        Tree(1, "Tree1", ""),
                        Tree(2, "Tree2", ""),
                        Tree(3, "Tree3", ""),
                        Tree(4, "Tree4", ""),
                        Tree(5, "Tree5", ""))
            }
        }
    }

    fun addTree(name: String, description: String) {
        viewModelScope.launch {
            FamilyTreeApi.retrofitService.addTree(Tree(null, name, description))
        }
    }

    fun deleteTree(id: Int) {
        viewModelScope.launch {
            FamilyTreeApi.retrofitService.deleteTree(id)
        }
    }
}