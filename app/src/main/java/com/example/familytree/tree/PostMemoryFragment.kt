package com.example.familytree.tree

import android.app.Activity
import android.app.DatePickerDialog
import android.content.ClipData
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Layout
import android.util.Log
import android.view.*
import android.widget.*
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.familytree.DateHelper
import com.example.familytree.R
import com.example.familytree.databinding.FragmentPostMemoryBinding
import com.example.familytree.network.NetworkMemory

class PostMemoryFragment : Fragment() {

    private lateinit var binding: FragmentPostMemoryBinding
    private var treeID: Int? = null

    private lateinit var postMemoryViewModel: PostMemoryViewModel
    private lateinit var memoryDate: Button
    private lateinit var memoryDescription: EditText
    private lateinit var imageList: LinearLayout
    private val allImageUris = ArrayList<Uri>(0)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentPostMemoryBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // References
        this.treeID = PostMemoryFragmentArgs.fromBundle(arguments!!).treeID
        postMemoryViewModel = ViewModelProvider(this,
        PostMemoryViewModel.Factory(
            requireNotNull(context),
        )).get(PostMemoryViewModel::class.java)

        // Basic infomation
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
                postMemoryViewModel.postMemory(
                    NetworkMemory(
                        null,
                        this.treeID,
                        memoryDescription.text.toString(),
                        DateHelper.dateToIso(memoryDate.text.toString()),
                        null,
                        null
                    ),
                    this.allImageUris
                )
                Toast.makeText(context, "Posted", Toast.LENGTH_SHORT).show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setUpAddImages() {
        val requestCode = 1
        val addImages = binding.addImages
        addImages.setOnClickListener {
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            gallery.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            startActivityForResult(Intent.createChooser(gallery, "Select Images"), requestCode)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            setImageList(data?.clipData)
        }
    }

    private fun setImageList(clipData: ClipData?) {
        if (clipData == null) {
            return;
        }
        val count = clipData.itemCount
        for (i in (count - 1) downTo 0) {
            val imageUri = clipData.getItemAt(i).uri

            val imageView = ImageView(context)
            imageList.addView(imageView)

            val imageParams = LinearLayout.LayoutParams(500, 500)
            imageView.layoutParams = imageParams
            imageView.setImageURI(imageUri)
            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
            imageView.setOnClickListener {
                imageList.removeView(imageView)
                allImageUris.remove(imageUri)
            }

            allImageUris.add(imageUri)
        }
    }
}