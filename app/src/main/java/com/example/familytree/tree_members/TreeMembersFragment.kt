package com.example.familytree.tree_members

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.example.familytree.R
import com.example.familytree.network.Member
import me.jagar.mindmappingandroidlibrary.Views.Item
import me.jagar.mindmappingandroidlibrary.Views.ItemLocation
import me.jagar.mindmappingandroidlibrary.Views.ItemType
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

    private fun updateNodes(members: List<Member>) {
        // Remove all child views
        allNodes = emptyList()
        treeView.removeAllViews()

        // And update
        val me = Item(context, "Tôi", null, ItemType.MALE)
        val family1 = Item(context, "", null, ItemType.FAMILY)
        val wife = Item(context, "Vợ", null, ItemType.FEMALE)
        val daughter = Item(context, "Con gái", null, ItemType.FEMALE)
        val family2 = Item(context, "", null, ItemType.FAMILY)
        val sonInLaw = Item(context, "Con rể", null, ItemType.MALE)
        val grandSon = Item(context, "Cháu trai", null, ItemType.MALE)
        val father = Item(context, "Ba", null, ItemType.MALE)
        val family3 = Item(context, "", null, ItemType.FAMILY)
        val mother = Item(context, "Mẹ", null, ItemType.FEMALE)
        val sister = Item(context, "Chị", null, ItemType.FEMALE)
        val family4 = Item(context, "", null, ItemType.FAMILY)
        val brotherInLaw = Item(context, "Anh rể", null, ItemType.MALE)
        val sisterSon = Item(context, "Con trai chị", null, ItemType.MALE)
        val family5 = Item(context, "", null, ItemType.FAMILY)
        val grandFather = Item(context, "Ông nội", null, ItemType.MALE)
        val grandMother = Item(context, "Bà nội", null, ItemType.FEMALE)
        val uncle = Item(context, "Bác trai", null, ItemType.MALE)
        val family6 = Item(context, "", null, ItemType.FAMILY)
        val uncleWife = Item(context, "Vợ bác", null, ItemType.FEMALE)
        val uncleSon = Item(context, "Anh họ", null, ItemType.MALE)
        val family7 = Item(context, "", null, ItemType.FAMILY)
        val uncleSonWife = Item(context, "Vợ anh họ", null, ItemType.FEMALE)
        val nephew = Item(context, "Cháu họ", null, ItemType.MALE)

        allNodes = listOf(me, family1, wife, daughter, family2, sonInLaw, grandSon, father, family3, mother,
        sister, family4, brotherInLaw, sisterSon, family5, grandFather, grandMother, uncle, family6, uncleWife,
        uncleSon, family7, uncleSonWife, nephew)

        treeView.addCentralItem(me)
        treeView.addItem(family1, me, ItemLocation.RIGHT)
        treeView.addItem(wife, family1, ItemLocation.RIGHT)
        treeView.addItem(daughter, family1, ItemLocation.BOTTOM)
        treeView.addItem(family2, daughter, ItemLocation.LEFT)
        treeView.addItem(sonInLaw, family2, ItemLocation.LEFT)
        treeView.addItem(grandSon, family2, ItemLocation.BOTTOM)
        treeView.addItem(family3, me, ItemLocation.TOP)
        treeView.addItem(father, family3, ItemLocation.LEFT)
        treeView.addItem(mother, family3, ItemLocation.RIGHT)
        treeView.addItem(sister, family3, ItemLocation.BOTTOM)
        treeView.addItem(family4, sister, ItemLocation.LEFT)
        treeView.addItem(brotherInLaw, family4, ItemLocation.LEFT)
        treeView.addItem(sisterSon, family4, ItemLocation.BOTTOM)
        treeView.addItem(family5, father, ItemLocation.TOP)
        treeView.addItem(grandFather, family5, ItemLocation.LEFT)
        treeView.addItem(grandMother, family5, ItemLocation.RIGHT)
        treeView.addItem(uncle, family5, ItemLocation.BOTTOM)
        treeView.addItem(family6, uncle, ItemLocation.RIGHT)
        treeView.addItem(uncleWife, family6, ItemLocation.RIGHT)
        treeView.addItem(uncleSon, family6, ItemLocation.BOTTOM)
        treeView.addItem(family7, uncleSon, ItemLocation.RIGHT)
        treeView.addItem(uncleSonWife, family7, ItemLocation.RIGHT)
        treeView.addItem(nephew, family7, ItemLocation.BOTTOM)

        treeView.ReingoldTilford()

        allNodes.forEach {
            setStyle(it)
        }
    }

    private fun setStyle(item: Item) {
        if (item.type == ItemType.FAMILY) {
            return setFamilyStyle(item)
        }

        item.setBackgroundResource(when (item.type) {
            ItemType.MALE -> R.drawable.bg_male
            else -> R.drawable.bg_female
        })

        val image = ImageView(context)
        image.setImageResource(when (item.type) {
            ItemType.MALE -> R.drawable.ic_male
            else -> R.drawable.ic_female
        })
        image.setPadding(0, 0, 0, 20)
        item.addView(image, 0)

        item.gravity = Gravity.CENTER
        item.title.gravity = Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM
        item.removeView(item.content)

        item.setPadding(5, 5, 5, 5)

        item.setOnClickListener {
            memberMenuBar.visibility = when (memberMenuBar.visibility) {
                View.INVISIBLE -> View.VISIBLE
                View.VISIBLE -> View.INVISIBLE
                else -> View.VISIBLE
            }
        }
    }

    private fun setFamilyStyle(family: Item) {
        family.setBackgroundResource(R.drawable.ic_familynode)

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