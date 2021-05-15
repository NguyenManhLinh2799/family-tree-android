package com.example.familytree.my_trees

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.familytree.database.getDatabase
import com.example.familytree.network.FamilyTreeApi
import com.example.familytree.network.NetworkTree
import com.example.familytree.repository.FamilyTreeRepository
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class MyTreesViewModel(context: Context) : ViewModel() {

    private val database = getDatabase(context)
    private val familyTreeRepository = FamilyTreeRepository(database)
    init {
        viewModelScope.launch {
            familyTreeRepository.refreshTrees()
        }
    }
    val myTrees = familyTreeRepository.trees

//    var myTrees = MutableLiveData<List<NetworkTree>>()
//
//    init {
//        viewModelScope.launch {
//            try {
//                myTrees.value = FamilyTreeApi.retrofitService.getTrees().data
//                Log.e("MyTreesViewModel", myTrees.value!![0].name)
//            } catch (e: Exception) {
//                myTrees.value = listOf(
//                        NetworkTree(1, "Tree1", ""),
//                        NetworkTree(2, "Tree2", ""),
//                        NetworkTree(3, "Tree3", ""),
//                        NetworkTree(4, "Tree4", ""),
//                        NetworkTree(5, "Tree5", ""))
//            }
//        }
//    }

    fun addTree(name: String, description: String) {
        viewModelScope.launch {
            familyTreeRepository.addTree(name, description)
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