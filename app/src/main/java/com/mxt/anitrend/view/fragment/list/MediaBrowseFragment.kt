package com.mxt.anitrend.view.fragment.list

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast

import com.afollestad.materialdialogs.DialogAction
import com.annimon.stream.IntPair
import com.annimon.stream.Stream
import com.mxt.anitrend.R
import com.mxt.anitrend.adapter.recycler.index.MediaAdapter
import com.mxt.anitrend.base.custom.fragment.FragmentBaseList
import com.mxt.anitrend.model.entity.anilist.Genre
import com.mxt.anitrend.model.entity.anilist.MediaTag
import com.mxt.anitrend.model.entity.base.MediaBase
import com.mxt.anitrend.model.entity.container.body.PageContainer
import com.mxt.anitrend.model.entity.container.request.QueryContainerBuilder
import com.mxt.anitrend.presenter.fragment.MediaPresenter
import com.mxt.anitrend.util.Settings
import com.mxt.anitrend.util.CompatUtil
import com.mxt.anitrend.util.DateUtil
import com.mxt.anitrend.util.DialogUtil
import com.mxt.anitrend.util.GenreTagUtil
import com.mxt.anitrend.util.KeyUtil
import com.mxt.anitrend.util.MediaActionUtil
import com.mxt.anitrend.util.MediaBrowseUtil
import com.mxt.anitrend.util.NotifyUtil
import com.mxt.anitrend.view.activity.detail.MediaActivity

import java.util.Collections
import java.util.Locale
import java.util.WeakHashMap

/**
 * Created by max on 2018/02/03.
 * Multi purpose media browse fragment
 */

open class MediaBrowseFragment : FragmentBaseList<MediaBase, PageContainer<MediaBase>, MediaPresenter>() {

    protected var queryContainer: QueryContainerBuilder? = null
    private var mediaBrowseUtil: MediaBrowseUtil? = null

    /**
     * Override and set presenter, mColumnSize, and fetch argument/s
     *
     * @param savedInstanceState
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            queryContainer = arguments!!.getParcelable(KeyUtil.getArg_graph_params())
            mediaBrowseUtil = arguments!!.getParcelable(KeyUtil.getArg_media_util())
        }
        if (mediaBrowseUtil == null)
            mediaBrowseUtil = MediaBrowseUtil(true)

        setIsPager(true)
        setIsFilterable(mediaBrowseUtil!!.isFilterEnabled)
        mColumnSize = if (mediaBrowseUtil!!.isCompactType) R.integer.grid_giphy_x3 else R.integer.grid_list_x2
        mAdapter = MediaAdapter(context!!, mediaBrowseUtil!!.isCompactType)
        setPresenter(MediaPresenter(context))
        setViewModel(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        if (mediaBrowseUtil!!.isBasicFilter) {
            menu.findItem(R.id.action_type).isVisible = false
            menu.findItem(R.id.action_year).isVisible = false
            menu.findItem(R.id.action_status).isVisible = false
            menu.findItem(R.id.action_genre).isVisible = false
            menu.findItem(R.id.action_tag).isVisible = false
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (context != null)
            when (item.itemId) {
                R.id.action_sort -> {
                    DialogUtil.Companion.createSelection(context, R.string.app_filter_sort, CompatUtil.getIndexOf(
                        KeyUtil.MediaSortType,
                        presenter.settings.mediaSort
                    ), CompatUtil.capitalizeWords(KeyUtil.MediaSortType),
                        { dialog, which ->
                            if (which === DialogAction.POSITIVE)
                                presenter.settings.mediaSort = KeyUtil.MediaSortType[dialog.getSelectedIndex()]
                        })
                    return true
                }
                R.id.action_order -> {
                    DialogUtil.Companion.createSelection(context, R.string.app_filter_order, CompatUtil.getIndexOf(
                        KeyUtil.SortOrderType,
                        presenter.settings.sortOrder
                    ), CompatUtil.getStringList(context, R.array.order_by_types),
                        { dialog, which ->
                            if (which === DialogAction.POSITIVE)
                                presenter.settings.sortOrder = KeyUtil.SortOrderType[dialog.getSelectedIndex()]
                        })
                    return true
                }
                R.id.action_genre -> {
                    val genres = presenter.database.genreCollection
                    if (CompatUtil.isEmpty(genres)) {
                        NotifyUtil.INSTANCE.makeText(
                            context,
                            R.string.app_splash_loading,
                            R.drawable.ic_warning_white_18dp,
                            Toast.LENGTH_SHORT
                        ).show()
                        presenter.checkGenresAndTags(activity!!)
                    } else {
                        val genresIndexMap = presenter
                            .settings.selectedGenres

                        val selectedGenres = Stream.of(genresIndexMap!!)
                            .map<Int>(Function<Entry<Int, String>, Int> { it.key })
                            .toArray<Int>(Integer[]::new  /* Currently unsupported in Kotlin */)

                        DialogUtil.Companion.createCheckList(context,
                            R.string.app_filter_genres,
                            genres,
                            selectedGenres,
                            { dialog, which, text -> false },
                            { dialog, which ->
                                when (which) {
                                    POSITIVE -> {
                                        val selectedIndices = GenreTagUtil
                                            .createGenreSelectionMap(genres, dialog.getSelectedIndices())

                                        presenter.settings
                                            .selectedGenres = selectedIndices
                                    }
                                    NEGATIVE -> presenter.settings
                                        .selectedGenres = WeakHashMap<Int, String>()
                                }
                            })
                    }
                    return true
                }
                R.id.action_tag -> {
                    val tagList = presenter.database.mediaTags
                    if (CompatUtil.isEmpty(tagList)) {
                        NotifyUtil.INSTANCE.makeText(
                            context,
                            R.string.app_splash_loading,
                            R.drawable.ic_warning_white_18dp,
                            Toast.LENGTH_SHORT
                        ).show()
                        presenter.checkGenresAndTags(activity!!)
                    } else {
                        val tagsIndexMap = presenter
                            .settings.selectedTags

                        val selectedTags = Stream.of(tagsIndexMap!!)
                            .map<Int>(Function<Entry<Int, String>, Int> { it.key })
                            .toArray<Int>(Integer[]::new  /* Currently unsupported in Kotlin */)

                        DialogUtil.Companion.createCheckList(context, R.string.app_filter_tags, tagList, selectedTags,
                            { dialog, which, text -> false }, { dialog, which ->
                                when (which) {
                                    POSITIVE -> {
                                        val selectedIndices = GenreTagUtil
                                            .createTagSelectionMap(tagList, dialog.getSelectedIndices())

                                        presenter.settings
                                            .selectedTags = selectedIndices
                                    }
                                    NEGATIVE -> presenter.settings
                                        .selectedTags = WeakHashMap<Int, String>()
                                }
                            })
                    }
                    return true
                }
                R.id.action_type -> {
                    if (CompatUtil.equals(
                            queryContainer!!.getVariable(KeyUtil.getArg_mediaType()),
                            KeyUtil.getANIME()
                        )
                    ) {
                        DialogUtil.Companion.createSelection(context,
                            R.string.app_filter_show_type,
                            CompatUtil.getIndexOf(
                                KeyUtil.AnimeFormat,
                                presenter.settings.animeFormat
                            ),
                            CompatUtil.getStringList(context, R.array.anime_formats),
                            { dialog, which ->
                                if (which === DialogAction.POSITIVE)
                                    presenter.settings.animeFormat = KeyUtil.AnimeFormat[dialog.getSelectedIndex()]
                            })
                    } else {
                        DialogUtil.Companion.createSelection(context,
                            R.string.app_filter_show_type,
                            CompatUtil.getIndexOf(
                                KeyUtil.MangaFormat,
                                presenter.settings.mangaFormat
                            ),
                            CompatUtil.getStringList(context, R.array.manga_formats),
                            { dialog, which ->
                                if (which === DialogAction.POSITIVE)
                                    presenter.settings.mangaFormat = KeyUtil.MangaFormat[dialog.getSelectedIndex()]
                            })
                    }
                    return true
                }
                R.id.action_year -> {
                    val yearRanges = DateUtil.getYearRanges(1950, 1)
                    DialogUtil.Companion.createSelection(context,
                        R.string.app_filter_year,
                        CompatUtil.getIndexOf(yearRanges, presenter.settings.seasonYear),
                        yearRanges,
                        { dialog, which ->
                            if (which === DialogAction.POSITIVE)
                                presenter.settings.seasonYear = yearRanges[dialog.getSelectedIndex()]
                        })
                    return true
                }
                R.id.action_status -> {
                    DialogUtil.Companion.createSelection(context, R.string.anime, CompatUtil.getIndexOf(
                        KeyUtil.MediaStatus,
                        presenter.settings.mediaStatus
                    ), CompatUtil.getStringList(context, R.array.media_status),
                        { dialog, which ->
                            if (which === DialogAction.POSITIVE)
                                presenter.settings.mediaStatus = KeyUtil.MediaStatus[dialog.getSelectedIndex()]
                        })
                    return true
                }
            }
        return super.onOptionsItemSelected(item)
    }

    override fun updateUI() {
        injectAdapter()
    }

    override fun makeRequest() {
        val bundle = viewModel.params
        val pref = presenter.settings
        queryContainer!!.putVariable(KeyUtil.getArg_page(), presenter.currentPage)

        if (getIsFilterable()) {
            if (!mediaBrowseUtil!!.isBasicFilter) {
                if (CompatUtil.equals(queryContainer!!.getVariable(KeyUtil.getArg_mediaType()), KeyUtil.getMANGA())) {
                    queryContainer!!.putVariable(
                        KeyUtil.getArg_startDateLike(), String.format(
                            Locale.getDefault(),
                            "%d%%", presenter.settings.seasonYear
                        )
                    )
                        .putVariable(KeyUtil.getArg_format(), pref.mangaFormat)
                } else {
                    queryContainer!!.putVariable(KeyUtil.getArg_seasonYear(), presenter.settings.seasonYear)
                        .putVariable(KeyUtil.getArg_format(), pref.animeFormat)
                }

                queryContainer!!.putVariable(KeyUtil.getArg_status(), pref.mediaStatus)
                    .putVariable(KeyUtil.getArg_genres(), GenreTagUtil.getMappedValues(pref.selectedGenres))
                    .putVariable(KeyUtil.getArg_tags(), GenreTagUtil.getMappedValues(pref.selectedTags))
            }
            queryContainer!!.putVariable(KeyUtil.getArg_sort(), pref.mediaSort!! + pref.sortOrder)
        }
        bundle.putParcelable(KeyUtil.getArg_graph_params(), queryContainer)
        viewModel.requestData(KeyUtil.getMEDIA_BROWSE_REQ(), context!!)
    }

    override fun onChanged(content: PageContainer<MediaBase>?) {
        if (content != null) {
            if (content.hasPageInfo())
                presenter.pageInfo = content.pageInfo
            if (!content.isEmpty)
                onPostProcessed(content.pageData)
            else
                onPostProcessed(emptyList())
        } else
            onPostProcessed(emptyList())
        if (mAdapter!!.itemCount < 1)
            onPostProcessed(null)
    }

    /**
     * When the target view from [View.OnClickListener]
     * is clicked from a view holder this method will be called
     *
     * @param target view that has been clicked
     * @param data   the model that at the click index
     */
    override fun onItemClick(target: View, data: IntPair<MediaBase>) {
        when (target.id) {
            R.id.container -> {
                val intent = Intent(activity, MediaActivity::class.java)
                intent.putExtra(KeyUtil.getArg_id(), data.second.id)
                intent.putExtra(KeyUtil.getArg_mediaType(), data.second.type)
                CompatUtil.startRevealAnim(activity, target, intent)
            }
        }
    }

    /**
     * When the target view from [View.OnLongClickListener]
     * is clicked from a view holder this method will be called
     *
     * @param target view that has been long clicked
     * @param data   the model that at the long click index
     */
    override fun onItemLongClick(target: View, data: IntPair<MediaBase>) {
        when (target.id) {
            R.id.container -> if (presenter.settings.isAuthenticated) {
                mediaActionUtil = MediaActionUtil.Builder()
                    .setId(data.second.id).build(activity!!)
                mediaActionUtil!!.startSeriesAction()
            } else
                NotifyUtil.INSTANCE.makeText(
                    context,
                    R.string.info_login_req,
                    R.drawable.ic_group_add_grey_600_18dp,
                    Toast.LENGTH_SHORT
                ).show()
        }
    }

    companion object {

        fun newInstance(params: Bundle, queryContainer: QueryContainerBuilder): MediaBrowseFragment {
            val args = Bundle(params)
            args.putParcelable(KeyUtil.getArg_graph_params(), queryContainer)
            val fragment = MediaBrowseFragment()
            fragment.arguments = args
            return fragment
        }

        fun newInstance(params: Bundle): MediaBrowseFragment {
            val args = Bundle(params)
            val fragment = MediaBrowseFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
