package com.greenline.photoqselector

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    val REQUEST_IMAGE_CAPTURE = 1
    var currentUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvTakePhoto.setOnClickListener { checkCameraPermission() }
        tvGallry.setOnClickListener {
            val intent = Intent(this, PhotoReviewActivity::class.java)
            startActivity(intent)
        }
    }

    @SuppressLint("CheckResult")
    private fun checkCameraPermission(){
        RxPermissions(this)
            .request(Manifest.permission.CAMERA)
            .subscribe{granted->
                if(granted){
                    takePhoto()
                }
            }
    }

    private fun createImgFile(): Uri{
        val timeStap = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val file = File.createTempFile("JPEG_${timeStap}_",
            ".jpg",
            storageDir)
        return FileProvider.getUriForFile(this, "${BuildConfig.APPLICATION_ID}.fileProvider", file).apply { currentUri = this }
    }

    /**
     * 是否打开分区存储策略
     * @return Boolean
     */
    private fun isQ(): Boolean{
//        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
        return !Environment.isExternalStorageLegacy()
    }

    /**
     * 拍照
     */
    private fun takePhoto() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also {takePictureIntent->
            takePictureIntent.resolveActivity(packageManager)?.also {
                val createUri = if(isQ())createImgFileQ() else createImgFile()
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, createUri)
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(Activity.RESULT_OK != resultCode){
            return
        }
        if(REQUEST_IMAGE_CAPTURE == requestCode){
            Log.e("internal storage", currentUri?.toString() ?: "")
            if(!isQ()){
                galleryAddPic()
            }
            Glide.with(this)
                .load(currentUri)
                .into(ivPhoto)
        }
    }

    /**
     * 通知媒体管理搜索到对应图片
     */
    private fun galleryAddPic(){
        Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).also {mediaScanIntent->
            currentUri?.let {
                mediaScanIntent.data = currentUri
                sendBroadcast(mediaScanIntent)
            }
        }
    }

    /**
     * Q适配图片创建的uri
     * @return Uri?
     */
    private fun createImgFileQ(): Uri?{
        val timeStap = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val name = "JPEG_${timeStap}_.jpg"
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, name)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/QSelector")
        }
        val resolver = contentResolver
        val collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val item = resolver.insert(collection, values)
        return item?.apply {
            currentUri = this
            Log.e("Q-URI", toString())
        }
    }
}
