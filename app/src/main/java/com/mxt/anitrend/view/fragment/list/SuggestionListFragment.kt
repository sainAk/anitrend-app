package com.mxt.anitrend.view.fragment.list

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem

import com.afollestad.materialdialogs.DialogAction
import com.mxt.anitrend.R
import com.mxt.anitrend.util.Settings
import com.mxt.anitrend.util.CompatUtil
import com.mxt.anitrend.util.DialogUtil
import com.mxt.anitrend.util.GraphUtil
import com.mxt.anitrend.util.KeyUtil

/**
 * Created by max on 2017/11/04.
 * Suggestions adapter
 */
class SuggestionListFragment : MediaBrowseFragment() {

    override fun makeRequest() {
        val pref = presenter.settings
        val bundle = viewModel.params
        queryContainer!!.putVariable(KeyUtil.getArg_tagsInclude(), presenter.getTopFavouriteTags(6))
            .putVariable(KeyUtil.getArg_genresInclude(), presenter.getTopFavouriteGenres(4))
            .putVariable(KeyUtil.getArg_sort(), pref.mediaSort!! + pref.sortOrder)
            .putVariable(KeyUtil.getArg_page(), presenter.currentPage)
        bundle.putParcelable(KeyUtil.getArg_graph_params(), queryContainer)
        viewModel.requestData(KeyUtil.getMEDIA_BROWSE_REQ(), context!!)
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

    companion object {

        override fun newInstance(params: Bundle): SuggestionListFragment {
            val args = Bundle(params)
            args.putParcelable(
                KeyUtil.getArg_graph_params(), GraphUtil.getDefaultQuery(true)
                    .putVariable(KeyUtil.getArg_mediaType(), KeyUtil.getANIME())
                    .putVariable(KeyUtil.getArg_onList(), false)
            )
            val fragment = SuggestionListFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
