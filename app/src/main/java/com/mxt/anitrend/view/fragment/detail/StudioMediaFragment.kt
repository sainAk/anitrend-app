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
import com.mxt.anitrend.adapter.recycler.index.MediaAdapter
import com.mxt.anitrend.base.custom.fragment.FragmentBaseList
import com.mxt.anitrend.model.entity.base.MediaBase
import com.mxt.anitrend.model.entity.container.body.ConnectionContainer
import com.mxt.anitrend.model.entity.container.body.PageContainer
import com.mxt.anitrend.model.entity.container.request.QueryContainerBuilder
import com.mxt.anitrend.presenter.fragment.MediaPresenter
import com.mxt.anitrend.util.Settings
import com.mxt.anitrend.util.CompatUtil
import com.mxt.anitrend.util.DialogUtil
import com.mxt.anitrend.util.GraphUtil
import com.mxt.anitrend.util.KeyUtil
import com.mxt.anitrend.util.MediaActionUtil
import com.mxt.anitrend.util.NotifyUtil
import com.mxt.anitrend.view.activity.detail.MediaActivity

import java.util.Collections

/**
 * Created by max on 2018/03/25.
 * StudioMediaFragment
 */

class StudioMediaFragment :
    FragmentBaseList<MediaBase, ConnectionContainer<PageContainer<MediaBase>>, MediaPresenter>() {

    private var id: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null)
            id = arguments!!.getLong(KeyUtil.getArg_id())
        mColumnSize = R.integer.grid_giphy_x3
        setIsPager(true)
        setIsFilterable(true)
        mAdapter = MediaAdapter(context!!, true)
        setPresenter(MediaPresenter(context))
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
            }
        return super.onOptionsItemSelected(item)
    }

    override fun updateUI() {
        setSwipeRefreshLayoutEnabled(false)
        injectAdapter()
    }

    override fun makeRequest() {
        val pref = presenter.settings
        val queryContainer = GraphUtil.getDefaultQuery(getIsPager())
            .putVariable(KeyUtil.getArg_id(), id)
            .putVariable(KeyUtil.getArg_page(), presenter.currentPage)
            .putVariable(KeyUtil.getArg_sort(), pref.mediaSort!! + pref.sortOrder)
        viewModel.params.putParcelable(KeyUtil.getArg_graph_params(), queryContainer)
        viewModel.requestData(KeyUtil.getSTUDIO_MEDIA_REQ(), context!!)
    }

    override fun onChanged(content: ConnectionContainer<PageContainer<MediaBase>>?) {
        val pageContainer: PageContainer<MediaBase>
        if (content != null && (pageContainer = content.connection) != null) {
            if (!pageContainer.isEmpty) {
                if (pageContainer.hasPageInfo())
                    presenter.pageInfo = pageContainer.pageInfo
                if (!pageContainer.isEmpty)
                    onPostProcessed(pageContainer.pageData)
                else
                    onPostProcessed(emptyList())
            }
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

        fun newInstance(args: Bundle): StudioMediaFragment {
            val fragment = StudioMediaFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
