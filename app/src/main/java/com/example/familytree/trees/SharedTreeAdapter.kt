package com.example.familytree.trees

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.familytree.R
import com.example.familytree.domain.Tree

class SharedTreeAdapter : ListAdapter<Tree, SharedTreeAdapter.SharedTreeViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SharedTreeViewHolder {
        return SharedTreeViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: SharedTreeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class SharedTreeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.treeName)
        val description: TextView = itemView.findViewById(R.id.treeDescription)
        val owner: TextView = itemView.findViewById(R.id.treeOwner)

        fun bind(tree: Tree?) {
            name.text = tree?.name
            description.text = tree?.description
            owner.text = "Owner: ${tree?.owner?.username}"
            itemView.setOnClickListener {
                Log.e("SharedTreeAdapter", tree!!.id.toString())
                it.findNavController().navigate(
                    SharedTreesFragmentDirections.actionSharedTreesFragmentToTreeFragment(tree.id!!)
                )
            }
        }

        companion object {
            fun from(parent: ViewGroup): SharedTreeViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.list_item_shared_trees, parent, false)
                return SharedTreeViewHolder(view)
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