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
import com.example.familytree.DateHelper
import com.example.familytree.R
import com.example.familytree.domain.Memory

class MemoryAdapter(private val onItemClick: OnMemoryItemClick)
    : ListAdapter<Memory, MemoryAdapter.MemoryViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemoryViewHolder {
        return MemoryViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MemoryViewHolder, position: Int) {
        val memory = getItem(position)
        holder.bind(memory, onItemClick)
    }

    class MemoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val avatar: ImageView = itemView.findViewById(R.id.posterAvatar)
        val username: TextView = itemView.findViewById(R.id.posterUsername)
        val date: TextView = itemView.findViewById(R.id.memoryDate)
        val description: TextView = itemView.findViewById(R.id.memoryDescription)
        val deleteBtn: ImageView = itemView.findViewById(R.id.deleteMemoryBtn)
        fun bind(memory: Memory?, onItemClick: OnMemoryItemClick) {
            avatar.setPadding(20)
            date.text = DateHelper.isoToDate(memory?.memoryDate)
            description.text = memory?.description
            deleteBtn.setOnClickListener {
                onItemClick.onDeleteMemory(memory?.id)
            }
        }
        companion object {
            fun from(parent: ViewGroup): MemoryViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.list_item_memories, parent, false)
                return MemoryViewHolder(view)
            }
        }
    }

    interface OnMemoryItemClick {
        fun onDeleteMemory(memoryID: Int?)
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Memory>() {
        override fun areItemsTheSame(oldItem: Memory, newItem: Memory): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Memory, newItem: Memory): Boolean {
            return oldItem.id == newItem.id
        }
    }
}