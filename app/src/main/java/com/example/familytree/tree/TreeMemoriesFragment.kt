package com.example.familytree.tree

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.familytree.R
import com.example.familytree.databinding.FragmentTreeMemoriesBinding

private const val TREE_ID = "treeID"

class TreeMemoriesFragment : Fragment() {

    private lateinit var binding: FragmentTreeMemoriesBinding
    private var treeID: Int? = null
    private lateinit var treeMemoriesViewModel: TreeMemoriesViewModel
    private lateinit var treeFragment: TreeFragment

    private val onItemClick = object : MemoryAdapter.OnMemoryItemClick {
        override fun onDeleteMemory(memoryID: Int?) {
            treeMemoriesViewModel.deleteMemory(memoryID)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentTreeMemoriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        treeFragment = parentFragment as TreeFragment

        if (this.treeID == null) {
            arguments?.let {
                this.treeID = it.getInt(TREE_ID)
                treeMemoriesViewModel = ViewModelProvider(this,
                TreeMemoriesViewModel.Factory(
                    requireNotNull(context),
                    requireNotNull(this.treeID)
                )).get(TreeMemoriesViewModel::class.java)
            }
        } else {
            treeMemoriesViewModel.loadMemories(this.treeID!!)
        }

        val memoryAdapter = MemoryAdapter(onItemClick)
        binding.memories.adapter = memoryAdapter

        treeMemoriesViewModel.memoryList.observe(viewLifecycleOwner, {
            memoryAdapter.submitList(it)
        })

        binding.postMemory.setOnClickListener {
            treeFragment.navigateToPostMemory()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(treeID: Int) =
            TreeMemoriesFragment().apply {
                arguments = Bundle().apply {
                    putInt(TREE_ID, treeID)
                }
            }
    }
}