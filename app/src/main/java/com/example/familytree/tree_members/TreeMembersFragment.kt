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
import androidx.navigation.findNavController
import com.example.familytree.R
import com.example.familytree.databinding.FragmentTreeMembersBinding
import com.example.familytree.network.member.Member
import me.jagar.mindmappingandroidlibrary.Views.Item
import me.jagar.mindmappingandroidlibrary.Views.ItemLocation
import me.jagar.mindmappingandroidlibrary.Views.ItemType
import me.jagar.mindmappingandroidlibrary.Views.MindMappingView

class TreeMembersFragment : Fragment() {

    private lateinit var binding: FragmentTreeMembersBinding

    private lateinit var treeView: MindMappingView
    private lateinit var memberMenuBar: LinearLayout
    private lateinit var allNodes: List<Item>

    private var treeID: Int? = null

//    private var treeMembersViewModel: TreeMembersViewModel by lazy {
//        ViewModelProvider(this,
//            TreeMembersViewModel.Factory(
//                requireNotNull(context),
//                requireNotNull(TreeMembersFragmentArgs.fromBundle(arguments!!).treeID)
//            ))
//            .get(TreeMembersViewModel::class.java)
//    }

    private lateinit var treeMembersViewModel: TreeMembersViewModel

    private var added = ArrayList<Item>(0)
    private var notYetAdded = ArrayList<Member>(0)

    private var focusedNode: Item? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentTreeMembersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (this.treeID == null) {
            this.treeID = TreeMembersFragmentArgs.fromBundle(arguments!!).treeID
            treeMembersViewModel = ViewModelProvider(this,
                TreeMembersViewModel.Factory(
                    requireNotNull(context),
                    requireNotNull(this.treeID)
                ))
                .get(TreeMembersViewModel::class.java)
        } else {
            treeMembersViewModel.loadTreeMembers(this.treeID!!)
        }

        // Menu bar
        setupMenuBar()

        treeView = binding.treeView

        // Observe members
        treeMembersViewModel.treeMembers.observe(viewLifecycleOwner, {
            updateAllNodes(it.people)
        })
    }

    private fun updateAllNodes(members: List<Member>) {

        // Remove all child views
        allNodes = emptyList()
        added.clear()
        notYetAdded.clear()
        treeView.removeAllViews()

        // And update
        //fakeData()
        addAllMembers(members)
        allNodes = added

        treeView.ReingoldTilford()
        
        allNodes.forEach {
            setStyle(it)
        }
    }

    private fun addAllMembers(members: List<Member>) {
        notYetAdded = members as ArrayList<Member>
        // Find the root member
        var rootMem: Member? = null
        for (mem in notYetAdded) {
            if (mem.parent1Id == null && mem.parent2Id == null) {
                if (mem.spouses?.isNotEmpty() == true) {
                    if (mem.spouses[0].parent1Id == null && mem.spouses[0].parent2Id == null) {
                        rootMem = mem
                        break
                    }
                } else {
                    rootMem = mem
                    break
                }
            }
        }

        // Create central item
        if (rootMem != null) {
            val rootItem = Item(context, rootMem.id!!, rootMem.fullName, null,
            when (rootMem.isMale) {
                true -> ItemType.MALE
                else -> ItemType.FEMALE
            })
            treeView.addCentralItem(rootItem)
            added.add(rootItem)
            notYetAdded.remove(rootMem)

            // Find and add partner (if any)
            var familyItem: Item? = null
            if (rootMem.spouses?.isNotEmpty() == true) {
                for (mem in notYetAdded) {
                    if (mem.id == rootMem.spouses!![0].id) {
                        familyItem = Item(context, 0, "", null, ItemType.FAMILY)
                        if (mem.isMale) {
                            val partnerItem = Item(context, mem.id!!, mem.fullName, null, ItemType.MALE)
                            treeView.addItem(familyItem, rootItem, ItemLocation.LEFT)
                            treeView.addItem(partnerItem, familyItem, ItemLocation.LEFT)
                            added.add(partnerItem)
                        } else {
                            val partnerItem = Item(context, mem.id!!, mem.fullName, null, ItemType.FEMALE)
                            treeView.addItem(familyItem, rootItem, ItemLocation.RIGHT)
                            treeView.addItem(partnerItem, familyItem, ItemLocation.RIGHT)
                            added.add(partnerItem)
                        }
                        treeView.setRoot(familyItem)
                        added.add(familyItem)
                        notYetAdded.remove(mem)
                        break
                    }
                }
            }

            // Find and add children
            val childrenMem = ArrayList<Member>(0)
            for (mem in notYetAdded) {
                if (rootMem.isMale) {
                    if (mem.parent1Id == rootMem.id) {
                        childrenMem.add(mem)
                    }
                } else {
                    if (mem.parent2Id == rootMem.id) {
                        childrenMem.add(mem)
                    }
                }
            }
            for (childMem in childrenMem) {

                val childItem = Item(context, childMem.id!!, childMem.fullName, null,
                when (childMem.isMale) {
                    true -> ItemType.MALE
                    else -> ItemType.FEMALE
                })
                if (familyItem != null) {
                    treeView.addItem(childItem, familyItem, ItemLocation.BOTTOM)
                } else {
                    treeView.addItem(childItem, rootItem, ItemLocation.BOTTOM)
                }
                // Recursive
                add(childMem, childItem)
            }
        }
    }

    private fun add(member: Member, item: Item) {
        added.add(item)
        notYetAdded.remove(member)

        // Find and add partner (if any)
        var familyItem: Item? = null
        if (member.spouses?.isNotEmpty() == true) {
            for (mem in notYetAdded) {
                if (mem.id == member.spouses[0].id) {
                    familyItem = Item(context, 0, "", null, ItemType.FAMILY)
                    if (mem.isMale) {
                        val partnerItem = Item(context, mem.id!!, mem.fullName, null, ItemType.MALE)
                        treeView.addItem(familyItem, item, ItemLocation.LEFT)
                        treeView.addItem(partnerItem, familyItem, ItemLocation.LEFT)
                        added.add(partnerItem)
                    } else {
                        val partnerItem = Item(context, mem.id!!, mem.fullName, null, ItemType.FEMALE)
                        treeView.addItem(familyItem, item, ItemLocation.RIGHT)
                        treeView.addItem(partnerItem, familyItem, ItemLocation.RIGHT)
                        added.add(partnerItem)
                    }
                    added.add(familyItem)
                    notYetAdded.remove(mem)
                    break
                }
            }
        }

        val childrenMem = ArrayList<Member>(0)
        for (mem in notYetAdded) {
            if (member.isMale) {
                if (mem.parent1Id == member.id) {
                    childrenMem.add(mem)
                }
            } else {
                if (mem.parent2Id == member.id) {
                    childrenMem.add(mem)
                }
            }
        }
        for (childMem in childrenMem) {
            val childItem = Item(context, childMem.id!!, childMem.fullName, null,
                when (childMem.isMale) {
                    true -> ItemType.MALE
                    else -> ItemType.FEMALE
                })
            if (familyItem != null) {
                treeView.addItem(childItem, familyItem, ItemLocation.BOTTOM)
            } else {
                treeView.addItem(childItem, item, ItemLocation.BOTTOM)
            }
            // Recursive
            add(childMem, childItem)
        }
    }

    private fun fakeData() {
        val me = Item(context, 0, "Tôi", null, ItemType.MALE)
        val family1 = Item(context, 0, "", null, ItemType.FAMILY)
        val wife = Item(context, 0, "Vợ", null, ItemType.FEMALE)
        val daughter = Item(context, 0, "Con gái", null, ItemType.FEMALE)
        val family2 = Item(context, 0, "", null, ItemType.FAMILY)
        val sonInLaw = Item(context, 0, "Con rể", null, ItemType.MALE)
        val grandSon = Item(context, 0, "Cháu trai", null, ItemType.MALE)
        val father = Item(context, 0, "Ba", null, ItemType.MALE)
        val family3 = Item(context, 0, "", null, ItemType.FAMILY)
        val mother = Item(context, 0, "Mẹ", null, ItemType.FEMALE)
        val sister = Item(context, 0, "Chị", null, ItemType.FEMALE)
        val family4 = Item(context, 0, "", null, ItemType.FAMILY)
        val brotherInLaw = Item(context, 0, "Anh rể", null, ItemType.MALE)
        val sisterSon = Item(context, 0, "Con trai chị", null, ItemType.MALE)
        val family5 = Item(context, 0, "", null, ItemType.FAMILY)
        val grandFather = Item(context, 0, "Ông nội", null, ItemType.MALE)
        val grandMother = Item(context, 0, "Bà nội", null, ItemType.FEMALE)
        val uncle = Item(context, 0, "Bác trai", null, ItemType.MALE)
        val family6 = Item(context, 0, "", null, ItemType.FAMILY)
        val uncleWife = Item(context, 0, "Vợ bác", null, ItemType.FEMALE)
        val uncleSon = Item(context, 0, "Anh họ", null, ItemType.MALE)
        val family7 = Item(context, 0, "", null, ItemType.FAMILY)
        val uncleSonWife = Item(context, 0, "Vợ anh họ", null, ItemType.FEMALE)
        val nephew = Item(context, 0, "Cháu họ", null, ItemType.MALE)

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
    }

    private fun setStyle(item: Item) {
        val params = item.layoutParams
        params.height = 228
        params.width = 200
        item.layoutParams = params

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
            focusedNode = item
            memberMenuBar.visibility = when (memberMenuBar.visibility) {
                View.INVISIBLE -> View.VISIBLE
                View.VISIBLE -> View.INVISIBLE
                else -> View.VISIBLE
            }
        }
    }

    private fun setFamilyStyle(family: Item) {
        family.setBackgroundResource(R.drawable.ic_family)

//        val params = family.layoutParams
//        params.height = 228
//        params.width = 183
//        family.layoutParams = params
    }

    private fun setupMenuBar() {
        memberMenuBar = binding.memberMenuBar
        memberMenuBar.visibility = View.INVISIBLE

        // Navigate to member info
        val memberInfoBtn = binding.memberInfoBtn
        memberInfoBtn.setOnClickListener {
            it.findNavController().navigate(
                TreeMembersFragmentDirections.actionTreeMembersFragmentToMemberInfoFragment(focusedNode!!.id)
            )
        }

        // Navigate to add member
        val addMemberBtn = binding.addMemberBtn
        addMemberBtn.setOnClickListener {
            it.findNavController().navigate(
                TreeMembersFragmentDirections.actionTreeMembersFragmentToAddMemberFragment(focusedNode!!.id)
            )
        }

        // Navigate to edit member
        val editMemberBtn = binding.editMemberBtn
        editMemberBtn.setOnClickListener {
            it.findNavController().navigate(TreeMembersFragmentDirections.actionTreeMembersFragmentToEditMemberFragment(focusedNode!!.id))
        }
    }
}