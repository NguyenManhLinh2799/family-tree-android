package com.example.familytree

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.example.familytree.databinding.FragmentMyTreesBinding

class MyTreesFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding: FragmentMyTreesBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_my_trees, container, false
        )

        val myTreesViewModel = ViewModelProviders.of(this).get(MyTreesViewModel::class.java)
        binding.myTreesViewModel = myTreesViewModel

        binding.treeList.adapter = TreeAdapter(myTreesViewModel.treeList)

        binding.lifecycleOwner = this

        return binding.root
    }
}