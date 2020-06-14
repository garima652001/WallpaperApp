package com.georgcantor.wallpaperapp.view.fragment.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.GravityCompat.START
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.viewpager.widget.ViewPager
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.util.Constants.ARG_QUERY
import com.georgcantor.wallpaperapp.util.openActivity
import com.georgcantor.wallpaperapp.util.runDelayed
import com.georgcantor.wallpaperapp.util.shortToast
import com.georgcantor.wallpaperapp.view.activity.models.ModelsActivity
import com.georgcantor.wallpaperapp.view.fragment.pictures.PicturesFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment : Fragment(), NavigationView.OnNavigationItemSelectedListener {

    private var recentlyBackPressed = false
    private var audiPressed = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_main, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                turnViewPagerOrExit()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        navigation_view.setNavigationItemSelectedListener(this)
        navigation_view.itemIconTintList = null

        MainPagerAdapter(childFragmentManager).apply {
            addFragment(PicturesFragment.create(getString(R.string.bmw_request)))
            addFragment(PicturesFragment.create(getString(R.string.audi_request)))
            addFragment(PicturesFragment.create(getString(R.string.mercedes_request)))

            view_pager.adapter = this
        }
        try {
            view_pager.offscreenPageLimit = 3
        } catch (e: IllegalStateException) {
        }

        view_pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> {
                        bottom_nav_view.selectedItemId = R.id.nav_bmw
                        audiPressed = false
                    }
                    1 -> {
                        bottom_nav_view.selectedItemId = R.id.nav_audi
                        audiPressed = true
                    }
                    2 -> bottom_nav_view.selectedItemId = R.id.nav_mercedes
                }
            }
        })

        val itemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_bmw -> {
                    audiPressed = false
                    view_pager.currentItem = 0
                    return@OnNavigationItemSelectedListener true
                }
                R.id.nav_audi -> {
                    audiPressed = true
                    view_pager.currentItem = 1
                    return@OnNavigationItemSelectedListener true
                }
                R.id.nav_mercedes -> {
                    view_pager.currentItem = 2
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }
        bottom_nav_view.setOnNavigationItemSelectedListener(itemSelectedListener)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        context?.apply {
            when (item.itemId) {
                R.id.nav_aston -> openActivity(ModelsActivity::class.java) { putString(ARG_QUERY, getString(R.string.aston)) }
                R.id.nav_bentley -> openActivity(ModelsActivity::class.java) { putString(ARG_QUERY, getString(R.string.bentley)) }
                R.id.nav_bugatti -> openActivity(ModelsActivity::class.java) { putString(ARG_QUERY, getString(R.string.bugatti)) }
                R.id.nav_ferrari -> openActivity(ModelsActivity::class.java) { putString(ARG_QUERY, getString(R.string.ferrari)) }
                R.id.nav_lambo -> openActivity(ModelsActivity::class.java) { putString(ARG_QUERY, getString(R.string.lamborghini)) }
                R.id.nav_mclaren -> openActivity(ModelsActivity::class.java) { putString(ARG_QUERY, getString(R.string.mclaren)) }
                R.id.nav_porsche -> openActivity(ModelsActivity::class.java) { putString(ARG_QUERY, getString(R.string.porsche)) }
                R.id.nav_rolls -> openActivity(ModelsActivity::class.java) { putString(ARG_QUERY, getString(R.string.rolls)) }
                R.id.nav_favorites -> findNavController(this@MainFragment).navigate(R.id.action_mainFragment_to_favoritesFragment)
                R.id.nav_rate_us -> {
                }
            }
        }
        drawer_layout.closeDrawer(START)

        return true
    }

    private fun turnViewPagerOrExit() {
        requireActivity().drawer_layout.apply {
            if (isDrawerOpen(START)) {
                closeDrawer(START)
                return
            }
        }

        when (view_pager.currentItem) {
            0 -> {
                when (recentlyBackPressed) {
                    true -> requireActivity().finish()
                    false -> {
                        recentlyBackPressed = true
                        context?.shortToast(getString(R.string.press_back))
                    }
                }
                runDelayed(2000) { recentlyBackPressed = false }
            }
            1 -> view_pager.currentItem = 0
            2 -> view_pager.currentItem = if (audiPressed) 1 else 0
        }
    }
}