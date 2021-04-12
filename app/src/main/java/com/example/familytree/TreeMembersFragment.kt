package com.example.familytree

import android.location.Location
import android.opengl.Visibility
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import me.jagar.mindmappingandroidlibrary.Views.Item
import me.jagar.mindmappingandroidlibrary.Views.ItemLocation
import me.jagar.mindmappingandroidlibrary.Views.MindMappingView

class TreeMembersFragment: Fragment() {

    private lateinit var allMembers: List<Item>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_tree_members, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val treeView = view.findViewById<MindMappingView>(R.id.treeView)

        val father = Item(this.context, "Bố", "", true)
        val mother = Item(this.context, "Mẹ", "", true)
        val me = Item(this.context, "Tôi", "", true)
        val brother = Item(this.context, "Em trai", "", true)

        allMembers = listOf(
                father,
                mother,
                me,
                brother
        )

        treeView?.addCentralItem(father, false)
        treeView.addItem(mother, father, 200, 200, ItemLocation.RIGHT, false, null)
        treeView.addItem(me, father, 200, 200, ItemLocation.BOTTOM, false, null)
        treeView.addItem(brother, father, 200, 200, ItemLocation.BOTTOM, false, null)

        val memberInfoBtn = view.findViewById<LinearLayout>(R.id.memberInfoBtn)
        memberInfoBtn.setOnClickListener {
            it.findNavController().navigate(TreeMembersFragmentDirections.actionTreeMembersFragmentToMemberInfoFragment())
        }

        val memberMenuBar = view.findViewById<LinearLayout>(R.id.memberMenuBar)
        memberMenuBar.visibility = View.INVISIBLE
        allMembers.forEach { member ->
            member.setOnClickListener {
                memberMenuBar?.visibility = when (memberMenuBar.visibility) {
                    View.INVISIBLE -> View.VISIBLE
                    View.VISIBLE -> View.INVISIBLE
                    else -> View.VISIBLE
                }
            }
        }
    }
}