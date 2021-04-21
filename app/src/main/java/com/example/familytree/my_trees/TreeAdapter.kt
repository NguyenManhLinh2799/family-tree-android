package com.example.familytree.my_trees

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.familytree.my_trees.MyTreesFragmentDirections
import com.example.familytree.R
import com.example.familytree.network.Tree

class TreeAdapter : ListAdapter<Tree, TreeAdapter.TreeViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TreeViewHolder {
        return TreeViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: TreeViewHolder, position: Int) {
        val tree = getItem(position)
        holder.bind(tree)
    }

    class TreeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.treeName)
        val description: TextView = itemView.findViewById(R.id.treeDescription)

        fun bind(tree: Tree?) {
            name.text = tree?.name
            description.text = tree?.description
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

    companion object DiffCallback : DiffUtil.ItemCallback<Tree>() {
        override fun areItemsTheSame(oldItem: Tree, newItem: Tree): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Tree, newItem: Tree): Boolean {
            return oldItem.id == newItem.id
        }
    }
}