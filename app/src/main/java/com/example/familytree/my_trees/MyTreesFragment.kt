package com.example.familytree.my_trees

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.familytree.R
import com.example.familytree.databinding.FragmentMyTreesBinding

class MyTreesFragment: Fragment() {

    private lateinit var binding: FragmentMyTreesBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_my_trees, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val myTreesViewModel = ViewModelProviders.of(this).get(MyTreesViewModel::class.java)
        binding.myTreesViewModel = myTreesViewModel
        binding.treeList.adapter = TreeAdapter(myTreesViewModel.treeList)
        binding.lifecycleOwner = viewLifecycleOwner
    }
}