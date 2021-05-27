package com.example.familytree.member

import android.app.DatePickerDialog
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Toast
import com.example.familytree.DateHelper
import com.example.familytree.R
import com.example.familytree.databinding.FragmentAddMemberBinding
import com.example.familytree.network.member.Member
import kotlinx.android.synthetic.main.fragment_edit_member.*

enum class RelationshipType {
    PARENT,
    PARTNER,
    CHILD
}

class AddMemberFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private lateinit var binding: FragmentAddMemberBinding

//    private val addMemberViewModel: AddMemberViewModel by lazy {
//        ViewModelProvider(this,
//            AddMemberViewModel.Factory(
//                requireNotNull(context),
//                requireNotNull(AddMemberFragmentArgs.fromBundle(arguments!!).memberID)
//            ))
//            .get(AddMemberViewModel::class.java)
//    }

    private lateinit var addMemberViewModel: AddMemberViewModel

    private var type = RelationshipType.PARENT

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentAddMemberBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addMemberViewModel = ViewModelProvider(this,
        AddMemberViewModel.Factory(
            requireNotNull(context),
            requireNotNull(AddMemberFragmentArgs.fromBundle(arguments!!).memberID)
        )).get(AddMemberViewModel::class.java)

        // Relationship spinner
        val relationshipWith = binding.relationshipWith
        addMemberViewModel.member.observe(viewLifecycleOwner, {
            relationshipWith.text = "Relationship with ${it.fullName}"
        })
        setUpSpinner()

        // Basic info
        val firstName = binding.firstName
        val lastName = binding.lastName

        val dob = binding.dateOfBirth
        dob.setOnClickListener {
            showDatePickerDialog(dob)
        }
        val dod = binding.dateOfDeath
        dod.setOnClickListener {
            showDatePickerDialog(dod)
        }

        val male = binding.male
        male.isChecked = true
        val female = binding.female

        // Save button
        binding.save.setOnClickListener {
            //Log.e("AddMemberFragment", type.toString())
            val newMember = Member(
                null,
                null,
                firstName.text.toString(),
                lastName.text.toString(),
                DateHelper.dateToIso(dateOfBirth.text.toString()),
                DateHelper.dateToIso(dateOfDeath.text.toString()),
                null,
                null,
                if (male.isChecked) 0 else 1,
                null,
                null,
                null
            )

            when(type) {
                RelationshipType.PARENT -> addMemberViewModel.addParentMember(newMember)
                RelationshipType.PARTNER -> addMemberViewModel.addPartnerMember(newMember)
                RelationshipType.CHILD -> addMemberViewModel.addChildMember(newMember)
            }

            Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showDatePickerDialog(button: Button) {
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            button.text = "$dayOfMonth/${month + 1}/$year"
        }

        val datePickerDialog = DatePickerDialog(requireContext(), R.style.MySpinnerDatePickerStyle, dateSetListener, 2021, 0, 1)
        datePickerDialog.setOnCancelListener {
            button.text = null
        }
        datePickerDialog.show()
    }

    private fun setUpSpinner() {
        val relationshipType = binding.relationshipType
        ArrayAdapter.createFromResource(
            requireNotNull(context),
            R.array.relationship_type,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            relationshipType.adapter = adapter
        }
        relationshipType.onItemSelectedListener = this
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        type = when(position) {
            RelationshipType.PARENT.ordinal -> RelationshipType.PARENT
            RelationshipType.PARTNER.ordinal -> RelationshipType.PARTNER
            else -> RelationshipType.CHILD
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }
}