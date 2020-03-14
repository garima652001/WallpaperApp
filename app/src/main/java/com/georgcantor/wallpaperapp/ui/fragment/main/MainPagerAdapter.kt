package com.georgcantor.wallpaperapp.ui.fragment.main

import android.os.Parcelable
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class MainPagerAdapter(manager: FragmentManager) :
    FragmentStatePagerAdapter(manager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private val fragments = ArrayList<Fragment>()

    override fun getItem(position: Int): Fragment = fragments[position]

    override fun getCount(): Int = fragments.size

    fun addFragment(fragment: Fragment) {
        fragments.add(fragment)
    }

    override fun restoreState(state: Parcelable?, loader: ClassLoader?) {
        try {
            super.restoreState(state, loader)
        } catch (e: IllegalStateException) {
        }
    }
}