package com.example.familytree.member

import android.app.DatePickerDialog
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.example.familytree.DateHelper
import com.example.familytree.R
import com.example.familytree.databinding.FragmentAddMemberBinding
import com.example.familytree.network.member.Member
import kotlinx.android.synthetic.main.fragment_edit_member.*

class AddMemberFragment : Fragment() {

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

        binding.save.setOnClickListener {
            addMemberViewModel.addChildMember(
                Member(
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