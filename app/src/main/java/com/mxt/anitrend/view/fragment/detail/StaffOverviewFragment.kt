package com.mxt.anitrend.view.fragment.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.mxt.anitrend.R
import com.mxt.anitrend.base.custom.fragment.FragmentBase
import com.mxt.anitrend.databinding.FragmentStaffOverviewBinding
import com.mxt.anitrend.model.entity.base.StaffBase
import com.mxt.anitrend.model.entity.container.request.QueryContainerBuilder
import com.mxt.anitrend.presenter.base.BasePresenter
import com.mxt.anitrend.util.CompatUtil
import com.mxt.anitrend.util.GraphUtil
import com.mxt.anitrend.util.KeyUtil

import butterknife.ButterKnife
import butterknife.OnClick

/**
 * Created by max on 2018/01/30.
 * StaffOverviewFragment
 */

class StaffOverviewFragment : FragmentBase<StaffBase, BasePresenter, StaffBase>() {

    private var model: StaffBase? = null

    private var binding: FragmentStaffOverviewBinding? = null

    private var id: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null)
            id = arguments!!.getLong(KeyUtil.getArg_id())
        setViewModel(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentStaffOverviewBinding.inflate(inflater, container, false)
        unbinder = ButterKnife.bind(this, binding!!.root)
        binding!!.stateLayout.showLoading()
        return binding!!.root
    }

    override fun updateUI() {
        if (model != null) {
            binding!!.model = model
            binding!!.stateLayout.showContent()
        } else
            binding!!.stateLayout.showError(
                CompatUtil.getDrawable(context, R.drawable.ic_emoji_sweat),
                getString(R.string.layout_empty_response), getString(R.string.try_again)
            ) { view -> makeRequest() }
    }

    override fun onStart() {
        super.onStart()
        if (model != null)
            updateUI()
        else
            makeRequest()
    }

    override fun makeRequest() {
        val queryContainer = GraphUtil.getDefaultQuery(false)
            .putVariable(KeyUtil.getArg_id(), id)
        viewModel.params.putParcelable(KeyUtil.getArg_graph_params(), queryContainer)
        viewModel.requestData(KeyUtil.getSTAFF_OVERVIEW_REQ(), context!!)

    }

    /**
     * Called when a view has been clicked.
     *
     * @param view The view that was clicked.
     */
    @OnClick(R.id.staff_img)
    override fun onClick(view: View) {
        when (view.id) {
            R.id.staff_img -> CompatUtil.imagePreview(
                activity,
                view,
                model!!.image.large,
                R.string.image_preview_error_staff_image
            )
            else -> super.onClick(view)
        }
    }

    override fun onChanged(model: StaffBase?) {
        if (model != null)
            this.model = model
        updateUI()
    }

    companion object {

        fun newInstance(args: Bundle): StaffOverviewFragment {
            val fragment = StaffOverviewFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
