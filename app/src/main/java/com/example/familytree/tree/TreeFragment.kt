package com.example.familytree.tree

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.navigation.fragment.findNavController
import com.example.familytree.R
import com.example.familytree.databinding.FragmentTreeBinding
import com.google.android.material.tabs.TabLayout
import me.jagar.mindmappingandroidlibrary.Views.Item

private const val TREE_ID = "treeID"

class TreeFragment : Fragment() {

    private lateinit var binding: FragmentTreeBinding
    private var treeID: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (treeID == null) {
            treeID = TreeFragmentArgs.fromBundle(arguments!!).treeID
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTreeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tabLayout = binding.tabLayout
        val viewPager = binding.viewPager
        val adapter = TreePagerAdapter(treeID!!, childFragmentManager)
        viewPager.adapter = adapter
        tabLayout.setupWithViewPager(viewPager)
    }

    fun navigateToMemberInfo(memberID: Int) {
        findNavController().navigate(
            TreeFragmentDirections.actionTreeFragmentToMemberInfoFragment(memberID)
        )
    }

    fun navigateToEditMember(memberID: Int) {
        findNavController().navigate(
            TreeFragmentDirections.actionTreeFragmentToEditMemberFragment(memberID)
        )
    }

    fun navigateToAddMember(memberID: Int) {
        findNavController().navigate(
            TreeFragmentDirections.actionTreeFragmentToAddMemberFragment(memberID, this.treeID!!)
        )
    }

    fun navigateToAddContributor() {
        findNavController().navigate(
            TreeFragmentDirections.actionTreeFragmentToAddContributorFragment(this.treeID!!)
        )
    }

    fun navigateToPostMemory() {
        findNavController().navigate(
            TreeFragmentDirections.actionTreeFragmentToPostMemoryFragment(this.treeID!!)
        )
    }

    class TreePagerAdapter(val treeID: Int, fm: FragmentManager): FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> TreeMembersFragment.newInstance(treeID)
                1 -> TreeContributorsFragment.newInstance(treeID)
                else -> TreeMemoriesFragment.newInstance(treeID)
            }
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return when(position) {
                0 -> "Family tree"
                1 -> "Contributors"
                else -> "Memories"
            }
        }

        override fun getCount(): Int {
            return 3
        }
    }
}