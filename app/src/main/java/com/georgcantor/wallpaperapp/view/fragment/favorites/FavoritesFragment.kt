package com.georgcantor.wallpaperapp.view.fragment.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager.VERTICAL
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.model.data.CommonPic
import com.georgcantor.wallpaperapp.model.local.Favorite
import com.georgcantor.wallpaperapp.util.*
import com.georgcantor.wallpaperapp.util.Constants.ARG_DETAIL
import com.georgcantor.wallpaperapp.view.activity.detail.DetailActivity
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_favorites.*
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class FavoritesFragment : Fragment() {

    private lateinit var viewModel: FavoritesViewModel
    private lateinit var linearLayoutManager: RecyclerView.LayoutManager
    private lateinit var gridLayoutManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = getViewModel { parametersOf() }

        linearLayoutManager = LinearLayoutManager(requireContext())
        gridLayoutManager = StaggeredGridLayoutManager(context?.getScreenSize() ?: 2, VERTICAL)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_favorites, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(toolbar) {
            title = getString(R.string.favorites_toolbar)
            navigationIcon = getDrawable(requireContext(), R.drawable.ic_arrow_back)
            setNavigationOnClickListener {
                activity?.onBackPressed()
                activity?.overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right)
            }
        }

        with(viewModel) {
            isProgressVisible.observe(viewLifecycleOwner) { visible ->
                if (visible) progress_animation.showAnimation() else progress_animation.hideAnimation()
            }

            error.observe(viewLifecycleOwner) { requireActivity()::shortToast }

            favorites.observe(viewLifecycleOwner) { favorites ->
                if (favorites.isNullOrEmpty()) empty_anim_view.showAnimation() else empty_anim_view.hideAnimation()

                val isNotGrid = favorites.size in 1..4
                setupRecyclerView(if (isNotGrid) linearLayoutManager else gridLayoutManager)
                favorites_recycler.adapter = FavoritesAdapter(
                    favorites as MutableList<Favorite>,
                    isNotGrid,
                    {
                        val pic = Gson().fromJson(it.image, CommonPic::class.java)

                        context?.openActivity(DetailActivity::class.java) {
                            putParcelable(
                                ARG_DETAIL,
                                CommonPic(
                                    url = pic.url,
                                    width = pic.width,
                                    height = pic.height,
                                    tag = pic.tag,
                                    imageUrl = pic.imageUrl,
                                    fullHdUrl = pic.fullHdUrl,
                                    id = pic.id
                                )
                            )
                        }
                    }
                ) { }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getFavorites()
    }

    private fun setupRecyclerView(manager: RecyclerView.LayoutManager) {
        favorites_recycler.setHasFixedSize(true)
        favorites_recycler.layoutManager = manager
    }
}