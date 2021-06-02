package com.example.familytree.tree

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.familytree.R
import com.example.familytree.domain.User

class ContributorAdapter(private val onItemCLick: OnContributorItemClick)
    : ListAdapter<User, ContributorAdapter.ContributorViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContributorViewHolder {
        return ContributorViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ContributorViewHolder, position: Int) {
        val contributor = getItem(position)
        holder.bind(contributor, onItemCLick)
    }

    class ContributorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val username: TextView = itemView.findViewById(R.id.contributorUsername)
        val email: TextView = itemView.findViewById(R.id.contributorEmail)
        val removeBtn: ImageView = itemView.findViewById(R.id.removeContributorBtn)
        fun bind(contributor: User?, onItemCLick: OnContributorItemClick) {
            username.text = contributor?.userName
            email.text = contributor?.email
            removeBtn.setOnClickListener {
                onItemCLick.onRemove(contributor?.id)
            }
        }
        companion object {
            fun from(parent: ViewGroup): ContributorViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.list_item_contributors, parent, false)
                return ContributorViewHolder(view)
            }
        }
    }

    interface OnContributorItemClick {
        fun onRemove(id: String?)
    }

    companion object DiffCallback : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.id == newItem.id
        }
    }
}