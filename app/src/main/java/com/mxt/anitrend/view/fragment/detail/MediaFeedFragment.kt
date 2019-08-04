package com.mxt.anitrend.view.fragment.detail

import android.os.Bundle

import com.mxt.anitrend.model.entity.container.request.QueryContainerBuilder
import com.mxt.anitrend.util.KeyUtil
import com.mxt.anitrend.view.fragment.list.FeedListFragment

/**
 * Created by max on 2018/03/24.
 * Media feed list fragment for media types, both anime and manga
 */

class MediaFeedFragment : FeedListFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setIsMenuDisabled(true)
        setIsFeed(false)
    }

    override fun makeRequest() {
        queryContainer!!.putVariable(KeyUtil.getArg_page(), presenter.currentPage)
        viewModel.params.putParcelable(KeyUtil.getArg_graph_params(), queryContainer)
        viewModel.requestData(KeyUtil.getMEDIA_SOCIAL_REQ(), context!!)
    }

    companion object {

        override fun newInstance(params: Bundle, queryContainer: QueryContainerBuilder): MediaFeedFragment {
            val args = Bundle(params)
            args.putParcelable(KeyUtil.getArg_graph_params(), queryContainer)
            val fragment = MediaFeedFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
