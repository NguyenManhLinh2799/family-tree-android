package com.example.familytree.member

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import coil.load
import com.example.familytree.DateHelper
import com.example.familytree.R
import com.example.familytree.databinding.FragmentMemberInfoBinding
import com.example.familytree.tree_members.TreeMembersFragmentArgs
import com.example.familytree.tree_members.TreeMembersViewModel

class MemberInfoFragment: Fragment() {

    private lateinit var binding: FragmentMemberInfoBinding

    private val memberInfoViewModel: MemberInfoViewModel by lazy {
        ViewModelProvider(this,
            MemberInfoViewModel.Factory(
                requireNotNull(context),
                requireNotNull(MemberInfoFragmentArgs.fromBundle(arguments!!).memberID)
            ))
            .get(MemberInfoViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentMemberInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val avatar = binding.avatar
        val fullName = binding.fullName
        val sex = binding.sex
        val dob = binding.dateOfBirth
        val dod = binding.dateOfDeath
        val note = binding.note

        memberInfoViewModel.member.observe(viewLifecycleOwner, {
            if (it.imageUrl != null) {
                avatar.load(it.imageUrl)
            }

            fullName.text = it.fullName
            sex.text = it.sex
            dob.text = DateHelper.isoToDate(it.dateOfBirth)
            dod.text = DateHelper.isoToDate(it.dateOfDeath)
            note.text = it.note
        })
    }
}