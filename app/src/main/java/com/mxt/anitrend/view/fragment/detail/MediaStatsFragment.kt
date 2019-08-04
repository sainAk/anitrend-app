package com.mxt.anitrend.view.fragment.detail

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.annimon.stream.IntPair
import com.annimon.stream.Stream
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.mxt.anitrend.R
import com.mxt.anitrend.adapter.recycler.detail.LinkAdapter
import com.mxt.anitrend.adapter.recycler.detail.RankAdapter
import com.mxt.anitrend.base.custom.fragment.FragmentBase
import com.mxt.anitrend.base.interfaces.event.ItemClickListener
import com.mxt.anitrend.databinding.FragmentSeriesStatsBinding
import com.mxt.anitrend.model.entity.anilist.ExternalLink
import com.mxt.anitrend.model.entity.anilist.Media
import com.mxt.anitrend.model.entity.anilist.MediaRank
import com.mxt.anitrend.model.entity.anilist.meta.ScoreDistribution
import com.mxt.anitrend.model.entity.container.request.QueryContainerBuilder
import com.mxt.anitrend.presenter.fragment.MediaPresenter
import com.mxt.anitrend.util.ChartUtil
import com.mxt.anitrend.util.CompatUtil
import com.mxt.anitrend.util.GraphUtil
import com.mxt.anitrend.util.KeyUtil
import com.mxt.anitrend.util.MediaBrowseUtil
import com.mxt.anitrend.util.MediaUtil
import com.mxt.anitrend.view.activity.detail.MediaBrowseActivity
import java.util.Locale

import butterknife.ButterKnife

/**
 * Created by max on 2017/12/28.
 */

class MediaStatsFragment : FragmentBase<Media, MediaPresenter, Media>() {

    private var binding: FragmentSeriesStatsBinding? = null
    private var model: Media? = null

    private var rankAdapter: RankAdapter? = null
    private var linkAdapter: LinkAdapter? = null

    private var mediaId: Long = 0
    @KeyUtil.MediaType
    private var mediaType: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mediaId = arguments!!.getLong(KeyUtil.getArg_id())
            mediaType = arguments!!.getString(KeyUtil.getArg_mediaType())
        }
        setIsMenuDisabled(true)
        mColumnSize = R.integer.grid_list_x2
        setPresenter(MediaPresenter(context))
        setViewModel(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSeriesStatsBinding.inflate(inflater, container, false)
        unbinder = ButterKnife.bind(this, binding!!.root)
        binding!!.stateLayout.showLoading()
        binding!!.linksRecycler.layoutManager =
            StaggeredGridLayoutManager(resources.getInteger(mColumnSize), StaggeredGridLayoutManager.VERTICAL)
        binding!!.linksRecycler.setHasFixedSize(true)
        binding!!.rankingRecycler.layoutManager =
            StaggeredGridLayoutManager(resources.getInteger(mColumnSize), StaggeredGridLayoutManager.VERTICAL)
        binding!!.rankingRecycler.setHasFixedSize(true)
        return binding!!.root
    }

    override fun onStart() {
        super.onStart()
        makeRequest()
    }

    /**
     * Is automatically called in the @onStart Method if overridden in list implementation
     */
    override fun updateUI() {
        binding!!.model = model
        if (rankAdapter == null) {
            rankAdapter = RankAdapter(context!!)
            rankAdapter!!.onItemsInserted(model!!.rankings)
            rankAdapter!!.clickListener = object : ItemClickListener<MediaRank> {
                override fun onItemClick(target: View, data: IntPair<MediaRank>) {
                    val intent = Intent(activity, MediaBrowseActivity::class.java)
                    val args = Bundle()
                    val queryContainer = GraphUtil.getDefaultQuery(true)
                        .putVariable(KeyUtil.getArg_type(), mediaType)
                        .putVariable(KeyUtil.getArg_format(), data.second.format)

                    if (MediaUtil.isAnimeType(model))
                        queryContainer.putVariable(KeyUtil.getArg_season(), data.second.season)

                    if (!data.second.isAllTime) {
                        if (MediaUtil.isAnimeType(model))
                            queryContainer.putVariable(KeyUtil.getArg_seasonYear(), data.second.year)
                        else
                            queryContainer.putVariable(
                                KeyUtil.getArg_startDateLike(), String.format(
                                    Locale.getDefault(),
                                    "%d%%", data.second.year
                                )
                            )
                    }

                    when (data.second.type) {
                        KeyUtil.getRATED() -> queryContainer.putVariable(
                            KeyUtil.getArg_sort(),
                            KeyUtil.getSCORE() + KeyUtil.getDESC()
                        )
                        KeyUtil.getPOPULAR() -> queryContainer.putVariable(
                            KeyUtil.getArg_sort(),
                            KeyUtil.getPOPULARITY() + KeyUtil.getDESC()
                        )
                    }

                    args.putParcelable(KeyUtil.getArg_graph_params(), queryContainer)
                    args.putParcelable(
                        KeyUtil.getArg_media_util(), MediaBrowseUtil()
                            .setCompactType(true)
                            .setFilterEnabled(false)
                    )
                    args.putString(KeyUtil.getArg_activity_tag(), data.second.typeHtmlPlainTitle)
                    intent.putExtras(args)
                    startActivity(intent)
                }

                override fun onItemLongClick(target: View, data: IntPair<MediaRank>) {

                }
            }
        }

        if (linkAdapter == null) {
            linkAdapter = LinkAdapter(context!!)
            linkAdapter!!.onItemsInserted(model!!.externalLinks)
            linkAdapter!!.clickListener = object : ItemClickListener<ExternalLink> {
                override fun onItemClick(target: View, data: IntPair<ExternalLink>) {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse(data.second.url)
                    startActivity(intent)
                }

                override fun onItemLongClick(target: View, data: IntPair<ExternalLink>) {

                }
            }
        }

        binding!!.stateLayout.showContent()
        binding!!.linksRecycler.adapter = linkAdapter
        binding!!.rankingRecycler.adapter = rankAdapter
        showStatusDistribution()
        showScoreDistribution()
    }

    /**
     * All new or updated network requests should be handled in this method
     */
    override fun makeRequest() {
        val queryContainer = GraphUtil.getDefaultQuery(false)
            .putVariable(KeyUtil.getArg_id(), mediaId)
            .putVariable(KeyUtil.getArg_type(), mediaType)
        viewModel.params.putParcelable(KeyUtil.getArg_graph_params(), queryContainer)
        viewModel.requestData(KeyUtil.getMEDIA_STATS_REQ(), context!!)
    }

    private fun showScoreDistribution() {
        if (model!!.stats != null && model!!.stats.scoreDistribution != null) {

            val barEntries = presenter.getMediaScoreDistribution(model!!.stats.scoreDistribution)

            val barDataSet = BarDataSet(barEntries, getString(R.string.title_score_distribution))

            configureScoreDistribution(model!!.stats.scoreDistribution)

            if (context != null)
                barDataSet.setColor(CompatUtil.getColorFromAttr(context, R.attr.colorAccent), 200)

            barDataSet.valueTextColor = CompatUtil.getColorFromAttr(context, R.attr.titleColor)
            val barData = BarData(barDataSet)
            barData.barWidth = 0.6f

            binding!!.seriesScoreDist.data = barData
            binding!!.seriesScoreDist.disableScroll()
            binding!!.seriesScoreDist.setFitBars(true)
            binding!!.seriesScoreDist.setPinchZoom(false)
            binding!!.seriesScoreDist.isDoubleTapToZoomEnabled = false
            binding!!.seriesScoreDist.invalidate()
        }
    }

    private fun showStatusDistribution() {
        if (model!!.stats != null && model!!.stats.statusDistribution != null) {
            configureSeriesStats()

            val pieEntries = presenter.getMediaStats(model!!.stats.statusDistribution)
            val pieDataSet = PieDataSet(pieEntries, getString(R.string.title_series_stats))
            pieDataSet.sliceSpace = 3f

            // Set legend and section colors with a moderate ~ 20% transparency
            pieDataSet.setColors(
                Color.parseColor("#c26fc1ea"),
                Color.parseColor("#c248c76d"),
                Color.parseColor("#c2f7464a"),
                Color.parseColor("#c29256f3"),
                Color.parseColor("#c2fba640")
            )

            val pieData = PieData(pieDataSet)
            if (context != null)
                pieData.setValueTextColor(CompatUtil.getColorFromAttr(context, R.attr.titleColor))

            pieData.setValueTextSize(9f)
            pieData.setValueFormatter(PercentFormatter())

            binding!!.seriesStats.legend.textColor = CompatUtil.getColorFromAttr(context, R.attr.titleColor)
            binding!!.seriesStats.setHoleColor(CompatUtil.getColorFromAttr(context, R.attr.color))
            binding!!.seriesStats.data = pieData
            binding!!.seriesStats.invalidate()
        }
    }

    /**
     * Called when the model state is changed.
     *
     * @param model The new data
     */
    override fun onChanged(model: Media?) {
        if (model != null) {
            this.model = model
            updateUI()
        } else
            binding!!.stateLayout.showError(
                CompatUtil.getDrawable(context, R.drawable.ic_emoji_sweat),
                getString(R.string.layout_empty_response), getString(R.string.try_again)
            ) { view ->
                binding!!.stateLayout.showLoading()
                makeRequest()
            }
    }

    private fun configureScoreDistribution(scoreDistributions: List<ScoreDistribution>) {
        binding!!.seriesScoreDist.description.isEnabled = false
        binding!!.seriesScoreDist.setDrawGridBackground(false)
        binding!!.seriesScoreDist.setDrawBarShadow(false)


        ChartUtil.StepXAxisFormatter<Int>()
            .setDataModel(
                Stream.of(scoreDistributions)
                    .map<Int>(Function<ScoreDistribution, Int> { it.getScore() })
                    .toList()
            )
            .setChartBase(binding!!.seriesScoreDist)
            .build(context!!)

        ChartUtil.StepYAxisFormatter()
            .setChartBase(binding!!.seriesScoreDist)
            .build(context!!)
    }

    private fun configureSeriesStats() {
        binding!!.seriesStats.setUsePercentValues(true)
        binding!!.seriesStats.description.isEnabled = false
        binding!!.seriesStats.setExtraOffsets(0f, 0f, 50f, 0f)
        binding!!.seriesStats.isDrawHoleEnabled = true
        binding!!.seriesStats.holeRadius = 58f
        binding!!.seriesStats.transparentCircleRadius = 61f

        binding!!.seriesStats.rotationAngle = 0f
        binding!!.seriesStats.isRotationEnabled = false
        binding!!.seriesStats.isHighlightPerTapEnabled = true

        val l = binding!!.seriesStats.legend
        l.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        l.orientation = Legend.LegendOrientation.VERTICAL
        l.setDrawInside(false)
        l.xEntrySpace = 0f
        l.yEntrySpace = 0f
        l.yOffset = 0f

        // entry label styling
        // binding.seriesStats.setEntryLabelColor(CompatUtil.getColorFromAttr(getContext(), R.attr.subtitleColor));
        binding!!.seriesStats.setDrawEntryLabels(false)
    }

    companion object {

        fun newInstance(args: Bundle): MediaStatsFragment {
            val fragment = MediaStatsFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
