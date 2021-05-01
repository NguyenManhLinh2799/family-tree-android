package com.example.familytree.tree_members

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.example.familytree.R
import com.example.familytree.network.Person
import me.jagar.mindmappingandroidlibrary.Views.Item
import me.jagar.mindmappingandroidlibrary.Views.ItemLocation
import me.jagar.mindmappingandroidlibrary.Views.MindMappingView

class TreeMembersFragment : Fragment() {

    private lateinit var treeView: MindMappingView
    private lateinit var memberMenuBar: LinearLayout
    private lateinit var allNodes: List<Item>
    private val treeMembersViewModel: TreeMembersViewModel by lazy {
        ViewModelProviders.of(this).get(TreeMembersViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_tree_members, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Menu bar
        setupMenuBar(view)

        treeView = view.findViewById(R.id.treeView)

        // Observe members
        treeMembersViewModel.treeMembers.observe(viewLifecycleOwner, {
            updateNodes(it.people)
        })
    }

    private fun updateNodes(members: List<Person>) {
        // Remove all child views
        allNodes = emptyList()
        treeView.removeAllViews()

        // And update
        val me = Item(context, "Tôi", null, false, false)
        val family = Item(context, "", null, false, true)
        val father = Item(context, "Ba", null, false, false)
        val mother = Item(context, "Mẹ", null, false, false)
        val sister = Item(context, "Chị", null, false, false)
        val brother = Item(context, "Anh", null, false, false)
        val young = Item(context, "Em", null, false, false)

        allNodes = listOf(me, family, father, mother, sister, brother, young)

        treeView.addCentralItem(me, false)
        treeView.addItem(family, me, 300, 300, ItemLocation.TOP, false, null)
        treeView.addItem(father, family, 200, 0, ItemLocation.LEFT, false, null)
        treeView.addItem(mother, family, 200, 0, ItemLocation.RIGHT, false, null)
        treeView.addItem(sister, family, 300, 300, ItemLocation.BOTTOM, false, null)
        treeView.addItem(brother, family, 300, 300, ItemLocation.BOTTOM, false, null)
        treeView.addItem(young, family, 300, 300, ItemLocation.BOTTOM, false, null)

        allNodes.forEachIndexed { index, node ->
            setStyle(node, true)
            node.setOnClickListener {
                memberMenuBar.visibility = when (memberMenuBar.visibility) {
                    View.INVISIBLE -> View.VISIBLE
                    View.VISIBLE -> View.INVISIBLE
                    else -> View.VISIBLE
                }
            }
        }
    }

    private fun setStyle(item: Item, isMale: Boolean) {
        if (item.isFamily) {
            return setFamilyStyle(item)
        }

        item.setBackgroundResource(when (isMale) {
            true -> R.drawable.bg_male
            else -> R.drawable.bg_female
        })

        val image = ImageView(context)
        image.setImageResource(when (isMale) {
            true -> R.drawable.ic_male
            else -> R.drawable.ic_female
        })
        image.setPadding(0, 0, 0, 20)
        item.addView(image, 0)

        item.gravity = Gravity.CENTER
        item.title.gravity = Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM
        item.removeView(item.content)

        item.setPadding(5, 5, 5, 5)
    }

    private fun setFamilyStyle(family: Item) {
        family.setBackgroundResource(R.drawable.ic_family_node)

        val params = family.layoutParams
        params.height = 228
        //params.width = 183
        family.layoutParams = params
    }

    private fun setupMenuBar(view: View) {
        memberMenuBar = view.findViewById(R.id.memberMenuBar)
        memberMenuBar.visibility = View.INVISIBLE

        // Navigate to member info
        val memberInfoBtn = view.findViewById<LinearLayout>(R.id.memberInfoBtn)
        memberInfoBtn.setOnClickListener {
            it.findNavController().navigate(TreeMembersFragmentDirections.actionTreeMembersFragmentToMemberInfoFragment())
        }

        // Navigate to add member
        val addMemberBtn = view.findViewById<LinearLayout>(R.id.addMemberBtn)
        addMemberBtn.setOnClickListener {
            it.findNavController().navigate(TreeMembersFragmentDirections.actionTreeMembersFragmentToAddMemberFragment())
        }

        // Navigate to edit member
        val editMemberBtn = view.findViewById<LinearLayout>(R.id.editMemberBtn)
        editMemberBtn.setOnClickListener {
            it.findNavController().navigate(TreeMembersFragmentDirections.actionTreeMembersFragmentToAddMemberFragment())
        }
    }
}