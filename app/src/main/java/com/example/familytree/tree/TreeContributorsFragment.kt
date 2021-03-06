package com.example.familytree.tree

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.setPadding
import androidx.lifecycle.ViewModelProvider
import coil.load
import com.example.familytree.databinding.FragmentTreeContributorsBinding

private const val TREE_ID = "treeID"

class TreeContributorsFragment : Fragment() {

    private lateinit var binding: FragmentTreeContributorsBinding
    private var treeID: Int? = null
    private lateinit var treeContributorsViewModel: TreeContributorsViewModel
    private lateinit var treeFragment: TreeFragment

    private val onItemClick = object : ContributorAdapter.OnContributorItemClick {
        override fun onRemoveContributor(username: String?) {
            treeContributorsViewModel.removeContributor(username)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentTreeContributorsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        treeFragment = parentFragment as TreeFragment

        if (this.treeID == null) {
            arguments?.let {
                this.treeID = it.getInt(TREE_ID)
                treeContributorsViewModel = ViewModelProvider(this,
                TreeContributorsViewModel.Factory(
                    requireNotNull(context),
                    requireNotNull(this.treeID)
                )).get(TreeContributorsViewModel::class.java)
            }
        } else {
            treeContributorsViewModel.loadContributors(this.treeID!!)
        }

        val ownerAvatar = binding.ownerAvatar
        val ownerUsername = binding.ownerUsername
        val ownerEmail = binding.ownerEmail

        val contributors = binding.contributors
        val addContributor = binding.addContributor

        treeContributorsViewModel.contributorList.observe(viewLifecycleOwner, {
            if (it.owned == true) {
                addContributor.setOnClickListener {
                    treeFragment.navigateToAddContributor()
                }
            } else if (it.owned == false) {
                addContributor.visibility = View.INVISIBLE
            }

            if (it.owner.avatarUrl != null) {
                ownerAvatar.load(it.owner.avatarUrl)
            } else {
                ownerAvatar.setPadding(20)
            }
            ownerUsername.text = it.owner.userName
            ownerEmail.text = it.owner.email

            val contributorAdapter = ContributorAdapter(onItemClick, it.owned!!)
            contributors.adapter = contributorAdapter
            contributorAdapter.submitList(it.editors)
        })
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