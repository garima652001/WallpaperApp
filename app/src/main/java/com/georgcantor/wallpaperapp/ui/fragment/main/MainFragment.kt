package com.georgcantor.wallpaperapp.ui.fragment.main

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.ui.fragment.pictures.PicturesFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_main, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar.title = getString(R.string.bmw)

        val title = toolbar.getChildAt(0) as TextView
        title.typeface = Typeface.create("cursive", Typeface.NORMAL)

        val adapter = MainPagerAdapter(parentFragmentManager)
        adapter.addFragment(PicturesFragment.create(getString(R.string.bmw_request)))
        adapter.addFragment(PicturesFragment.create(getString(R.string.audi_request)))
        adapter.addFragment(PicturesFragment.create(getString(R.string.mercedes_request)))

        view_pager.adapter = adapter
        view_pager.offscreenPageLimit = 3

        view_pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> bottom_nav_view.selectedItemId = R.id.nav_bmw
                    1 -> bottom_nav_view.selectedItemId = R.id.nav_audi
                    2 -> bottom_nav_view.selectedItemId = R.id.nav_mercedes
                }
            }
        })

        val itemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_bmw -> {
                    toolbar.title = getString(R.string.bmw)
                    view_pager.currentItem = 0
                    return@OnNavigationItemSelectedListener true
                }
                R.id.nav_audi -> {
                    toolbar.title = getString(R.string.audi)
                    view_pager.currentItem = 1
                    return@OnNavigationItemSelectedListener true
                }
                R.id.nav_mercedes -> {
                    toolbar.title = getString(R.string.mercedes)
                    view_pager.currentItem = 2
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }
        bottom_nav_view.setOnNavigationItemSelectedListener(itemSelectedListener)
    }
}