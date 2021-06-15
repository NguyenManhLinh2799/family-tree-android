package com.example.familytree.member

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import coil.load
import com.example.familytree.DateHelper
import com.example.familytree.R
import com.example.familytree.databinding.FragmentEditMemberBinding
import com.example.familytree.network.member.Member
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.fragment_edit_member.*


class EditMemberFragment: Fragment() {

    private lateinit var binding: FragmentEditMemberBinding

    private val editMemberViewModel: EditMemberViewModel by lazy {
        ViewModelProvider(
            this,
            EditMemberViewModel.Factory(
                requireNotNull(context),
                requireNotNull(EditMemberFragmentArgs.fromBundle(requireArguments()).memberID)
            )
        )
            .get(EditMemberViewModel::class.java)
    }

    private var imgUrl: String? = null
    private var croppedImgUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditMemberBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpAddAvatar()

        val firstName = binding.firstName
        val lastName = binding.lastName
        val male = binding.male
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

        editMemberViewModel.member.observe(viewLifecycleOwner, {
            this.imgUrl = it.imageUrl
            if (this.imgUrl != null) {
                binding.avatar.load(this.imgUrl)
            }

            firstName.setText(it.firstName)
            lastName.setText(it.lastName)
            when (it.isMale) {
                true -> male.isChecked = true
                else -> female.isChecked = true
            }
            dob.text = DateHelper.isoToDate(it.dateOfBirth)
            dod.text = DateHelper.isoToDate(it.dateOfDeath)
            phone.setText(it.phoneNumber)
            address.setText(it.homeAddress)
            job.setText(it.occupation)
            note.setText(it.note)
        })

        binding.save.setOnClickListener {
            editMemberViewModel.editMember(
                Member(
                    null,
                    EditMemberFragmentArgs.fromBundle(requireArguments()).memberID,
                    firstName.text.toString(),
                    lastName.text.toString(),
                    DateHelper.dateToIso(dob.text.toString()),
                    DateHelper.dateToIso(dod.text.toString()),
                    null,
                    null,
                    if (male.isChecked) 0 else 1,
                    phone.text.toString(),
                    address.text.toString(),
                    job.text.toString(),
                    note.text.toString(),
                    null,
                    this.imgUrl
                ),
                this.croppedImgUri
            )
            Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showDatePickerDialog(button: Button) {
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            button.text = "$dayOfMonth/${month + 1}/$year"
        }

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            R.style.MySpinnerDatePickerStyle,
            dateSetListener,
            2021,
            0,
            1
        )
        datePickerDialog.setOnCancelListener {
            button.text = null
        }
        datePickerDialog.show()
    }

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