package com.example.familytree.tree

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.familytree.R
import com.example.familytree.databinding.FragmentTreeMembersBinding
import com.example.familytree.network.member.Member
import kotlinx.android.synthetic.main.activity_login.view.*
import kotlinx.android.synthetic.main.fragment_tree_members.*
import me.jagar.mindmappingandroidlibrary.Views.Item
import me.jagar.mindmappingandroidlibrary.Views.ItemLocation
import me.jagar.mindmappingandroidlibrary.Views.ItemType
import me.jagar.mindmappingandroidlibrary.Views.MindMappingView
import me.jagar.mindmappingandroidlibrary.Zoom.ZoomLayout

private const val TREE_ID = "treeID"

class TreeMembersFragment : Fragment() {

    // Init in view pager
    companion object {
        @JvmStatic
        fun newInstance(treeID: Int) = TreeMembersFragment().apply {
            arguments = Bundle().apply {
                putInt(TREE_ID, treeID)
            }
        }
    }

    // Search result item click
    private val onSearchMemberItemClick = object : SearchMemberAdapter.OnSearchMemberItemClick {
        override fun onClick(member: Item?) {
            searchResult.visibility = View.INVISIBLE
            treeMembersViewModel.select(member)
        }
    }

    // Views
    private lateinit var binding: FragmentTreeMembersBinding
    private lateinit var zoomLayout: ZoomLayout
    private lateinit var treeView: MindMappingView
    private lateinit var memberMenuBar: LinearLayout
    private lateinit var allNodes: List<Item>
    private lateinit var searchView: SearchView
    private lateinit var searchResult: RecyclerView

    // References
    private var treeID: Int? = null
    private lateinit var treeFragment: TreeFragment

    // View model
    private lateinit var treeMembersViewModel: TreeMembersViewModel

    // For the algorithm
    private var added = ArrayList<Item>(0)
    private var notYetAdded = ArrayList<Member>(0)

    // The node being focused
    private var focusedMember: Item? = null



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentTreeMembersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get parent fragment
        treeFragment = parentFragment as TreeFragment

        // View model
        if (this.treeID == null) {
            arguments?.let {
                this.treeID = it.getInt(TREE_ID)
                treeMembersViewModel = ViewModelProvider(this,
                    TreeMembersViewModel.Factory(
                        requireNotNull(context),
                        requireNotNull(this.treeID)
                    ))
                    .get(TreeMembersViewModel::class.java)
            }
        } else {
            treeMembersViewModel.loadTreeMembers(this.treeID!!)
        }

        // Menu bar
        setupMenuBar()

        // Zoom layout
        zoomLayout = binding.zoomLayout

        // Tree view
        treeView = binding.treeView

        // Observe members
        treeMembersViewModel.treeMembers.observe(viewLifecycleOwner, {
            updateAllNodes(it.people)
            (activity as AppCompatActivity).supportActionBar?.title = it.name
        })

        // Search
        val searchMemberAdapter = SearchMemberAdapter(this.onSearchMemberItemClick)
        searchResult = binding.searchResult
        searchResult.adapter = searchMemberAdapter
        setupSearchView()
        searchResult.visibility = View.INVISIBLE
        treeMembersViewModel.searchResult.observe(viewLifecycleOwner, {
            searchResult.visibility = View.VISIBLE
            searchMemberAdapter.submitList(it)
        })
        treeMembersViewModel.selectedMember.observe(viewLifecycleOwner, {
            unFocus()
            focus(it)
        })

        // Delete
        treeMembersViewModel.deleteSuccess.observe(viewLifecycleOwner, {
            if (it) {
                Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Cannot delete this member", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateAllNodes(members: List<Member>) {

        // Remove all nodes
        allNodes = emptyList()
        added.clear()
        notYetAdded.clear()

        // Re-create tree view
        treeView.removeAllViews()
        treeView.clearAllPaths()

        // And update
        //fakeData()
        addAllMembers(members)
        allNodes = added

        treeView.ReingoldTilford()
        treeView.invalidate()
        
        allNodes.forEach {
            setStyle(it)
        }

        zoomLayout.zoomTo(2f, false)
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
            val rootItem = Item(context, rootMem.id!!, rootMem.fullName, rootMem.getLifeTime(),
            when (rootMem.isMale) {
                true -> ItemType.MALE
                else -> ItemType.FEMALE
            }, rootMem.imageUrl)
            treeView.addCentralItem(rootItem)
            added.add(rootItem)
            notYetAdded.remove(rootMem)

            // Find and add partner (if any)
            var familyItem: Item? = null
            if (rootMem.spouses?.isNotEmpty() == true) {
                for (mem in notYetAdded) {
                    if (mem.id == rootMem.spouses!![0].id) {
                        familyItem = Item(context, 0, "", null, ItemType.FAMILY, null)
                        if (mem.isMale) {
                            val partnerItem = Item(context, mem.id!!, mem.fullName, mem.getLifeTime(), ItemType.MALE, mem.imageUrl)
                            treeView.addItem(familyItem, rootItem, ItemLocation.LEFT)
                            treeView.addItem(partnerItem, familyItem, ItemLocation.LEFT)
                            added.add(partnerItem)
                        } else {
                            val partnerItem = Item(context, mem.id!!, mem.fullName, mem.getLifeTime(), ItemType.FEMALE, mem.imageUrl)
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

                val childItem = Item(context, childMem.id!!, childMem.fullName, childMem.getLifeTime(),
                when (childMem.isMale) {
                    true -> ItemType.MALE
                    else -> ItemType.FEMALE
                }, childMem.imageUrl)
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
                    familyItem = Item(context, 0, "", null, ItemType.FAMILY, null)
                    if (mem.isMale) {
                        val partnerItem = Item(context, mem.id!!, mem.fullName, mem.getLifeTime(), ItemType.MALE, mem.imageUrl)
                        treeView.addItem(familyItem, item, ItemLocation.LEFT)
                        treeView.addItem(partnerItem, familyItem, ItemLocation.LEFT)
                        added.add(partnerItem)
                    } else {
                        val partnerItem = Item(context, mem.id!!, mem.fullName, mem.getLifeTime(), ItemType.FEMALE, mem.imageUrl)
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
            val childItem = Item(context, childMem.id!!, childMem.fullName, childMem.getLifeTime(),
                when (childMem.isMale) {
                    true -> ItemType.MALE
                    else -> ItemType.FEMALE
                }, childMem.imageUrl)
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
        val me = Item(context, 0, "T??i", null, ItemType.MALE, null)
        val family1 = Item(context, 0, "", null, ItemType.FAMILY, null)
        val wife = Item(context, 0, "V???", null, ItemType.FEMALE, null)
        val daughter = Item(context, 0, "Con g??i", null, ItemType.FEMALE, null)
        val family2 = Item(context, 0, "", null, ItemType.FAMILY, null)
        val sonInLaw = Item(context, 0, "Con r???", null, ItemType.MALE, null)
        val grandSon = Item(context, 0, "Ch??u trai", null, ItemType.MALE, null)
        val father = Item(context, 0, "Ba", null, ItemType.MALE, null)
        val family3 = Item(context, 0, "", null, ItemType.FAMILY, null)
        val mother = Item(context, 0, "M???", null, ItemType.FEMALE, null)
        val sister = Item(context, 0, "Ch???", null, ItemType.FEMALE, null)
        val family4 = Item(context, 0, "", null, ItemType.FAMILY, null)
        val brotherInLaw = Item(context, 0, "Anh r???", null, ItemType.MALE, null)
        val sisterSon = Item(context, 0, "Con trai ch???", null, ItemType.MALE, null)
        val family5 = Item(context, 0, "", null, ItemType.FAMILY, null)
        val grandFather = Item(context, 0, "??ng n???i", null, ItemType.MALE, null)
        val grandMother = Item(context, 0, "B?? n???i", null, ItemType.FEMALE, null)
        val uncle = Item(context, 0, "B??c trai", null, ItemType.MALE, null)
        val family6 = Item(context, 0, "", null, ItemType.FAMILY, null)
        val uncleWife = Item(context, 0, "V??? b??c", null, ItemType.FEMALE, null)
        val uncleSon = Item(context, 0, "Anh h???", null, ItemType.MALE, null)
        val family7 = Item(context, 0, "", null, ItemType.FAMILY, null)
        val uncleSonWife = Item(context, 0, "V??? anh h???", null, ItemType.FEMALE, null)
        val nephew = Item(context, 0, "Ch??u h???", null, ItemType.MALE, null)

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

        // Set width and height
        val params = item.layoutParams
        params.height = 250
        params.width = 200
        item.layoutParams = params

        // If this is family item
        if (item.type == ItemType.FAMILY) {
            return setFamilyStyle(item)
        }

        // Background
        item.setBackgroundResource(when (item.type) {
            ItemType.MALE -> R.drawable.bg_male
            else -> R.drawable.bg_female
        })

        // Image
        val image = ImageView(context)
        if (item.imgUrl != null) {
            image.load(item.imgUrl)
        }
        image.y = 0f
        image.z = -10f
        item.addView(image, 0)
        val imgParams = image.layoutParams as RelativeLayout.LayoutParams
        imgParams.width = 150
        imgParams.height = 150
        imgParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE)
        image.layoutParams = imgParams

        // Title
        item.title.y = 150f
        item.title.z = 10f
        item.title.textAlignment = View.TEXT_ALIGNMENT_CENTER
        item.addView(item.title, 1)
        val titleParams = item.title.layoutParams as RelativeLayout.LayoutParams
        titleParams.width = RelativeLayout.LayoutParams.MATCH_PARENT
        item.title.layoutParams = titleParams

        // Content
        item.content.y = 190f
        item.content.z = 20f
        item.content.textAlignment = View.TEXT_ALIGNMENT_CENTER
        item.content.textSize = 10f
        item.addView(item.content, 2)
        val contentParams = item.content.layoutParams as RelativeLayout.LayoutParams
        contentParams.width = RelativeLayout.LayoutParams.MATCH_PARENT
        item.content.layoutParams = contentParams

        item.setPadding(5, 5, 5, 5)
        item.setBorder(Color.TRANSPARENT, 0)
        item.setOnClickListener {
            if (focusedMember == item) {
                unFocus()
            } else {
                focus(item)
            }
        }
    }

    private fun setFamilyStyle(family: Item) {
        val params = family.layoutParams
        params.height = 250
        params.width = 200
        family.layoutParams = params
    }

    private fun setupMenuBar() {
        memberMenuBar = binding.memberMenuBar
        memberMenuBar.visibility = View.INVISIBLE

        // Navigate to member info
        val memberInfoBtn = binding.memberInfoBtn
        memberInfoBtn.setOnClickListener {
            treeFragment.navigateToMemberInfo(focusedMember!!.id)
        }

        // Navigate to add member
        val addMemberBtn = binding.addMemberBtn
        addMemberBtn.setOnClickListener {
            treeFragment.navigateToAddMember(focusedMember!!.id)
        }

        // Navigate to edit member
        val editMemberBtn = binding.editMemberBtn
        editMemberBtn.setOnClickListener {
            treeFragment.navigateToEditMember(focusedMember!!.id)
        }

        // Delete member
        val deleteMemberBtn = binding.deleteMemberBtn
        deleteMemberBtn.setOnClickListener {
            treeMembersViewModel.deleteMember(focusedMember!!.id)
            unFocus()
        }
    }

    private fun focus(item: Item) {
        focusedMember?.setBorder(Color.TRANSPARENT, 0)
        focusedMember = item
        focusedMember!!.setBorder(Color.GRAY, 15)
        memberMenuBar.visibility = View.VISIBLE

        zoomLayout.moveTo(3f, -item.x + 575f, -item.y + 950f, true)
    }

    private fun unFocus() {
        focusedMember?.setBorder(Color.TRANSPARENT, 0)
        focusedMember = null
        memberMenuBar.visibility = View.INVISIBLE
    }

    private fun setupSearchView() {
        searchView = binding.searchView

        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != "") {
                    treeMembersViewModel.search(newText, allNodes)
                } else {
                    searchResult.visibility = View.INVISIBLE
                }
                return false
            }
        })
    }
}