package com.georgcantor.wallpaperapp.view.fragment.pictures

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat.START
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager.VERTICAL
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.util.*
import com.georgcantor.wallpaperapp.util.Constants.ARG_DETAIL
import com.georgcantor.wallpaperapp.util.Constants.ARG_QUERY
import com.georgcantor.wallpaperapp.view.activity.categories.CategoriesActivity
import com.georgcantor.wallpaperapp.view.activity.detail.DetailActivity
import com.georgcantor.wallpaperapp.view.activity.search.SearchActivity
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.fragment_pictures.*
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class PicturesFragment : Fragment(), Toolbar.OnMenuItemClickListener {

    companion object {
        fun create(query: String): PicturesFragment {
            return PicturesFragment().apply {
                arguments = Bundle().apply { putString(ARG_QUERY, query) }
            }
        }
    }

    private lateinit var viewModel: PicturesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = getViewModel { parametersOf() }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_pictures, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        val query = arguments?.get(ARG_QUERY) as String

        with(toolbar) {
            title = query
            navigationIcon = getDrawable(requireContext(), R.drawable.ic_drawer_opener)
            setNavigationOnClickListener {
                requireActivity().drawer_layout.openDrawer(START)
            }

            inflateMenu(R.menu.menu_main)
            setOnMenuItemClickListener(this@PicturesFragment)
        }

        val adapter = PicturesAdapter {
            context?.openActivity(DetailActivity::class.java) { putParcelable(ARG_DETAIL, it) }
        }
        pictures_recycler.adapter = adapter

        val gridLayoutManager = StaggeredGridLayoutManager(requireContext().getScreenSize(), VERTICAL)

        pictures_recycler.layoutManager = gridLayoutManager

        val scrollListener = object : EndlessScrollListener(gridLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
                viewModel.getPictures(query, page)
            }
        }
        scrollListener.resetState()

        pictures_recycler.addOnScrollListener(scrollListener)

        with(viewModel) {
            isNetworkAvailable.observe(viewLifecycleOwner) { available ->
                no_internet_image.visibility = if (available) GONE else VISIBLE
            }

            isProgressVisible.observe(viewLifecycleOwner) { visible ->
                if (visible) progress_animation.showAnimation() else progress_animation.hideAnimation()
            }

            error.observe(viewLifecycleOwner) { requireActivity()::shortToast }

            pictures.observe(viewLifecycleOwner, adapter::setPictures)

            getPictures(query, 1)

            refresh_layout.setOnRefreshListener {
                getPictures(query, 1)
                refresh_layout.isRefreshing = false
            }
        }
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_categories -> {
                context?.openActivity(CategoriesActivity::class.java)
                return true
            }
            R.id.action_search -> {
                context?.openActivity(SearchActivity::class.java)
                return true
            }
        }
        return false
    }
}