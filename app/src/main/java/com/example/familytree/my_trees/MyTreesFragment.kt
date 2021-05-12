package com.example.familytree.my_trees

import android.app.AlertDialog
import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.familytree.R
import com.example.familytree.databinding.FragmentMyTreesBinding
import com.example.familytree.network.Tree


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

        binding.addTree.setOnClickListener {
            showAddTreeDialog()
        }
    }

    private fun showAddTreeDialog() {
        val dialogBuilder = AlertDialog.Builder(context)
            .setTitle("Add a new family tree")
            .setView(R.layout.dialog_add_tree)
            .setPositiveButton("Add") { dialog, which ->
                val d = Dialog::class.java.cast(dialog)
                val name = d.findViewById<EditText>(R.id.dialogTreeName)?.text.toString()
                val description = d.findViewById<EditText>(R.id.dialogTreeDescription)?.text.toString()
                Toast.makeText(context, "$name added", Toast.LENGTH_SHORT).show()
                myTreesViewModel.addTree(name, description)

                // Refresh fragment
                val ft = fragmentManager!!.beginTransaction()
                if (Build.VERSION.SDK_INT >= 26) {
                    ft.setReorderingAllowed(false)
                }
                ft.detach(this).attach(this).commit()
            }
            .setNegativeButton("Cancel") { dialog, which -> dialog.cancel() }

        val dialog = dialogBuilder.create()
        dialog.show()
    }
}