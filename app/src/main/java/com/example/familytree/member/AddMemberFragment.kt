package com.example.familytree.member

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.familytree.DateHelper
import com.example.familytree.R
import com.example.familytree.databinding.FragmentAddMemberBinding
import com.example.familytree.network.member.Member
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.fragment_edit_member.*

enum class RelationshipType {
    PARENT,
    PARTNER,
    CHILD
}

class AddMemberFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private lateinit var binding: FragmentAddMemberBinding

    private lateinit var addMemberViewModel: AddMemberViewModel
    private var type = RelationshipType.PARENT
    private var croppedImgUri: Uri? = null
    private var treeID: Int? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentAddMemberBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Save treeID for navigating back
        this.treeID = AddMemberFragmentArgs.fromBundle(requireArguments()).treeID

        // Add avatar
        setUpAddAvatar()

        // View model
        addMemberViewModel = ViewModelProvider(this,
        AddMemberViewModel.Factory(
            requireNotNull(context),
            requireNotNull(AddMemberFragmentArgs.fromBundle(requireArguments()).memberID)
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
        val male = binding.male
        male.isChecked = true
        val female = binding.female

        val dob = binding.dateOfBirth
        dob.setOnClickListener {
            showDatePickerDialog(dob)
        }
        val dod = binding.dateOfDeath
        dod.setOnClickListener {
            showDatePickerDialog(dod)
        }

        val phone = binding.phone
        val address = binding.address
        val job = binding.job
        val note = binding.note

        // Save button
        binding.add.setOnClickListener {
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
                phone.text.toString(),
                address.text.toString(),
                job.text.toString(),
                note.text.toString(),
                null,
                null
            )

            when(type) {
                RelationshipType.PARENT -> addMemberViewModel.addParentMember(newMember, this.croppedImgUri)
                RelationshipType.PARTNER -> addMemberViewModel.addPartnerMember(newMember, this.croppedImgUri)
                RelationshipType.CHILD -> addMemberViewModel.addChildMember(newMember, this.croppedImgUri)
            }
        }

        addMemberViewModel.addMessage.observe(viewLifecycleOwner, {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        })

        addMemberViewModel.navigateToTreeMembers.observe(viewLifecycleOwner, {
            if (it == true) {
                findNavController().navigate(
                    AddMemberFragmentDirections.actionAddMemberFragmentToTreeFragment(this.treeID!!)
                )
                addMemberViewModel.doneNavigating()
            }
        })
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

    override fun onNothingSelected(parent: AdapterView<*>?) {}

    private fun setUpAddAvatar() {
        val requestCode = 1
        val addAvatar = binding.addAvatar
        addAvatar.setOnClickListener {
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, requestCode)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val avatar = binding.avatar

        // Pick image
        if (resultCode == Activity.RESULT_OK && requestCode == 1) {
            CropImage.activity()
                .setAspectRatio(1, 1)
                .start(requireNotNull(context), this)
        }

        // Crop Image
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                this.croppedImgUri = result.uri
                avatar.setImageURI(croppedImgUri)
            }
        }
    }
}