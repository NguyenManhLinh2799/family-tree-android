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

class UserAdapter(private val onItemCLick: OnUserItemClick) :
    ListAdapter<User, UserAdapter.UserViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        return UserViewHolder.form(parent)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = getItem(position)
        holder.bind(user, onItemCLick)
    }


    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val avatar: ImageView = itemView.findViewById(R.id.avatar)
        val username: TextView = itemView.findViewById(R.id.username)
        val email: TextView = itemView.findViewById(R.id.email)
        val addBtn: ImageView = itemView.findViewById(R.id.addBtn)
        fun bind(user: User?, onItemCLick: OnUserItemClick) {
            if (user?.avatarUrl != null) {
                avatar.load(user.avatarUrl)
            } else {
                avatar.setPadding(20)
            }
            username.text = user?.userName
            email.text = user?.email
            addBtn.setOnClickListener {
                onItemCLick.onAddContributor(user?.userName)
            }
        }
        companion object {
            fun form(parent: ViewGroup): UserViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.list_item_users, parent, false)
                return UserViewHolder(view)
            }
        }
    }

    interface OnUserItemClick {
        fun onAddContributor(username: String?)
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