package com.mxt.anitrend.view.fragment.detail

import android.os.Bundle

import com.mxt.anitrend.model.entity.container.request.QueryContainerBuilder
import com.mxt.anitrend.util.KeyUtil
import com.mxt.anitrend.view.fragment.list.FeedListFragment

/**
 * Created by max on 2017/11/26.
 * user profile targeted feeds
 */

class UserFeedFragment : FeedListFragment() {

    private var userId: Long = 0
    private var userName: String? = null

    /**
     * Override and set presenter, mColumnSize, and fetch argument/s
     *
     * @param savedInstanceState
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null)
            if (arguments!!.containsKey(KeyUtil.getArg_id()))
                userId = arguments!!.getLong(KeyUtil.getArg_id())
            else
                userName = arguments!!.getString(KeyUtil.getArg_userName())
        setIsMenuDisabled(true)
        setIsFeed(false)
    }

    override fun makeRequest() {
        if (presenter.settings.isAuthenticated && presenter.isCurrentUser(userId, userName))
            userId = presenter.database.currentUser!!.id

        if (userId > 0)
            queryContainer!!.putVariable(KeyUtil.getArg_userId(), userId)
        else
            queryContainer!!.putVariable(KeyUtil.getArg_userName(), userName)

        if (queryContainer!!.containsVariable(KeyUtil.getArg_userId()) || queryContainer!!.containsVariable(KeyUtil.getArg_userName()))
            super.makeRequest()
    }

    companion object {

        override fun newInstance(params: Bundle, queryContainer: QueryContainerBuilder): UserFeedFragment {
            val args = Bundle(params)
            args.putParcelable(KeyUtil.getArg_graph_params(), queryContainer)
            val fragment = UserFeedFragment()
            fragment.arguments = args
            return fragment
        }
    }

}