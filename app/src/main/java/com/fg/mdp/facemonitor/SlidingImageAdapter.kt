package com.fg.mdp.facemonitor

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.fg.mdp.facemonitor.model.Imagebg
import kotlin.collections.ArrayList


class SlidingImageAdapter(fragmentManager: FragmentManager) : FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {


    var imageBG = ArrayList<Imagebg>()


    fun updateData(imageBG_: ArrayList<Imagebg>) {
        this.imageBG = imageBG_
        notifyDataSetChanged()
    }

    override fun getItem(position: Int): Fragment = SlidingImageFragment(imageBG[position].url)

    override fun getCount(): Int = imageBG.size
}
