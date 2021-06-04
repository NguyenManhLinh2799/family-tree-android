package com.example.familytree.tree

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import com.example.familytree.databinding.FragmentAddContributorBinding

class AddContributorFragment : Fragment() {

    private lateinit var binding: FragmentAddContributorBinding
    private var treeID: Int? = null
    private lateinit var addContributorsViewModel: AddContributorViewModel

    private val onItemClick = object : UserAdapter.OnUserItemClick {
        override fun onAddContributor(username: String?) {
            addContributorsViewModel.addContributor(username)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentAddContributorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.treeID = AddContributorFragmentArgs.fromBundle(arguments!!).treeID

        addContributorsViewModel = ViewModelProvider(this,
            AddContributorViewModel.Factory(
                requireNotNull(context),
                requireNotNull(this.treeID))
        ).get(AddContributorViewModel::class.java)

        val userAdapter = UserAdapter(onItemClick)
        binding.usersFound.adapter = userAdapter

        addContributorsViewModel.searchResult.observe(viewLifecycleOwner, {
            userAdapter.submitList(it)
        })

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                addContributorsViewModel.search(newText)
                return false
            }
        })
    }
}