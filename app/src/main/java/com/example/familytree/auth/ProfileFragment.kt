package com.example.familytree.auth

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import coil.load
import com.example.familytree.DateHelper
import com.example.familytree.R
import com.example.familytree.databinding.FragmentProfileBinding
import com.example.familytree.network.auth.EditProfileRequest
import com.example.familytree.network.auth.NetworkUser
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.fragment_edit_member.*

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private val profileViewModel: ProfileViewModel by lazy {
        ViewModelProvider(this, ProfileViewModel.Factory(
            requireNotNull(context)
        )).get(ProfileViewModel::class.java)
    }
    private var imgUrl: String? = null
    private var croppedImgUri: Uri? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpAddAvatar()

        val firstName = binding.firstName
        val midName = binding.midName
        val lastName = binding.lastName
        val male = binding.male
        val female = binding.female

        val dob = binding.dateOfBirth
        dob.setOnClickListener {
            showDatePickerDialog(dob)
        }

        val address = binding.address
        val phone = binding.phone
        val email = binding.email

        profileViewModel.userProfile.observe(viewLifecycleOwner, {
            this.imgUrl = it.avatarUrl
            if (this.imgUrl != null) {
                binding.avatar.load(this.imgUrl)
            }

            firstName.setText(it.firstName)
            midName.setText(it.midName)
            lastName.setText(it.lastName)
            when (it.isMale) {
                true -> male.isChecked = true
                else -> female.isChecked = true
            }
            dob.text = DateHelper.isoToDate(it.dateOfBirth)
            address.setText(it.address)
            phone.setText(it.phone)
            email.hint = it.email
        })

        binding.save.setOnClickListener {
            profileViewModel.editProfile(
                EditProfileRequest(
                    firstName.text.toString(),
                    midName.text.toString(),
                    lastName.text.toString(),
                    this.imgUrl,
                    address.text.toString(),
                    phone.text.toString(),
                    if (male.isChecked) 0 else 1,
                    DateHelper.dateToIso(dob.text.toString()),
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