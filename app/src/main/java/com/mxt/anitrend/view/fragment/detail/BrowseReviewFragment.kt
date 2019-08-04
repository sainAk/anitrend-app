package com.mxt.anitrend.view.fragment.detail

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast

import com.afollestad.materialdialogs.DialogAction
import com.annimon.stream.IntPair
import com.mxt.anitrend.R
import com.mxt.anitrend.adapter.recycler.index.ReviewAdapter
import com.mxt.anitrend.base.custom.fragment.FragmentBaseList
import com.mxt.anitrend.model.entity.anilist.Review
import com.mxt.anitrend.model.entity.base.MediaBase
import com.mxt.anitrend.model.entity.container.body.PageContainer
import com.mxt.anitrend.model.entity.container.request.QueryContainerBuilder
import com.mxt.anitrend.presenter.base.BasePresenter
import com.mxt.anitrend.util.Settings
import com.mxt.anitrend.util.CompatUtil
import com.mxt.anitrend.util.DialogUtil
import com.mxt.anitrend.util.GraphUtil
import com.mxt.anitrend.util.KeyUtil
import com.mxt.anitrend.util.MediaActionUtil
import com.mxt.anitrend.util.NotifyUtil
import com.mxt.anitrend.view.activity.detail.MediaActivity
import com.mxt.anitrend.view.sheet.BottomReviewReader

import java.util.Collections

/**
 * Created by max on 2017/10/30.
 * Media review browse
 */

class BrowseReviewFragment : FragmentBaseList<Review, PageContainer<Review>, BasePresenter>() {

    @KeyUtil.MediaType
    private var mediaType: String? = null


    /**
     * Override and set presenter, mColumnSize, and fetch argument/s
     *
     * @param savedInstanceState
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null)
            mediaType = arguments!!.getString(KeyUtil.getArg_mediaType())
        setIsPager(true)
        mColumnSize = R.integer.single_list_x1
        setIsFilterable(true)
        mAdapter = ReviewAdapter(context!!)
        setPresenter(BasePresenter(context))
        setViewModel(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.findItem(R.id.action_genre).isVisible = false
        menu.findItem(R.id.action_tag).isVisible = false
        menu.findItem(R.id.action_type).isVisible = false
        menu.findItem(R.id.action_year).isVisible = false
        menu.findItem(R.id.action_status).isVisible = false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (context != null)
            when (item.itemId) {
                R.id.action_sort -> {
                    DialogUtil.Companion.createSelection(context, R.string.app_filter_sort, CompatUtil.getIndexOf(
                        KeyUtil.ReviewSortType,
                        presenter.settings.reviewSort
                    ), CompatUtil.capitalizeWords(KeyUtil.ReviewSortType),
                        { dialog, which ->
                            if (which === DialogAction.POSITIVE)
                                presenter.settings.reviewSort = KeyUtil.ReviewSortType[dialog.getSelectedIndex()]
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
            }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Is automatically called in the @onStart Method if overridden in list implementation
     */
    override fun updateUI() {
        injectAdapter()
    }

    /**
     * All new or updated network requests should be handled in this method
     */
    override fun makeRequest() {
        val pref = presenter.settings
        val queryContainer = GraphUtil.getDefaultQuery(true)
            .putVariable(KeyUtil.getArg_mediaType(), mediaType)
            .putVariable(KeyUtil.getArg_page(), presenter.currentPage)
            .putVariable(KeyUtil.getArg_sort(), pref.reviewSort!! + pref.sortOrder)

        viewModel.params.putParcelable(KeyUtil.getArg_graph_params(), queryContainer)
        viewModel.requestData(KeyUtil.getMEDIA_REVIEWS_REQ(), context!!)
    }

    override fun onChanged(content: PageContainer<Review>?) {
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
    override fun onItemClick(target: View, data: IntPair<Review>) {
        when (target.id) {
            R.id.series_image -> {
                val mediaBase = data.second.media
                val intent = Intent(activity, MediaActivity::class.java)
                intent.putExtra(KeyUtil.getArg_id(), mediaBase.id)
                intent.putExtra(KeyUtil.getArg_mediaType(), mediaBase.type)
                CompatUtil.startRevealAnim(activity, target, intent)
            }
            R.id.review_read_more -> {
                mBottomSheet = BottomReviewReader.Builder()
                    .setReview(data.second)
                    .setTitle(R.string.drawer_title_reviews)
                    .build()
                showBottomSheet()
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
    override fun onItemLongClick(target: View, data: IntPair<Review>) {
        when (target.id) {
            R.id.series_image -> if (presenter.settings.isAuthenticated) {
                mediaActionUtil = MediaActionUtil.Builder()
                    .setId(data.second.media.id).build(activity!!)
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

        fun newInstance(@KeyUtil.MediaType mediaType: String): BrowseReviewFragment {
            val args = Bundle()
            args.putString(KeyUtil.getArg_mediaType(), mediaType)
            val fragment = BrowseReviewFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
