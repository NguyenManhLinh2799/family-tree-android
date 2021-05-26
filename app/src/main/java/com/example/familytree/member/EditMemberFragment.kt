package com.example.familytree.member

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.familytree.DateHelper
import com.example.familytree.R
import com.example.familytree.databinding.FragmentEditMemberBinding
import com.example.familytree.network.member.Member
import kotlinx.android.synthetic.main.fragment_edit_member.*

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
        dob.setOnClickListener {
            showDatePickerDialog(dob)
        }
        val dod = binding.dateOfDeath
        dod.setOnClickListener {
            showDatePickerDialog(dod)
        }

        val male = binding.male
        val female = binding.female

        editMemberViewModel.member.observe(viewLifecycleOwner, {
            firstName.setText(it.firstName)
            lastName.setText(it.lastName)
            dob.text = DateHelper.isoToDate(it.dateOfBirth)
            dod.text = DateHelper.isoToDate(it.dateOfDeath)
            when (it.isMale) {
                true -> male.isChecked = true
                else -> female.isChecked = true
            }
        })

        binding.save.setOnClickListener {
            editMemberViewModel.editMember(
                Member(
                null,
                EditMemberFragmentArgs.fromBundle(arguments!!).memberID,
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
            )
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
}