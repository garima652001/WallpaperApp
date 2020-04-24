package com.georgcantor.wallpaperapp.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.util.*
import com.georgcantor.wallpaperapp.view.adapter.PicturesAdapter
import com.georgcantor.wallpaperapp.viewmodel.SearchViewModel
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_common.*
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class AudiFragment : Fragment() {

    private lateinit var viewModel: SearchViewModel
    private var adapter: PicturesAdapter? = null
    private val disposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = getViewModel { parametersOf() }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_common, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!requireActivity().isNetworkAvailable()) {
            noInternetImageView.visible()
        }

        refreshLayout.setOnRefreshListener {
            loadData(1)
            refreshLayout.isRefreshing = false
        }

        val gridLayoutManager = StaggeredGridLayoutManager(
            requireContext().getScreenSize(),
            StaggeredGridLayoutManager.VERTICAL
        )
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = gridLayoutManager

        val scrollListener = object : EndlessScrollListener(gridLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
                loadData(page)
            }
        }
        scrollListener.resetState()
        recyclerView.addOnScrollListener(scrollListener)
        adapter = PicturesAdapter(requireContext())
        recyclerView.adapter = adapter

        loadData(1)
    }

    override fun onDestroy() {
        disposable.dispose()
        super.onDestroy()
    }

    private fun loadData(index: Int) {
        disposable.add(
            viewModel.getPics(getString(R.string.audi_request), index)
                .doOnSubscribe { animationView?.showAnimation() }
                .doFinally { animationView?.hideAnimation() }
                .subscribe({
                    adapter?.setPictures(it)
                    if (it.isNullOrEmpty()) {
                        viewModel.getPixabayPictures(getString(R.string.audi_request), index)
                            .subscribe({
                                adapter?.setPictures(it)
                            }, {
                                viewModel.getPics(getString(R.string.audi_request), index)
                                    .subscribe({
                                        adapter?.setPictures(it)
                                    }, {
                                    })
                            })
                    }
                }, {
                    viewModel.getPixabayPictures(getString(R.string.audi_request), index)
                        .subscribe({
                            adapter?.setPictures(it)
                        }, {
                            viewModel.getPics(getString(R.string.audi_request), index)
                                .subscribe({
                                    adapter?.setPictures(it)
                                }, {
                                })
                        })
                })
        )
    }
}