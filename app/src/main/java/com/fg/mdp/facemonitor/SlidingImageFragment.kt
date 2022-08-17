package com.fg.mdp.facemonitor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.android.synthetic.main.slidingimages_layout.*

class SlidingImageFragment(var imageBG: String) : Fragment(){

    private var TAG = SlidingImageFragment::class.java.simpleName
    lateinit var rootView: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.slidingimages_layout, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Glide
            .with(this)
            .load(imageBG)
            .diskCacheStrategy(
                DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
//            .centerCrop()
//                .placeholder(R.drawable.balloon)
            .into(image)
    }
}