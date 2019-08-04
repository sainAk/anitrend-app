package com.mxt.anitrend.view.fragment.detail

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast

import com.annimon.stream.IntPair
import com.mxt.anitrend.R
import com.mxt.anitrend.adapter.recycler.index.ReviewAdapter
import com.mxt.anitrend.base.custom.fragment.FragmentBaseList
import com.mxt.anitrend.model.entity.anilist.Review
import com.mxt.anitrend.model.entity.base.MediaBase
import com.mxt.anitrend.model.entity.container.body.PageContainer
import com.mxt.anitrend.model.entity.container.request.QueryContainerBuilder
import com.mxt.anitrend.presenter.base.BasePresenter
import com.mxt.anitrend.util.CompatUtil
import com.mxt.anitrend.util.GraphUtil
import com.mxt.anitrend.util.KeyUtil
import com.mxt.anitrend.util.MediaActionUtil
import com.mxt.anitrend.util.NotifyUtil
import com.mxt.anitrend.view.activity.detail.MediaActivity
import com.mxt.anitrend.view.activity.detail.ProfileActivity
import com.mxt.anitrend.view.sheet.BottomReviewReader

import java.util.Collections

/**
 * Created by max on 2017/12/28.
 * Reviews for a given series
 */

class ReviewFragment : FragmentBaseList<Review, PageContainer<Review>, BasePresenter>() {

    @KeyUtil.MediaType
    private var mediaType: String? = null
    private var mediaId: Long = 0

    /**
     * Override and set presenter, mColumnSize, and fetch argument/s
     *
     * @param savedInstanceState
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mediaId = arguments!!.getLong(KeyUtil.getArg_id())
            mediaType = arguments!!.getString(KeyUtil.getArg_mediaType())
        }
        mAdapter = ReviewAdapter(context!!, true)
        mColumnSize = R.integer.single_list_x1
        setIsPager(true)
        setPresenter(BasePresenter(context))
        setViewModel(true)
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
        if (mediaId == 0L)
            return
        val queryContainer = GraphUtil.getDefaultQuery(getIsPager())
            .putVariable(KeyUtil.getArg_mediaId(), mediaId)
            .putVariable(KeyUtil.getArg_mediaType(), mediaType)
            .putVariable(KeyUtil.getArg_page(), presenter.currentPage)
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
        val intent: Intent
        when (target.id) {
            R.id.series_image -> {
                val mediaBase = data.second.media
                intent = Intent(activity, MediaActivity::class.java)
                intent.putExtra(KeyUtil.getArg_id(), mediaBase.id)
                intent.putExtra(KeyUtil.getArg_mediaType(), mediaBase.type)
                CompatUtil.startRevealAnim(activity, target, intent)
            }
            R.id.user_avatar -> if (presenter.settings.isAuthenticated) {
                intent = Intent(activity, ProfileActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                intent.putExtra(KeyUtil.getArg_id(), data.second.user.id)
                CompatUtil.startRevealAnim(activity, target, intent)
            } else
                NotifyUtil.INSTANCE.makeText(
                    activity,
                    R.string.info_login_req,
                    R.drawable.ic_warning_white_18dp,
                    Toast.LENGTH_SHORT
                ).show()
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

        fun newInstance(args: Bundle): ReviewFragment {
            val reviewFragment = ReviewFragment()
            reviewFragment.arguments = args
            return reviewFragment
        }
    }
}