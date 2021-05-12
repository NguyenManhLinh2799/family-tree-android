package com.example.familytree.my_trees

import android.view.*
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
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
        val moreBtn: ImageView = itemView.findViewById(R.id.moreBtn)

        fun bind(tree: Tree?) {
            name.text = tree?.name
            description.text = tree?.description
            itemView.setOnClickListener { view: View ->
                view.findNavController().navigate(MyTreesFragmentDirections.actionMyTreesFragmentToTreeMembersFragment())
            }
            setUpMoreBtn()
        }

        companion object {
            fun from(parent: ViewGroup): TreeViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.list_item_my_trees, parent, false)

                return TreeViewHolder(view)
            }
        }

        private fun setUpMoreBtn() {
            moreBtn.setOnClickListener {
                val popupMenu = PopupMenu(it.context, it, Gravity.END)
                val inflater = popupMenu.menuInflater
                inflater.inflate(R.menu.my_trees_list_item_menu, popupMenu.menu)

                popupMenu.setOnMenuItemClickListener { item: MenuItem? ->
                    when (item!!.itemId) {
                        R.id.deleteTree -> {
                            Toast.makeText(it.context, "${this.name.text} deleted", Toast.LENGTH_SHORT).show()
                        }
                    }
                    true
                }
                popupMenu.show()
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