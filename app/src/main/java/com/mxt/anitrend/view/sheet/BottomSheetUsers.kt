package com.mxt.anitrend.view.sheet

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import com.google.android.material.bottomsheet.BottomSheetBehavior
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import android.text.TextUtils
import android.view.View

import com.annimon.stream.IntPair
import com.miguelcatalan.materialsearchview.MaterialSearchView
import com.mxt.anitrend.R
import com.mxt.anitrend.adapter.recycler.index.UserAdapter
import com.mxt.anitrend.base.custom.sheet.BottomSheetBase
import com.mxt.anitrend.base.custom.sheet.BottomSheetList
import com.mxt.anitrend.databinding.BottomSheetListBinding
import com.mxt.anitrend.model.entity.base.UserBase
import com.mxt.anitrend.presenter.base.BasePresenter
import com.mxt.anitrend.util.CompatUtil
import com.mxt.anitrend.util.KeyUtil
import com.mxt.anitrend.view.activity.detail.ProfileActivity

import java.util.ArrayList

import butterknife.ButterKnife

class BottomSheetUsers : BottomSheetList<UserBase>(), MaterialSearchView.OnQueryTextListener,
    MaterialSearchView.SearchViewListener {

    private var binding: BottomSheetListBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter = BasePresenter(context)
        mColumnSize = resources.getInteger(R.integer.single_list_x1)
        mAdapter = UserAdapter(activity!!)
        if (arguments != null) {
            val baseList = arguments!!.getParcelableArrayList(KeyUtil.getArg_list_model())
            if (!CompatUtil.isEmpty(baseList))
                mAdapter.onItemsInserted(baseList)
        }
    }

    /**
     * Setup your view un-binder here as well as inflating other views as needed
     * into your view stub
     *
     * @param savedInstanceState
     */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        binding = BottomSheetListBinding.inflate(CompatUtil.getLayoutInflater(activity))
        dialog.setContentView(binding!!.root)
        unbinder = ButterKnife.bind(this, dialog)
        createBottomSheetBehavior(binding!!.root)
        mLayoutManager = StaggeredGridLayoutManager(mColumnSize, StaggeredGridLayoutManager.VERTICAL)
        return dialog
    }

    /**
     * Is automatically called in the @onStart Method if overridden in list implementation
     */
    override fun updateUI() {
        toolbarTitle.text = getString(mTitle, mAdapter.itemCount)
        toolbarSearch.visibility = View.VISIBLE
        searchView.setOnQueryTextListener(this)
        searchView.setOnSearchViewListener(this)
        injectAdapter()
    }

    /**
     * All new or updated network requests should be handled in this method
     */
    override fun makeRequest() {

    }

    /**
     * Called when the user submits the query. This could be due to a key press on the
     * keyboard or due to pressing a submit button.
     * The listener can override the standard behavior by returning true
     * to indicate that it has handled the submit request. Otherwise return false to
     * let the SearchView handle the submission by launching any associated intent.
     *
     * @param query the query text that is to be submitted
     * @return true if the query has been handled by the listener, false to let the
     * SearchView perform the default action.
     */
    override fun onQueryTextSubmit(query: String): Boolean {
        return false
    }

    /**
     * Called when the query text is changed by the user.
     *
     * @param newText the new content of the query text field.
     * @return false if the SearchView should perform the default action of showing any
     * suggestions if available, true if the action was handled by the listener.
     */
    override fun onQueryTextChange(newText: String): Boolean {
        if (!TextUtils.isEmpty(newText) && mAdapter != null && mAdapter.filter != null) {
            mAdapter.filter.filter(newText)
            return true
        }
        return false
    }

    override fun onSearchViewShown() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    override fun onSearchViewClosed() {
        if (mAdapter != null && mAdapter.filter != null)
            mAdapter.filter.filter("")
    }

    /**
     * When the target view from [View.OnClickListener]
     * is clicked from a view holder this method will be called
     *
     * @param target view that has been clicked
     * @param data   the model that at the clicked index
     */
    override fun onItemClick(target: View, data: IntPair<UserBase>) {
        when (target.id) {
            R.id.container -> {
                val intent = Intent(activity, ProfileActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                intent.putExtra(KeyUtil.getArg_id(), data.second.id)
                if (activity != null)
                    activity!!.startActivity(intent)
            }
        }
    }

    /**
     * When the target view from [View.OnLongClickListener]
     * is clicked from a view holder this method will be called
     *
     * @param target view that has been long clicked
     * @param data   the model that at the long clicked index
     */
    override fun onItemLongClick(target: View, data: IntPair<UserBase>) {

    }

    /**
     * Builder class for bottom sheet
     */
    class Builder : BottomSheetBase.BottomSheetBuilder() {

        override fun build(): BottomSheetBase<*> {
            return newInstance(bundle)
        }

        fun setModel(model: List<UserBase>): BottomSheetBase.BottomSheetBuilder {
            bundle.putParcelableArrayList(KeyUtil.getArg_list_model(), model as ArrayList<out Parcelable>)
            return this
        }
    }

    companion object {

        fun newInstance(bundle: Bundle): BottomSheetUsers {
            val fragment = BottomSheetUsers()
            fragment.arguments = bundle
            return fragment
        }
    }
}
