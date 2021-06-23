package com.example.familytree.tree

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.app.Activity
import android.app.DatePickerDialog
import android.content.ClipData
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.familytree.DateHelper
import com.example.familytree.R
import com.example.familytree.databinding.FragmentPostMemoryBinding
import com.example.familytree.network.NetworkMemory

private const val PICK_IMAGE_REQUEST = 1
private const val READ_EXTERNAL_REQUEST = 2

class PostMemoryFragment : Fragment() {

    private lateinit var binding: FragmentPostMemoryBinding
    private var treeID: Int? = null

    private lateinit var postMemoryViewModel: PostMemoryViewModel
    private lateinit var memoryDate: Button
    private lateinit var memoryDescription: EditText
    private lateinit var imageList: LinearLayout
    private val allImagePaths = ArrayList<String>(0)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentPostMemoryBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // References
        this.treeID = PostMemoryFragmentArgs.fromBundle(requireArguments()).treeID
        postMemoryViewModel = ViewModelProvider(this,
        PostMemoryViewModel.Factory(
            requireNotNull(context),
        )).get(PostMemoryViewModel::class.java)

        // Basic information
        this.memoryDate = binding.memoryDate
        this.memoryDate.setOnClickListener {
            showDatePickerDialog(it as Button)
        }
        this.memoryDescription = binding.memoryDescription

        // Images
        imageList = binding.imageList
        setUpAddImages()

        // Navigate back after posting
        postMemoryViewModel.navigateToTreeMembers.observe(viewLifecycleOwner, {
            if (it == true) {
                findNavController().navigate(
                    PostMemoryFragmentDirections.actionPostMemoryFragmentToTreeFragment(this.treeID!!)
                )
                postMemoryViewModel.doneNavigating()
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.post_memory_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.postMemory -> {
                postMemoryViewModel.postMemoryByPaths(
                    NetworkMemory(
                        null,
                        this.treeID,
                        memoryDescription.text.toString(),
                        DateHelper.dateToIso(memoryDate.text.toString()),
                        null,
                        null,
                        null
                    ),
                    this.allImagePaths
                )
                Toast.makeText(context, "Posted", Toast.LENGTH_SHORT).show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setUpAddImages() {
        val addImages = binding.addImages
        addImages.setOnClickListener {
            requestPermissionAndPickImages()
//            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
//            gallery.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
//            startActivityForResult(Intent.createChooser(gallery, "Select Images"), PICK_IMAGE_REQUEST)
        }
    }

    private fun requestPermissionAndPickImages() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            pickImages()
            return
        }
        val result = ContextCompat.checkSelfPermission(requireContext(), READ_EXTERNAL_STORAGE)
        if (result == PackageManager.PERMISSION_GRANTED) {
            pickImages()
        } else {
            requestPermissions(listOf(READ_EXTERNAL_STORAGE).toTypedArray(), READ_EXTERNAL_REQUEST)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode != READ_EXTERNAL_REQUEST) return;
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            pickImages()
        } else {
            Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    private fun pickImages() {
        val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        gallery.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        startActivityForResult(Intent.createChooser(gallery, "Select Images"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            setImageList(data?.clipData)
        }
    }

    private fun setImageList(clipData: ClipData?) {
        if (clipData == null) {
            return;
        }
        val count = clipData.itemCount
        for (i in 0 until count) {
            val imageUri = clipData.getItemAt(i).uri
            val imagePath = getRealPathFromURI(imageUri)

            val imageView = ImageView(context)
            imageList.addView(imageView)

            val imageParams = LinearLayout.LayoutParams(500, 500)
            imageView.layoutParams = imageParams
            imageView.setImageURI(imageUri)
            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
            imageView.setOnClickListener {
                imageList.removeView(imageView)
                allImagePaths.remove(imagePath)
            }

            if (imagePath != null) {
                allImagePaths.add(imagePath)
            }
            Log.e("PostMemoryFragment", imageUri.toString())
        }
    }

    private fun getRealPathFromURI(contentURI: Uri): String? {
        val result: String?
        val cursor: Cursor? = requireContext().contentResolver.query(contentURI, null, null, null, null)
        if (cursor == null) {
            result = contentURI.path
        } else {
            cursor.moveToFirst()
            val idx: Int = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            result = cursor.getString(idx)
            cursor.close()
        }
        return result
    }
}