package com.mxt.anitrend.view.fragment.list

import android.os.Bundle

import com.mxt.anitrend.model.entity.container.request.QueryContainerBuilder
import com.mxt.anitrend.util.KeyUtil

class MediaLatestList : MediaBrowseFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setIsFilterable(false)
    }

    override fun makeRequest() {
        val bundle = viewModel.params
        queryContainer!!.putVariable(KeyUtil.getArg_page(), presenter.currentPage)
        bundle.putParcelable(KeyUtil.getArg_graph_params(), queryContainer)
        viewModel.requestData(KeyUtil.getMEDIA_BROWSE_REQ(), context!!)
    }

    companion object {

        override fun newInstance(params: Bundle, queryContainer: QueryContainerBuilder): MediaLatestList {
            val args = Bundle(params)
            args.putParcelable(KeyUtil.getArg_graph_params(), queryContainer)
            val fragment = MediaLatestList()
            fragment.arguments = args
            return fragment
        }
    }
}
