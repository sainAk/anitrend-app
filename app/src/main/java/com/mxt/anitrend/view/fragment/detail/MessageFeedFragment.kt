package com.mxt.anitrend.view.fragment.detail

import android.content.Intent
import android.os.Bundle
import android.view.View

import com.annimon.stream.IntPair
import com.mxt.anitrend.R
import com.mxt.anitrend.adapter.recycler.index.FeedAdapter
import com.mxt.anitrend.model.entity.anilist.FeedList
import com.mxt.anitrend.util.CompatUtil
import com.mxt.anitrend.util.GraphUtil
import com.mxt.anitrend.util.KeyUtil
import com.mxt.anitrend.view.activity.detail.ProfileActivity
import com.mxt.anitrend.view.fragment.list.FeedListFragment
import com.mxt.anitrend.view.sheet.BottomSheetComposer

/**
 * Created by max on 2018/03/24.
 * MessageFeedFragment
 */

class MessageFeedFragment : FeedListFragment() {

    private var userId: Long = 0
    @KeyUtil.MessageType
    private var messageType: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            messageType = arguments!!.getInt(KeyUtil.getArg_message_type())
            userId = arguments!!.getLong(KeyUtil.getArg_userId())
        }
        setIsMenuDisabled(true)
        setIsFeed(false)
        (mAdapter as FeedAdapter).setMessageType(messageType)
    }

    override fun updateUI() {
        super.updateUI()
    }

    override fun makeRequest() {
        queryContainer = GraphUtil.getDefaultQuery(true)
        queryContainer!!.putVariable(KeyUtil.getArg_page(), presenter.currentPage)
            .putVariable(
                if (messageType == KeyUtil.getMESSAGE_TYPE_INBOX()) KeyUtil.getArg_userId() else KeyUtil.getArg_messengerId(),
                userId
            )
        viewModel.params.putParcelable(KeyUtil.getArg_graph_params(), queryContainer)
        viewModel.requestData(KeyUtil.getFEED_MESSAGE_REQ(), context!!)
    }

    override fun onItemClick(target: View, data: IntPair<FeedList>) {
        val intent: Intent
        when (target.id) {
            R.id.messenger_avatar -> if (data.second.messenger != null) {
                intent = Intent(activity, ProfileActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                intent.putExtra(KeyUtil.getArg_id(), data.second.messenger!!.id)
                CompatUtil.startRevealAnim(activity, target, intent)
            }
            R.id.recipient_avatar -> if (data.second.recipient != null) {
                intent = Intent(activity, ProfileActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                intent.putExtra(KeyUtil.getArg_id(), data.second.recipient!!.id)
                CompatUtil.startRevealAnim(activity, target, intent)
            }
            R.id.widget_edit -> {
                mBottomSheet = BottomSheetComposer.Builder().setUserActivity(data.second)
                    .setRequestMode(KeyUtil.getMUT_SAVE_MESSAGE_FEED())
                    .setUserModel(data.second.recipient)
                    .setTitle(R.string.edit_status_title)
                    .build()
                showBottomSheet()
            }
            else -> super.onItemClick(target, data)
        }
    }

    companion object {

        fun newInstance(params: Bundle, @KeyUtil.MessageType messageType: Int): MessageFeedFragment {
            val args = Bundle(params)
            args.putInt(KeyUtil.getArg_message_type(), messageType)
            val fragment = MessageFeedFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
