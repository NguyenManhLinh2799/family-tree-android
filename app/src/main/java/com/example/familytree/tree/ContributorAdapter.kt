package com.example.familytree.tree

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.setPadding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.familytree.R
import com.example.familytree.domain.User

class ContributorAdapter(private val onItemCLick: OnContributorItemClick, owned: Boolean)
    : ListAdapter<User, ContributorAdapter.ContributorViewHolder>(DiffCallback) {

    private val owned = owned

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContributorViewHolder {
        return ContributorViewHolder.from(parent, owned)
    }

    override fun onBindViewHolder(holder: ContributorViewHolder, position: Int) {
        val contributor = getItem(position)
        holder.bind(contributor, onItemCLick)
    }

    class ContributorViewHolder(itemView: View, owned: Boolean) : RecyclerView.ViewHolder(itemView) {
        val avatar: ImageView = itemView.findViewById(R.id.contributorAvatar)
        val username: TextView = itemView.findViewById(R.id.contributorUsername)
        val email: TextView = itemView.findViewById(R.id.contributorEmail)
        val removeBtn: ImageView = itemView.findViewById(R.id.removeContributorBtn)
        val owned = owned

        fun bind(contributor: User?, onItemCLick: OnContributorItemClick) {
            if (contributor?.avatarUrl != null) {
                avatar.load(contributor.avatarUrl)
            } else {
                avatar.setPadding(20)
            }
            username.text = contributor?.userName
            email.text = contributor?.email

            if (owned) {
                removeBtn.setOnClickListener {
                    onItemCLick.onRemoveContributor(contributor?.userName)
                }
            } else {
                removeBtn.visibility = View.INVISIBLE
            }
        }

        companion object {
            fun from(parent: ViewGroup, owned: Boolean): ContributorViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.list_item_contributors, parent, false)
                return ContributorViewHolder(view, owned)
            }
        }
    }

    interface OnContributorItemClick {
        fun onRemoveContributor(username: String?)
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