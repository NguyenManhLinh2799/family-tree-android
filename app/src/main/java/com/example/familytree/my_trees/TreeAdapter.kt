package com.example.familytree.my_trees

import android.app.AlertDialog
import android.app.Dialog
import android.view.*
import android.widget.*
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.familytree.R
import com.example.familytree.domain.Tree
import com.example.familytree.network.NetworkTree

class TreeAdapter(private val onItemClick: OnItemClick) : ListAdapter<Tree, TreeAdapter.TreeViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TreeViewHolder {
        return TreeViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: TreeViewHolder, position: Int) {
        val tree = getItem(position)
        holder.bind(tree, onItemClick)
    }

    class TreeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.treeName)
        val description: TextView = itemView.findViewById(R.id.treeDescription)
        val moreBtn: ImageView = itemView.findViewById(R.id.moreBtn)

        fun bind(tree: Tree?, onItemClick: OnItemClick) {
            name.text = tree?.name
            description.text = tree?.description
            itemView.setOnClickListener { view: View ->
                view.findNavController().navigate(MyTreesFragmentDirections.actionMyTreesFragmentToTreeMembersFragment())
            }
            setUpMoreBtn(tree?.id, onItemClick)
        }

        companion object {
            fun from(parent: ViewGroup): TreeViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.list_item_my_trees, parent, false)

                return TreeViewHolder(view)
            }
        }

        private fun setUpMoreBtn(id: Int?, onItemClicked: OnItemClick) {
            moreBtn.setOnClickListener {
                val popupMenu = PopupMenu(it.context, it, Gravity.END)
                val inflater = popupMenu.menuInflater
                inflater.inflate(R.menu.my_trees_list_item_menu, popupMenu.menu)

                popupMenu.setOnMenuItemClickListener { item: MenuItem? ->
                    when (item!!.itemId) {
                        R.id.deleteTree -> {
                            Toast.makeText(it.context, "${this.name.text} deleted", Toast.LENGTH_SHORT).show()
                            onItemClicked.onDelete(id)
                        }
                        R.id.editTree -> {
                            showEditTreeDialog(id, onItemClicked)
                        }
                    }
                    true
                }
                popupMenu.show()
            }
        }

        private fun showEditTreeDialog(id: Int?, onItemClick: OnItemClick) {
            val dialogBuilder = AlertDialog.Builder(itemView.context)
                    .setTitle("Edit family tree")
                    .setView(R.layout.dialog_tree_form)
                    .setPositiveButton("Save") { dialog, which ->
                        val d = Dialog::class.java.cast(dialog)
                        val name = d.findViewById<EditText>(R.id.dialogTreeName)?.text.toString()
                        val description = d.findViewById<EditText>(R.id.dialogTreeDescription)?.text.toString()
                        Toast.makeText(itemView.context, "Edited", Toast.LENGTH_SHORT).show()
                        onItemClick.onEdit(id, name, description)
                    }
                    .setNegativeButton("Cancel") { dialog, which -> dialog.cancel() }

            val dialog = dialogBuilder.create()

            dialog.show()
            dialog.findViewById<EditText>(R.id.dialogTreeName)?.setText(this.name.text.toString())
            dialog.findViewById<EditText>(R.id.dialogTreeDescription)?.setText(this.description.text.toString())
        }
    }

    interface OnItemClick {
        fun onDelete(id: Int?)
        fun onEdit(id: Int?, name: String, description: String)
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