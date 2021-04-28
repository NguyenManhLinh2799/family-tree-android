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

        // Init dump item
        val family = Item(context, "", null, false)
        val me = Item(context, "Tôi", null, false)
        val mother = Item(context, "Mẹ", null, false)
        val father = Item(context, "Bố", null, false)

        allNodes = listOf(me, mother, father)

        // Add item into view
        treeView.addCentralItem(family, false)
        setFamilyStyle(family)
        treeView.addItem(me, family, 300, 0, ItemLocation.BOTTOM, false, null)
        treeView.addItem(mother, family, 100, 0, ItemLocation.RIGHT, false, null)
        treeView.addItem(father, family, 150, 0, ItemLocation.LEFT, false, null)

        // Custom member node
        allNodes.forEach { node ->
            setStyle(node, true)
            node.setOnClickListener {
                memberMenuBar.visibility = when (memberMenuBar.visibility) {
                    View.INVISIBLE -> View.VISIBLE
                    View.VISIBLE -> View.INVISIBLE
                    else -> View.VISIBLE
                }
            }
        }

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
        val family = Item(context, "", null, false)
        val me = Item(context, members[0].fullName, null, false)
        val mother = Item(context, members[1].fullName, null, false)
        val father = Item(context, members[2].fullName, null, false)

        allNodes = listOf(me, mother, father)

        treeView.addCentralItem(family, false); setFamilyStyle(family)
        treeView.addItem(me, family, 300, 0, ItemLocation.BOTTOM, false, null)
        treeView.addItem(mother, family, 100, 0, ItemLocation.RIGHT, false, null)
        treeView.addItem(father, family, 150, 0, ItemLocation.LEFT, false, null)

        allNodes.forEachIndexed { index, node ->
            setStyle(node, members[index].isMale)
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
        val params = family.layoutParams
        params.height = 225
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