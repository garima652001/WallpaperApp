package com.georgcantor.wallpaperapp.view.activity.search

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.util.*
import com.georgcantor.wallpaperapp.view.activity.detail.DetailActivity
import com.georgcantor.wallpaperapp.view.fragment.pictures.PicturesAdapter
import kotlinx.android.synthetic.main.activity_search.*
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class SearchActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_CODE = 111
        private const val PERMISSION_REQUEST_CODE = 222
    }

    private var index = 1

    private lateinit var viewModel: SearchViewModel
    private lateinit var adapter: PicturesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        setContentView(R.layout.activity_search)

        viewModel = getViewModel { parametersOf() }
        setupToolbar()
        initViews()

        search_view.requestFocus()
        search_view.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                search_view.hideKeyboard()
                adapter.clearPictures()
                search(search_view.text.toString().trim { it <= ' ' }, index)
                return@OnEditorActionListener true
            }
            false
        })

        with(viewModel) {
            isNetworkAvailable.observe(this@SearchActivity) { available ->
                no_internet_image.visibility = if (available) View.GONE else View.VISIBLE
            }

            isProgressVisible.observe(this@SearchActivity) { visible ->
                if (visible) progress_animation.showAnimation() else progress_animation.hideAnimation()
            }

            error.observe(this@SearchActivity, this@SearchActivity::shortToast)

            pictures.observe(this@SearchActivity) {
                runDelayed(500) {
                    when (adapter.itemCount) {
                        0 -> search_error_anim.showAnimation()
                        else -> search_error_anim.hideAnimation()
                    }
                }
                adapter.setPictures(it)
            }
        }
    }

//    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
//        val cancel = menu?.findItem(R.id.action_cancel)
//        cancel?.isVisible = viewModel.isSearchingActive.value == true
//        return true
//    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_cancel -> {
                search_error_anim?.hideAnimation()
//                viewModel.isSearchingActive.value = false
                search_view.setText("")
                search_view.showKeyboard()
            }
            R.id.action_voice_search -> {} //checkPermission()
        }
        return super.onOptionsItemSelected(item)
    }

//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<String>,
//        grantResults: IntArray
//    ) {
//        if (requestCode == PERMISSION_REQUEST_CODE && grantResults.isNotEmpty()) {
//            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                speak()
//            }
//        }
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
//            val arrayList = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
//            arrayList?.toString()?.let { search(it, index) }
//
//            val word = arrayList?.get(0)
//            search_view.setText(word)
//            adapter.clearPictures()
//            search(word ?: "", 1)
//        }
//    }

    override fun onBackPressed() {
        super.onBackPressed()
        search_view.hideKeyboard()
        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right)
    }

    private fun setupToolbar() {
        supportActionBar?.setDisplayShowTitleEnabled(false)
        setSupportActionBar(search_toolbar)
        search_toolbar.navigationIcon = ContextCompat.getDrawable(this, R.drawable.ic_arrow_back)
        search_toolbar.setNavigationOnClickListener {
            super.onBackPressed()
            search_view.hideKeyboard()
            overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right)
        }
    }

    private fun initViews() {
        val staggeredGridLayoutManager = StaggeredGridLayoutManager(
            getScreenSize(),
            StaggeredGridLayoutManager.VERTICAL
        )
        search_recycler.layoutManager = staggeredGridLayoutManager
        adapter = PicturesAdapter {
            openActivity(DetailActivity::class.java) { putParcelable(Constants.ARG_DETAIL, it) }
        }
        search_recycler.adapter = adapter

        val query = search_view.text.toString().trim { it <= ' ' }

        val listener = object : EndlessScrollListener(staggeredGridLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
                search(query, page)
            }
        }
        search_recycler.addOnScrollListener(listener)

        refresh_layout.setOnRefreshListener {
            search(query, 1)
            refresh_layout.isRefreshing = false
        }
    }

//    private fun checkPermission() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
//                != PackageManager.PERMISSION_GRANTED
//            ) {
//                requestAudioPermission()
//            } else {
//                speak()
//            }
//        } else {
//            speak()
//        }
//    }

//    private fun speak() {
//        try {
//            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
//            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.speak_something))
//            startActivityForResult(intent, REQUEST_CODE)
//        } catch (e: Exception) {
//            shortToast(getString(R.string.something_went_wrong))
//        }
//    }

//    private fun requestAudioPermission() {
//        ActivityCompat.requestPermissions(
//            this,
//            arrayOf(Manifest.permission.RECORD_AUDIO),
//            PERMISSION_REQUEST_CODE
//        )
//    }

    private fun search(query: String, index: Int) = viewModel.getPictures(query, index)
}
