package com.example.familytree.member

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.familytree.R

class MemberInfoFragment: Fragment() {

    private val memberInfoViewModel: MemberInfoViewModel by lazy {
        ViewModelProviders.of(this).get(MemberInfoViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_member_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fullName = view.findViewById<TextView>(R.id.fullName)
        val dob = view.findViewById<TextView>(R.id.dateOfBirth)
        val sex = view.findViewById<TextView>(R.id.sex)
        memberInfoViewModel.member.observe(viewLifecycleOwner, {
            fullName.text = it.fullName
            dob.text = it.dateOfBirth
            sex.text = it.sex
        })
    }
}