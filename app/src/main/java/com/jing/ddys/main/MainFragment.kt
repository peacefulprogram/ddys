package com.jing.ddys.main

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.Fragment
import androidx.leanback.app.ProgressBarManager
import androidx.leanback.paging.PagingDataAdapter
import androidx.leanback.widget.*
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import coil.load
import com.jing.ddys.R
import com.jing.ddys.databinding.FragmentMainBinding
import com.jing.ddys.databinding.VideoCardLayoutBinding
import com.jing.ddys.detail.DetailActivity
import com.jing.ddys.repository.VideoCardInfo
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainFragment : Fragment() {

    private val TAG = MainFragment::class.java.simpleName

    lateinit var viewBinding: FragmentMainBinding

    lateinit var progressBarManager: ProgressBarManager

    private lateinit var viewModel: MainViewModel

    var changeCategoryJob: Job? = null

    var selectCategoryIndex = 0

    var selectCategoryColor = 0

    var unselectCategoryColor = 0

    private lateinit var videoPagingAdapter: PagingDataAdapter<VideoCardInfo>

    private lateinit var videoBridgeAdapter: ItemBridgeAdapter

    private lateinit var shadowOverlayHelper: ShadowOverlayHelper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        selectCategoryColor = ContextCompat.getColor(requireContext(), R.color.gray50)
        unselectCategoryColor = ContextCompat.getColor(requireContext(), R.color.gray400)
        viewBinding = FragmentMainBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        progressBarManager = ProgressBarManager().apply {
            enableProgressBar()
            initialDelay = 0L
            setRootView(viewBinding.root)
        }
        initCategoryRow()
        initVideoGrid()
        lifecycleScope.launch {
            viewModel.pager.collectLatest {
                videoPagingAdapter.submitData(it)
            }
        }

        videoPagingAdapter.addLoadStateListener {
            when (val refreshState = it.refresh) {
                is LoadState.Error -> {
                    Toast.makeText(
                        requireContext(),
                        "请求数据错误:${refreshState.error.message}",
                        Toast.LENGTH_LONG
                    ).show()
                    Log.e(TAG, "请求数据错误: ${refreshState.error.message}", refreshState.error)
                }
                is LoadState.NotLoading -> {
                    if (videoPagingAdapter.size() == 0) {
                        Toast.makeText(requireContext(), "未请求到数据", Toast.LENGTH_SHORT).show()
                    }
                }
                LoadState.Loading -> {}
            }
        }

        return viewBinding.root
    }

    private fun initVideoGrid() {
        videoBridgeAdapter = ItemBridgeAdapter()
        videoPagingAdapter =
            PagingDataAdapter(NewVideoCardPresenter(requireContext()), videoCardDiff).apply {
                var startRefresh = false
                addLoadStateListener {
                    when (it.refresh) {
                        LoadState.Loading -> {
                            startRefresh = true
                            progressBarManager.show()
                        }
                        else -> {
                            progressBarManager.hide()
                            if (startRefresh) {
                                startRefresh = false
                                if (videoBridgeAdapter.itemCount > 0) {
                                    viewBinding.videoGrid.selectedPosition = 0
                                }
                            }
                        }
                    }
                }
            }
        videoBridgeAdapter = ItemBridgeAdapter(videoPagingAdapter)
//        shadowOverlayHelper = ShadowOverlayHelper.Builder()
//            .needsOverlay(false)
//            .needsShadow(true)
//            .needsRoundedCorner(false)
//            .preferZOrder(false)
//            .keepForegroundDrawable(true)
//            .options(ShadowOverlayHelper.Options.DEFAULT)
//            .build(requireContext())
//        if (shadowOverlayHelper.needsWrapper()) {
//            videoBridgeAdapter.wrapper = ItemBridgeAdapterShadowOverlayWrapper(shadowOverlayHelper)
//        }

        FocusHighlightHelper.setupBrowseItemFocusHighlight(
            videoBridgeAdapter,
            FocusHighlight.ZOOM_FACTOR_MEDIUM,
            false
        )
        val columnsCount = 5
        viewBinding.videoGrid.apply {
            setNumColumns(columnsCount)
            val hGap = requireContext().resources.getDimension(R.dimen.video_card_h_gap).toInt()
            val vGap = requireContext().resources.getDimension(R.dimen.video_card_v_gap).toInt()
            addItemDecoration(object : ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    outRect.left = hGap
                    outRect.top = vGap
                    outRect.bottom = vGap
                }
            })

            focusSearchInterceptor = { focused, focusedPosition, direction ->
                if (direction == RecyclerView.FOCUS_UP && focusedPosition in 0 until columnsCount) {
                    viewBinding.categoryRow
                } else if (direction == RecyclerView.FOCUS_LEFT && focusedPosition % columnsCount == 0) {
                    focused
                } else {
                    null
                }
            }
            adapter = videoBridgeAdapter

        }
    }

    private fun initCategoryRow() {
        val categoryList = listOf(
            Pair("/", "首页"),
            Pair("/category/anime/new-bangumi/", "本季新番"),
            Pair("/category/airing/", "连载剧集"),
            Pair("/category/movie/", "电影"),
            Pair("/category/drama/kr-drama/", "韩剧"),
            Pair("/category/anime/", "动画"),
            Pair("/category/movie/western-movie/", "欧美电影"),
            Pair("/category/movie/asian-movie/", "日韩电影"),
            Pair("/category/movie/chinese-movie/", "华语电影"),
            Pair("/tag/douban-top250/", "豆瓣TOP250"),
            Pair("/category/drama/western-drama/", "欧美剧"),
            Pair("/category/drama/cn-drama/", "华语剧"),
            Pair("/category/drama/jp-drama/", "日剧"),
        )
        viewBinding.categoryRow.apply {
            setNumRows(1)
            adapter = CategoryAdapter(categoryList) {
                changeCategory(it)
            }
            viewTreeObserver.addOnGlobalFocusChangeListener { oldView, newView ->
                var newViewInTab = false
                if (newView?.parent == viewBinding.categoryRow) {
                    newViewInTab = true
                    newView.findViewById<TextView>(R.id.category_name)
                        .setTextColor(selectCategoryColor)
                }
                if (oldView?.parent == viewBinding.categoryRow) {
                    if (newViewInTab) {
                        oldView.findViewById<TextView>(R.id.category_name)
                            .setTextColor(unselectCategoryColor)
                    }
                }
            }
        }
    }

    fun changeCategory(url: String) {
        if (url == viewModel.category) {
            return
        }
        viewModel.onCategoryChoose(url)
        changeCategoryJob?.cancel()
        changeCategoryJob = lifecycleScope.launch {
            delay(500L)
            videoPagingAdapter.refresh()
        }
    }

    private inner class CategoryAdapter(
        private val categoryList: List<Pair<String, String>>,
        private val onCategorySelect: (String) -> Unit
    ) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val root = LayoutInflater.from(parent.context)
                .inflate(R.layout.video_category_item, parent, false)
            return object : RecyclerView.ViewHolder(root) {}
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val category = categoryList[position]
            val color =
                if (position == selectCategoryIndex) selectCategoryColor else unselectCategoryColor
            holder.itemView.apply {
                findViewById<TextView>(R.id.category_name).apply {
                    text = category.second
                    setTextColor(color)
                }
                setOnFocusChangeListener { _, hasFocus ->
                    if (hasFocus) {
                        onCategorySelect(category.first)
                    }
                }
                if (requireActivity().currentFocus == null && position == selectCategoryIndex) {
                    requestFocus()
                }
            }
        }

        override fun getItemCount(): Int = categoryList.size

    }

    private val videoCardDiff = object : DiffUtil.ItemCallback<VideoCardInfo>() {
        override fun areItemsTheSame(oldItem: VideoCardInfo, newItem: VideoCardInfo): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: VideoCardInfo, newItem: VideoCardInfo): Boolean {
            return oldItem.imageUrl == newItem.imageUrl
                    && oldItem.title == newItem.title
                    && oldItem.subTitle == newItem.subTitle
        }

    }

    private inner class NewVideoCardPresenter(context: Context) : Presenter() {
        val mDefaultCardImage = ContextCompat.getColor(context, R.color.gray900).toDrawable()
        override fun onCreateViewHolder(parent: ViewGroup?): ViewHolder {
            val vb = VideoCardLayoutBinding.inflate(LayoutInflater.from(parent!!.context))
            vb.root.setTag(R.id.view_binding_tag, vb)
            return ViewHolder(vb.root)
        }

        override fun onBindViewHolder(viewHolder: ViewHolder?, item: Any?) {
            val video = item as VideoCardInfo
            with(viewHolder!!.view.getTag(R.id.view_binding_tag) as VideoCardLayoutBinding) {
                root.setOnClickListener {
                    DetailActivity.navigateTo(requireContext(), item.url)
                }
                title.text = item.title
                subTitle.text = item.subTitle ?: ""
                if (video.imageUrl.isEmpty()) {
                    cover.setImageDrawable(mDefaultCardImage)
                } else {
                    cover.load(video.imageUrl) {
                        error(mDefaultCardImage)
                    }
                }
            }
        }

        override fun onUnbindViewHolder(viewHolder: ViewHolder?) {
        }

    }


}