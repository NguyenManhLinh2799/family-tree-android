package com.example.familytree.trees

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.familytree.databinding.FragmentSharedTreesBinding

class SharedTreesFragment : Fragment() {

    private lateinit var binding: FragmentSharedTreesBinding

    private val sharedTreesViewModel: SharedTreesViewModel by lazy {
        ViewModelProvider(this, SharedTreesViewModel.Factory(requireContext()))
            .get(SharedTreesViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentSharedTreesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedTreeAdapter = SharedTreeAdapter()
        binding.sharedTrees.adapter = sharedTreeAdapter

        sharedTreesViewModel.sharedTrees.observe(viewLifecycleOwner, {
            sharedTreeAdapter.submitList(it)
        })
    }
}