package com.example.databinding01;

import android.content.Context;
import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;

public class GlideBindingAdapters {

    @BindingAdapter("imageURL")
    public static void setImageResource(ImageView view, String imageURL) {
        Context context = view.getContext();
        Glide.with(context).load(imageURL).into(view);
    }
}
