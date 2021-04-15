package com.example.familytree

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
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

        val family = Item(context, ".", "", false)


        val father = Item(context, "Bố", "", false)
        val mother = Item(context, "Mẹ", "", false)
        val me = Item(context, "Tôi", "", false)
        val brother = Item(context, "Em trai", "", false)
        val sister = Item(context, "Chị gái", "", false)

        allMembers = listOf(
                father,
                mother,
                me,
                brother,
                sister
        )

        val memberIsMale = listOf<Boolean>(
                true,
                false,
                true,
                true,
                false
        )

        treeView?.addCentralItem(family, false)
        setFamilyStyle(family)

        treeView.addItem(father, family, 300, 0, ItemLocation.LEFT, false, null)
        treeView.addItem(mother, family, 300, 0, ItemLocation.RIGHT, false, null)
        treeView.addItem(me, family, 300, 150, ItemLocation.BOTTOM, false, null)
        treeView.addItem(brother, family, 300, 150, ItemLocation.BOTTOM, false, null)
        treeView.addItem(sister, family, 300, 150, ItemLocation.BOTTOM, false, null)

        val memberInfoBtn = view.findViewById<LinearLayout>(R.id.memberInfoBtn)
        memberInfoBtn.setOnClickListener {
            it.findNavController().navigate(TreeMembersFragmentDirections.actionTreeMembersFragmentToMemberInfoFragment())
        }

        val memberMenuBar = view.findViewById<LinearLayout>(R.id.memberMenuBar)
        memberMenuBar.visibility = View.INVISIBLE

        allMembers.forEachIndexed { index, member ->
            setStyle(member, memberIsMale[index])

            member.setOnClickListener {
                memberMenuBar?.visibility = when (memberMenuBar.visibility) {
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
        params.height = 200
        family.layoutParams = params
    }
}