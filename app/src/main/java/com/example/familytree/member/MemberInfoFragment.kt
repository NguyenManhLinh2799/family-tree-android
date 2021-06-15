package com.example.familytree.member

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import coil.load
import com.example.familytree.DateHelper
import com.example.familytree.databinding.FragmentMemberInfoBinding
import kotlinx.android.synthetic.main.fragment_add_member.*

class MemberInfoFragment: Fragment() {

    private lateinit var binding: FragmentMemberInfoBinding

    private val memberInfoViewModel: MemberInfoViewModel by lazy {
        ViewModelProvider(this,
            MemberInfoViewModel.Factory(
                requireNotNull(context),
                requireNotNull(MemberInfoFragmentArgs.fromBundle(requireArguments()).memberID)
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
        val phone = binding.phone
        val address = binding.address
        val job = binding.job
        val note = binding.note

        memberInfoViewModel.member.observe(viewLifecycleOwner, {
            if (it.imageUrl != null) {
                avatar.load(it.imageUrl)
            }

            fullName.text = it.fullName
            val act = activity as AppCompatActivity
            act.supportActionBar?.title = "${it.fullName}'s Info"

            sex.text = it.sex
            dob.text = DateHelper.isoToDate(it.dateOfBirth)
            dod.text = DateHelper.isoToDate(it.dateOfDeath)
            phone.text = it.phoneNumber
            address.text = it.homeAddress
            job.text = it.occupation
            note.text = it.note
        })
    }
}