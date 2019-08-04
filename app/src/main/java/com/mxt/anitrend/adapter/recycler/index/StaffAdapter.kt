package com.mxt.anitrend.adapter.recycler.index

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.Filter

import com.bumptech.glide.Glide
import com.mxt.anitrend.R
import com.mxt.anitrend.base.custom.recycler.RecyclerViewAdapter
import com.mxt.anitrend.base.custom.recycler.RecyclerViewHolder
import com.mxt.anitrend.databinding.AdapterStaffBinding
import com.mxt.anitrend.model.entity.base.StaffBase
import com.mxt.anitrend.util.CompatUtil

import butterknife.OnClick

/**
 * Created by max on 2017/12/20.
 * StaffAdapter
 */

class StaffAdapter(context: Context) : RecyclerViewAdapter<StaffBase>(context) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder<StaffBase> {
        return StaffViewHolder(AdapterStaffBinding.inflate(CompatUtil.getLayoutInflater(parent.context), parent, false))
    }

    override fun getFilter(): Filter? {
        return null
    }

    protected inner class StaffViewHolder
    /**
     * Default constructor which includes binding with butter knife
     *
     * @param binding
     */
        (private val binding: AdapterStaffBinding) : RecyclerViewHolder<StaffBase>(binding.root) {

        /**
         * Load image, text, buttons, etc. in this method from the given parameter
         * <br></br>
         *
         * @param model Is the model at the current adapter position
         */
        override fun onBindViewHolder(model: StaffBase?) {
            binding.model = model
            if (model!!.isFavourite)
                binding.favouriteIndicator.visibility = View.VISIBLE
            else
                binding.favouriteIndicator.visibility = View.GONE
            binding.executePendingBindings()
        }

        /**
         * If any image views are used within the view holder, clear any pending async img requests
         * by using Glide.clear(ImageView) or Glide.with(context).clear(view) if using Glide v4.0
         * <br></br>
         *
         * @see Glide
         */
        override fun onViewRecycled() {
            Glide.with(getContext()).clear(binding.staffImg)
            binding.unbind()
        }

        @OnClick(R.id.container)
        override fun onClick(v: View) {
            performClick(clickListener, data, v)
        }

        override fun onLongClick(v: View): Boolean {
            return performLongClick(clickListener, data, v)
        }
    }
}