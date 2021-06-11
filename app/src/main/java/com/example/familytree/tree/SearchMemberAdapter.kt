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
import com.example.familytree.network.member.Member
import me.jagar.mindmappingandroidlibrary.Views.Item

class SearchMemberAdapter(private val onItemClick: OnSearchMemberItemClick) :
    ListAdapter<Item, SearchMemberAdapter.SearchMemberViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchMemberViewHolder {
        return SearchMemberViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: SearchMemberViewHolder, position: Int) {
        holder.bind(getItem(position), onItemClick)
    }

    class SearchMemberViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val fullName: TextView = itemView.findViewById(R.id.memberFullName)
        val avatar: ImageView = itemView.findViewById(R.id.memberAvatar)

        fun bind(member: Item?, onItemClick: OnSearchMemberItemClick) {
            avatar.load(member?.imgUrl)
            fullName.text = member?.title?.text

            itemView.setOnClickListener {
                onItemClick.onClick(member)
            }
        }

        companion object {
            fun from(parent: ViewGroup): SearchMemberViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val view = inflater.inflate(R.layout.list_item_search_members, parent, false)
                return SearchMemberViewHolder(view)
            }
        }
    }

    interface OnSearchMemberItemClick {
        fun onClick(member: Item?)
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Item>() {
        override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem.id == newItem.id
        }
    }
}