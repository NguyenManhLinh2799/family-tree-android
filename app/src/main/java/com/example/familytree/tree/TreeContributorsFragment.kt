package com.example.familytree.tree

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.familytree.R
import com.example.familytree.databinding.FragmentTreeContributorsBinding

private const val TREE_ID = "treeID"

class TreeContributorsFragment : Fragment() {

    private lateinit var binding: FragmentTreeContributorsBinding
    private var treeID: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            treeID = it.getInt(TREE_ID)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentTreeContributorsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.textView.text = treeID.toString()
    }

    companion object {
        @JvmStatic
        fun newInstance(treeID: Int) =
            TreeContributorsFragment().apply {
                arguments = Bundle().apply {
                    putInt(TREE_ID, treeID)
                }
            }
    }
}