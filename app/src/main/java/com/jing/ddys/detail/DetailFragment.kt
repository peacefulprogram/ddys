package com.jing.ddys.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.leanback.app.ProgressBarManager
import androidx.leanback.app.RowsSupportFragment
import androidx.leanback.widget.*
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import coil.load
import com.jing.ddys.R
import com.jing.ddys.databinding.SmallVideoCardLayoutBinding
import com.jing.ddys.databinding.VideoDetailRowBinding
import com.jing.ddys.ext.showLongToast
import com.jing.ddys.playback.VideoPlaybackActivity
import com.jing.ddys.repository.*
import com.jing.ddys.util.Presenter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class DetailFragment(private val detailPageUrl: String) : RowsSupportFragment() {

    private lateinit var viewModel: DetailViewModel

    private lateinit var progressBarManager: ProgressBarManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[DetailViewModel::class.java]
        viewModel.queryDetail(detailPageUrl)
        onItemViewClickedListener = OnItemViewClickedListener { _, item, _, _ ->
            when (item) {
                is VideoSeason -> if (item.seasonUrl != null) {
                    adapter = createRowsAdapter()
                    viewModel.queryDetail(item.seasonUrl)
                }
                is VideoCardInfo -> DetailActivity.navigateTo(requireContext(), item.url)
                is VideoEpisode -> {
                    val resource = viewModel.detailFlow.value
                    if (resource is Resource.Success) {
                        val videoInfo = resource.data
                        VideoPlaybackActivity.navigateTo(
                            requireContext(),
                            videoInfo,
                            videoInfo.episodes.indexOf(item)
                        )
                    }
                }
            }

        }
    }

    private fun createRowsAdapter(): ArrayObjectAdapter {

        val presenterSelector = ClassPresenterSelector().apply {
            addClassPresenter(VideoDetailInfo::class.java, InfoRowPresenter().apply {
                selectEffectEnabled = false
            })
            addClassPresenter(ListRow::class.java, ListRowPresenter().apply {
                selectEffectEnabled = false
            })
        }
        return ArrayObjectAdapter(presenterSelector)
    }

    private fun renderDetailPage(detailInfo: VideoDetailInfo) {
        val rowsAdapter = createRowsAdapter()
        rowsAdapter.add(detailInfo)
        if (detailInfo.seasons.isNotEmpty()) {
            val seasonAdapter = ArrayObjectAdapter(Presenter {
                onCreateViewHolder {
                    val view = TextView(requireContext())
                    view.focusable = View.FOCUSABLE
                    view.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.season_and_ep_border)
                    Presenter.ViewHolder(view)
                }
                onBindViewHolder { viewHolder, item ->
                    val season = item as VideoSeason
                    val textView = viewHolder.view as TextView
                    textView.text = "第${season.seasonName}季"
                    if (season.currentSeason) {
                        textView.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.cyan300
                            )
                        )
                    }
                }
            })
            detailInfo.seasons.forEach { seasonAdapter.add(it) }
            rowsAdapter.add(ListRow(HeaderItem("选季"), seasonAdapter))
        }

        val episodeAdapter = ArrayObjectAdapter(Presenter {
            onCreateViewHolder {
                val view = TextView(requireContext())
                view.focusable = View.FOCUSABLE
                view.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.season_and_ep_border)
                Presenter.ViewHolder(view)
            }

            onBindViewHolder { viewHolder, item ->
                val episode = item as VideoEpisode
                val textView = viewHolder.view as TextView
                textView.text = episode.name
            }
        })
        detailInfo.episodes.forEach { episodeAdapter.add(it) }
        rowsAdapter.add(ListRow(HeaderItem("选集"), episodeAdapter))

        if (detailInfo.relatedVideo.isNotEmpty()) {
            val relatedAdapter = ArrayObjectAdapter(Presenter {
                onCreateViewHolder {
                    val vb =
                        SmallVideoCardLayoutBinding.inflate(LayoutInflater.from(requireContext()))
                    vb.root.setTag(R.id.view_binding_tag, vb)
                    Presenter.ViewHolder(vb.root)
                }

                onBindViewHolder { viewHolder, item ->
                    val video = item as VideoCardInfo
                    with(viewHolder.view.getTag(R.id.view_binding_tag) as SmallVideoCardLayoutBinding) {
                        if (video.imageUrl.isEmpty()) {
                            cover.setImageDrawable(0.toDrawable())
                        } else {
                            cover.load(video.imageUrl)
                        }
                        title.text = video.title
                    }

                }
            })
            detailInfo.relatedVideo.forEach { relatedAdapter.add(it) }
            rowsAdapter.add(ListRow(HeaderItem("相关视频"), relatedAdapter))
        }
        adapter = rowsAdapter
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val grid = super.onCreateView(inflater, container, savedInstanceState)
        val frameLayout = inflater.inflate(R.layout.fragment_detail, container, false) as ViewGroup
        frameLayout.addView(grid)
        progressBarManager = ProgressBarManager().apply {
            enableProgressBar()
            initialDelay = 0L
            setRootView(frameLayout)
        }
        return frameLayout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            viewModel.detailFlow.collectLatest {
                when (it) {
                    Resource.Loading -> progressBarManager.show()
                    is Resource.Success -> {
                        renderDetailPage(it.data)
                        progressBarManager.hide()
                    }
                    is Resource.Error -> {
                        requireContext().showLongToast(it.message)
                        progressBarManager.hide()
                    }
//                    else -> {}
                }
            }
        }
    }


    private class InfoRowPresenter : RowPresenter() {

        init {
            headerPresenter = null
        }

        override fun createRowViewHolder(parent: ViewGroup?): ViewHolder {
            val vb = VideoDetailRowBinding.inflate(LayoutInflater.from(parent!!.context))
            vb.root.focusable = View.FOCUSABLE
            vb.root.setTag(R.id.view_binding_tag, vb)
            return ViewHolder(vb.root)
        }

        override fun onBindRowViewHolder(vh: ViewHolder?, item: Any?) {
            val info = item as VideoDetailInfo
            with(vh!!.view.getTag(R.id.view_binding_tag) as VideoDetailRowBinding) {
                videoCover.load(info.coverUrl)
                title.text = info.title
                description.text = info.description
                root.requestFocus()
            }
        }

        override fun onBindViewHolder(
            viewHolder: Presenter.ViewHolder?,
            item: Any?,
            payloads: MutableList<Any>?
        ) {
            super.onBindViewHolder(viewHolder, item, payloads)
        }

    }

}