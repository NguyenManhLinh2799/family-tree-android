package com.example.familytree.member

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.familytree.R
import com.example.familytree.databinding.FragmentEditMemberBinding
import com.example.familytree.tree_members.TreeMembersViewModel

class EditMemberFragment: Fragment() {

    private lateinit var binding: FragmentEditMemberBinding

    private val editMemberViewModel: EditMemberViewModel by lazy {
        ViewModelProvider(this,
            EditMemberViewModel.Factory(
                requireNotNull(context),
                requireNotNull(EditMemberFragmentArgs.fromBundle(arguments!!).memberID)
            ))
            .get(EditMemberViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentEditMemberBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val firstName = binding.firstName
        val lastName = binding.lastName
        val dob = binding.dateOfBirth
        val dod = binding.dateOfDeath
        val male = binding.male
        val female = binding.female
        editMemberViewModel.member.observe(viewLifecycleOwner, {
            firstName.setText(it.firstName)
            lastName.setText(it.lastName)
            dob.setText(it.dateOfBirth)
            dod.setText(it.dateOfDeath)
            when (it.isMale) {
                true -> male.isChecked = true
                else -> female.isChecked = true
            }
        })
    }
}