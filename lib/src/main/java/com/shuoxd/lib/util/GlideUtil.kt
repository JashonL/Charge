package com.shuoxd.lib.util

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

object GlideUtil {


    fun  showGif(context:Context,gif:Int,imageView: ImageView){

        //加载gif
        Glide.with(context).asGif().load(gif)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC).into(imageView)

    }

}