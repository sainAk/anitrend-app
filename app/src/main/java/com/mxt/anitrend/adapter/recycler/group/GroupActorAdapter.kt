package com.mxt.anitrend.adapter.recycler.group

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.Filter

import com.bumptech.glide.Glide
import com.mxt.anitrend.R
import com.mxt.anitrend.adapter.recycler.shared.GroupMediaViewHolder
import com.mxt.anitrend.base.custom.recycler.RecyclerViewAdapter
import com.mxt.anitrend.base.custom.recycler.RecyclerViewHolder
import com.mxt.anitrend.base.interfaces.event.ItemClickListener
import com.mxt.anitrend.databinding.AdapterMediaHeaderBinding
import com.mxt.anitrend.databinding.AdapterStaffBinding
import com.mxt.anitrend.model.entity.base.StaffBase
import com.mxt.anitrend.model.entity.group.RecyclerItem
import com.mxt.anitrend.util.CompatUtil
import com.mxt.anitrend.util.KeyUtil

import butterknife.OnClick

/**
 * Created by max on 2018/03/23.
 * Character Actor Adapter
 */

class GroupActorAdapter(context: Context) : RecyclerViewAdapter<RecyclerItem>(context) {

    private var mediaClickListener: ItemClickListener<RecyclerItem>? = null

    fun setMediaClickListener(mediaClickListener: ItemClickListener<RecyclerItem>) {
        this.mediaClickListener = mediaClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, @KeyUtil.RecyclerViewType viewType: Int): RecyclerViewHolder<RecyclerItem> {
        return if (viewType == KeyUtil.getRECYCLER_TYPE_HEADER()) GroupMediaViewHolder(
            AdapterMediaHeaderBinding.inflate(
                CompatUtil.getLayoutInflater(parent.context),
                parent,
                false
            ), mediaClickListener
        ) else StaffViewHolder(
            AdapterStaffBinding.inflate(
                CompatUtil.getLayoutInflater(parent.context),
                parent,
                false
            )
        )
    }

    @KeyUtil.RecyclerViewType
    override fun getItemViewType(position: Int): Int {
        return data[position].getContentType()
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
        (private val binding: AdapterStaffBinding) : RecyclerViewHolder<RecyclerItem>(binding.root) {

        /**
         * Load image, text, buttons, etc. in this method from the given parameter
         * <br></br>
         *
         * @param model Is the model at the current adapter position
         */
        override fun onBindViewHolder(model: RecyclerItem?) {
            binding.model = model as StaffBase?
            if ((model as StaffBase).isFavourite)
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
