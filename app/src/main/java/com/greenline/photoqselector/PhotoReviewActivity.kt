package com.greenline.photoqselector

import android.annotation.SuppressLint
import android.content.ContentUris
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.BaseColumns
import android.provider.MediaStore
import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_photo_review.*
import java.util.jar.Manifest

/**
 * 图片相册查看
 * @author YuTianTian email: yutiantina@gmail.com
 * @since 2019-08-27
 */
class PhotoReviewActivity : AppCompatActivity() {

    val mediaColums = arrayOf(
        MediaStore.Images.Media._ID
    )

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
        contentResolver.query(MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL), mediaColums, null, null, null).use { cursor->
            val id = cursor?.getColumnIndexOrThrow(BaseColumns._ID)
            cursor?.moveToLast()
            do{
                photoList.add(fetchRealUri(cursor!!.getLong(id!!)))
            }while (true == cursor?.moveToPrevious())
        }

        initReclerview(photoList)
    }

    private fun fetchRealUri(id: Long): Uri{
        Log.e("media path id", id.toString())
        return ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id).apply { Log.e("media path", toString()) }
    }

    private fun initReclerview(photoList: MutableList<Uri>) {
        val adapter = PhotoAdapter(this, photoList)
        val layoutManager = GridLayoutManager(this, 4)
        rvList.layoutManager = layoutManager
        rvList.adapter = adapter
    }
}
