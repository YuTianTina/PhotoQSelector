package com.greenline.photoqselector

import android.annotation.SuppressLint
import android.app.Activity
import android.app.RecoverableSecurityException
import android.content.ContentUris
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.BaseColumns
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_photo_review.*

/**
 * 图片相册查看
 * @author YuTianTian email: yutiantina@gmail.com
 * @since 2019-08-27
 */
class PhotoReviewActivity : AppCompatActivity() {
    val REQUEST_DELETE = 101
    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_review)

        RxPermissions(this)
            .request(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            .subscribe{grant->
                if(grant){
                    obtainPictureMedias()
                }
            }
    }

    /**
     * 获取资源图片
     */
    private fun obtainPictureMedias() {
        val photoList = mutableListOf<Uri>()
        val mediaColums = arrayOf(
            MediaStore.Images.Media._ID
        )
        contentResolver.query(MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL), mediaColums, null, null, null).use { cursor->
            val id = cursor?.getColumnIndexOrThrow(BaseColumns._ID)
            cursor?.moveToLast()
            repeat(cursor?.count ?: 0){
                photoList.add((fetchRealUri(cursor!!.getLong(id!!))))
                cursor.moveToPrevious()
            }
        }

        initReclerview(photoList)
    }

    /**
     * 通过id获取对应资源的uri
     * @param id Long
     * @return Uri
     */
    private fun fetchRealUri(id: Long): Uri{
        Log.e("media path id", id.toString())
        return ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id).apply { Log.e("media path", toString()) }
    }

    @SuppressLint("NewApi")
    private fun initReclerview(photoList: MutableList<Uri>) {
        val adapter = PhotoAdapter(this, photoList)
        val layoutManager = GridLayoutManager(this, 4)
        rvList.layoutManager = layoutManager
        rvList.adapter = adapter.apply { delete { uri, position->
            try{
                contentResolver.delete(uri, null, null)
                return@delete true
            }catch (e: SecurityException) {
                val intentSender =
                    (e as? RecoverableSecurityException)?.userAction?.actionIntent?.intentSender
                if(null == intentSender){
                    Toast.makeText(this@PhotoReviewActivity, e.message, Toast.LENGTH_LONG).show()
                    return@delete false
                }
                startIntentSenderForResult(
                    intentSender,
                    REQUEST_DELETE,
                    null,
                    0,
                    0,
                    0,
                    null
                )
                return@delete  false
            }
        } }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(REQUEST_DELETE == requestCode && Activity.RESULT_OK == resultCode){
            Toast.makeText(this, "allow", Toast.LENGTH_LONG).show()
        }
    }


}
