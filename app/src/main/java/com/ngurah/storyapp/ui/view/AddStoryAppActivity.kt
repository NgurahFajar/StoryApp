package com.ngurah.storyapp.ui.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.ngurah.storyapp.R
import com.ngurah.storyapp.databinding.ActivityAddStoryAppBinding
import com.ngurah.storyapp.ui.viewmodel.AddStoryAppViewModel
import com.ngurah.storyapp.ui.viewmodelfactory.ViewModelFactory
import com.ngurah.storyapp.utils.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class AddStoryAppActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddStoryAppBinding
    private val addStoryAppViewModel: AddStoryAppViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    private var getFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryAppBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar()
        val preferences = this.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val bearer = "Bearer ${preferences.getString(TOKEN, "").toString()}"
        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }
        binding.apply {
            btnCamera.setOnClickListener { startTakePhoto() }
            btnGallery.setOnClickListener { startGallery() }
            btnUpload.setOnClickListener {
                changeAllButton(false)
                progressBar.visibility = View.VISIBLE
                getFile?.let { file ->
                    val desc = binding.descriptionEditText.text.toString()
                    if (desc.isNotEmpty()) {
                        val reducedFile = reduceImageFile(file)
                        val description = desc.toRequestBody("text/plain".toMediaType())
                        val requestImageFile = reducedFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
                        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                            "photo",
                            reducedFile.name,
                            requestImageFile
                        )
                        addStoryAppViewModel.addStoryApp(bearer, imageMultipart, description)
                            .observe(this@AddStoryAppActivity) { response ->
                                if (response.error == true) {
                                    changeAllButton(true)
                                    progressBar.visibility = View.GONE
                                    Toast.makeText(
                                        this@AddStoryAppActivity,
                                        response.message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    changeAllButton(true)
                                    progressBar.visibility = View.GONE
                                    Toast.makeText(
                                        this@AddStoryAppActivity,
                                        getString(R.string.success_upload_story),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    val intent = Intent(this@AddStoryAppActivity, MainActivity::class.java)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                                    startActivity(intent)
                                    finish()
                                }
                            }
                    } else {
                        changeAllButton(true)
                        progressBar.visibility = View.GONE
                        Toast.makeText(
                            this@AddStoryAppActivity,
                            getString(R.string.error_empty_description),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } ?: run {
                    changeAllButton(true)
                    progressBar.visibility = View.GONE
                    Toast.makeText(
                        this@AddStoryAppActivity,
                        getString(R.string.error_no_image_selected),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun supportActionBar(){
        supportActionBar?.apply {
            this.title = getString(R.string.add_story_title)
            setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    getString(R.string.error_permission_required),
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun changeAllButton(checkEnabled: Boolean) {
        binding.apply {
            btnCamera.isEnabled = checkEnabled
            btnGallery.isEnabled = checkEnabled
            btnUpload.isEnabled = checkEnabled
        }
    }

    private lateinit var currentPhotoPath: String
    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)
            myFile.let { file ->
                getFile = file
                binding.ivPreview.setImageBitmap(BitmapFactory.decodeFile(file.path))
            }
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg = result.data?.data as Uri
            selectedImg.let { uri ->
                val myFile = uriToFile(uri, this@AddStoryAppActivity)
                getFile = myFile
                binding.ivPreview.setImageURI(uri)
            }
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun startTakePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)

        createTempFile(application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this@AddStoryAppActivity,
                "com.ngurah.storyapp.ui",
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_TITLE, getString(R.string.choose_picture))
        launcherIntentGallery.launch(Intent.createChooser(intent, getString(R.string.choose_picture)))
    }


    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(android.Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}