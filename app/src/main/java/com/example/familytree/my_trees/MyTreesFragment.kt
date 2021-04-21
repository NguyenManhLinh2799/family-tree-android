package com.example.familytree.my_trees

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.familytree.R
import com.example.familytree.databinding.FragmentMyTreesBinding
import com.example.familytree.network.Tree
import kotlinx.android.synthetic.main.fragment_my_trees.*

class MyTreesFragment: Fragment() {

    private lateinit var binding: FragmentMyTreesBinding

    private val myTreesViewModel: MyTreesViewModel by lazy {
        ViewModelProviders.of(this).get(MyTreesViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_my_trees, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.myTreesViewModel = myTreesViewModel

        val treeAdapter = TreeAdapter()
        binding.myTrees.adapter = treeAdapter
        binding.lifecycleOwner = viewLifecycleOwner

        myTreesViewModel.myTrees.observe(viewLifecycleOwner, {
            treeAdapter.submitList(it as MutableList<Tree>?)
        })
    }
}