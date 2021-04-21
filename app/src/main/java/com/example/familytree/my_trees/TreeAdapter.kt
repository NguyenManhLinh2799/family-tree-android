package com.example.familytree.my_trees

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.familytree.my_trees.MyTreesFragmentDirections
import com.example.familytree.R

class TreeAdapter(private val treeList: List<String>) : RecyclerView.Adapter<TreeAdapter.TreeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TreeViewHolder {
        return TreeViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: TreeViewHolder, position: Int) {
        holder.bind(treeList[position])
    }

    override fun getItemCount() = treeList.size

    class TreeViewHolder private constructor (itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.treeName)

        fun bind(treeName: String) {
            name.text = treeName
            itemView.setOnClickListener { view: View ->
                view.findNavController().navigate(MyTreesFragmentDirections.actionMyTreesFragmentToTreeMembersFragment())
            }
        }

        companion object {
            fun from(parent: ViewGroup): TreeViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.list_item_my_trees, parent, false)

                return TreeViewHolder(view)
            }
        }
    }
}