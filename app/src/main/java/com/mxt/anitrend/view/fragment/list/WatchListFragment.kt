package com.mxt.anitrend.view.fragment.list

import androidx.lifecycle.Lifecycle
import android.os.Bundle
import android.os.Parcelable
import android.util.Log

import com.mxt.anitrend.BuildConfig
import com.mxt.anitrend.R
import com.mxt.anitrend.adapter.recycler.index.EpisodeAdapter
import com.mxt.anitrend.base.custom.fragment.FragmentChannelBase
import com.mxt.anitrend.base.interfaces.event.RetroCallback
import com.mxt.anitrend.model.entity.anilist.ExternalLink
import com.mxt.anitrend.model.entity.container.body.ConnectionContainer
import com.mxt.anitrend.model.entity.container.request.QueryContainerBuilder
import com.mxt.anitrend.presenter.widget.WidgetPresenter
import com.mxt.anitrend.util.EpisodeUtil
import com.mxt.anitrend.util.ErrorUtil
import com.mxt.anitrend.util.GraphUtil
import com.mxt.anitrend.util.KeyUtil

import java.util.ArrayList

import retrofit2.Call
import retrofit2.Response

/**
 * Created by max on 2017/11/03.
 * WatchListFragment for anime types
 */

class WatchListFragment : FragmentChannelBase(), RetroCallback<ConnectionContainer<List<ExternalLink>>> {

    private var mediaId: Long = 0
    @KeyUtil.MediaType
    private var mediaType: String? = null

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
        mAdapter = EpisodeAdapter(context!!)
        mAdapter.clickListener = clickListener
        setPresenter(WidgetPresenter<T>(context))
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
        if (externalLinks != null) {
            val feed = targetLink != null && targetLink.startsWith(BuildConfig.FEEDS_LINK)
            val bundle = viewModel.params
            bundle.putString(KeyUtil.getArg_search(), targetLink)
            bundle.putBoolean(KeyUtil.getArg_feed(), feed)
            viewModel.requestData(getRequestMode(feed), context!!)
        } else {
            val queryContainer = GraphUtil.getDefaultQuery(false)
                .putVariable(KeyUtil.getArg_id(), mediaId)
                .putVariable(KeyUtil.getArg_type(), mediaType)
            presenter.params.putParcelable(KeyUtil.getArg_graph_params(), queryContainer)
            presenter.requestData(KeyUtil.getMEDIA_EPISODES_REQ(), context!!, this)
        }
    }

    @KeyUtil.RequestType
    private fun getRequestMode(feed: Boolean): Int {
        return if (feed) if (isPopular) KeyUtil.getEPISODE_POPULAR_REQ() else KeyUtil.getEPISODE_LATEST_REQ() else KeyUtil.getEPISODE_FEED_REQ()
    }

    override fun onResponse(
        call: Call<ConnectionContainer<List<ExternalLink>>>,
        response: Response<ConnectionContainer<List<ExternalLink>>>
    ) {
        if (isAlive && lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
            val connectionContainer: ConnectionContainer<List<ExternalLink>>?
            if (response.isSuccessful && (connectionContainer = response.body()) != null) {
                if (!connectionContainer!!.isEmpty) {
                    externalLinks = connectionContainer.connection
                    if (mAdapter.itemCount < 1 && externalLinks != null)
                        targetLink = EpisodeUtil.episodeSupport(externalLinks)
                    if (targetLink == null)
                        showEmpty(getString(R.string.waring_missing_episode_links))
                    else
                        makeRequest()
                }
            } else
                Log.e(TAG, ErrorUtil.getError(response))
        }
    }

    override fun onFailure(call: Call<ConnectionContainer<List<ExternalLink>>>, throwable: Throwable) {
        if (isAlive && lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
            throwable.printStackTrace()
            Log.e(TAG, throwable.message)
        }
    }

    companion object {

        fun newInstance(params: Bundle, popular: Boolean): FragmentChannelBase {
            val args = Bundle(params)
            val fragment = WatchListFragment()
            args.putBoolean(KeyUtil.getArg_popular(), popular)
            fragment.arguments = args
            return fragment
        }

        fun newInstance(externalLinks: List<ExternalLink>, popular: Boolean): FragmentChannelBase {
            val fragment = WatchListFragment()
            val args = Bundle()
            args.putParcelableArrayList(KeyUtil.getArg_list_model(), externalLinks as ArrayList<out Parcelable>)
            args.putBoolean(KeyUtil.getArg_popular(), popular)
            fragment.arguments = args
            return fragment
        }
    }
}
