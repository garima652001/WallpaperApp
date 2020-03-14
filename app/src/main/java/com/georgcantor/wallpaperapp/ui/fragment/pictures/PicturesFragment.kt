package com.georgcantor.wallpaperapp.ui.fragment.pictures

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.util.EndlessScrollListener
import com.georgcantor.wallpaperapp.util.getScreenSize
import com.georgcantor.wallpaperapp.util.shortToast
import com.georgcantor.wallpaperapp.view.adapter.PicturesAdapter
import kotlinx.android.synthetic.main.fragment_pictures.*
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class PicturesFragment : Fragment() {

    companion object {
        private const val ARG_QUERY = "query"

        fun create(query: String): PicturesFragment {
            return PicturesFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_QUERY, query)
                }
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

        val query = arguments?.get(ARG_QUERY) as String

        val adapter = PicturesAdapter(requireContext())
        pictures_recycler.adapter = adapter

        val gridLayoutManager = StaggeredGridLayoutManager(
            requireContext().getScreenSize(),
            StaggeredGridLayoutManager.VERTICAL
        )

        pictures_recycler.layoutManager = gridLayoutManager

        val scrollListener = object : EndlessScrollListener(gridLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
                viewModel.getPictures(query, page)
            }
        }
        scrollListener.resetState()
        pictures_recycler.addOnScrollListener(scrollListener)

        viewModel.error.observe(viewLifecycleOwner, Observer(requireActivity()::shortToast))

        viewModel.pictures.observe(viewLifecycleOwner, Observer(adapter::setPictures))

        viewModel.getPictures(query, 1)
    }
}