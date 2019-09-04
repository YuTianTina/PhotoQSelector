package com.greenline.photoqselector

import android.app.RecoverableSecurityException
import android.content.Context
import android.media.Image
import android.net.Uri
import android.provider.MediaStore
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
    private var mPhotoList = photoList
    private var onDelete: ((Uri, Int) -> Boolean)? = null
    public fun delete(onDelete : (uri: Uri, position: Int) ->Boolean){
        this.onDelete = onDelete
    }

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
        holder.itemView.setOnClickListener {
            // 删除
            val index = holder.adapterPosition
            if(true == onDelete?.invoke(mPhotoList[index], index)){
                mPhotoList.removeAt(index)
                notifyItemRemoved(index)
            }
        }
    }
}

class PhotoVH(itemView: View) : RecyclerView.ViewHolder(itemView){

}