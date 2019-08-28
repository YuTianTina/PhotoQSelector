package com.greenline.photoqselector

import android.content.Context
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

/**
 *
 * @author yutiantian email: yutiantina@gmail.com
 * @since 2019-08-27
 */
class PhotoAdapter(val context: Context, photoList: MutableList<Uri>): RecyclerView.Adapter<PhotoVH>() {
    private var mPhotoList = photoList;
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoVH {
        val imgView = ImageView(context)
        return PhotoVH(imgView)
    }

    override fun getItemCount(): Int = mPhotoList.size

    override fun onBindViewHolder(holder: PhotoVH, position: Int) {
        Glide.with(context)
            .load(mPhotoList[position])
            .thumbnail(0.3f)
            .into(holder.itemView as ImageView)
    }
}

class PhotoVH(itemView: View) : RecyclerView.ViewHolder(itemView){

}