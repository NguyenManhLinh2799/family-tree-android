package com.example.familytree.trees

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.familytree.R
import com.example.familytree.databinding.FragmentMyTreesBinding
import com.example.familytree.domain.Tree


class MyTreesFragment: Fragment() {

    private lateinit var binding: FragmentMyTreesBinding

    private val myTreesViewModel: MyTreesViewModel by lazy {
        ViewModelProvider(this, MyTreesViewModel.Factory(requireNotNull(context))).get(MyTreesViewModel::class.java)
    }

    private val onItemClick = object : TreeAdapter.OnTreeItemClick {
        override fun onDelete(id: Int?) {
            myTreesViewModel.deleteTree(id)
        }

        override fun onEdit(id: Int?, name: String, description: String) {
            myTreesViewModel.editTree(id, name, description)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentMyTreesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val treeAdapter = TreeAdapter(onItemClick)
        binding.myTrees.adapter = treeAdapter

        myTreesViewModel.myTrees.observe(viewLifecycleOwner, {
            treeAdapter.submitList(it as MutableList<Tree>?)
        })

        binding.addTree.setOnClickListener {
            showAddTreeDialog()
        }
    }

    private fun showAddTreeDialog() {
        val dialogBuilder = AlertDialog.Builder(context)
            .setTitle("Add a new family tree")
            .setView(R.layout.dialog_tree_form)
            .setPositiveButton("Add") { dialogInterface, which ->
                val dialog = Dialog::class.java.cast(dialogInterface)
                val name = dialog.findViewById<EditText>(R.id.dialogTreeName)?.text.toString()
                val description = dialog.findViewById<EditText>(R.id.dialogTreeDescription)?.text.toString()
                Toast.makeText(context, "$name added", Toast.LENGTH_SHORT).show()
                myTreesViewModel.addTree(name, description)
            }
            .setNegativeButton("Cancel") { dialogInterface, which -> dialogInterface.cancel() }

        val dialog = dialogBuilder.create()
        dialog.show()
    }
}