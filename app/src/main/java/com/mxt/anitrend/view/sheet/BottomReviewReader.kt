package com.mxt.anitrend.view.sheet

import android.app.Dialog
import android.os.Bundle

import com.mxt.anitrend.R
import com.mxt.anitrend.base.custom.sheet.BottomSheetBase
import com.mxt.anitrend.base.custom.view.text.SeriesTitleView
import com.mxt.anitrend.databinding.BottomSheetReviewBinding
import com.mxt.anitrend.model.entity.anilist.Review
import com.mxt.anitrend.util.CompatUtil
import com.mxt.anitrend.util.KeyUtil

import butterknife.BindView
import butterknife.ButterKnife

/**
 * Created by max on 2017/11/05.
 * Review reader bottom sheet
 */

class BottomReviewReader : BottomSheetBase<*>() {

    private var model: Review? = null
    private var binding: BottomSheetReviewBinding? = null

    @BindView(R.id.series_title)
    var seriesTitleView: SeriesTitleView? = null

    /**
     * Set up your custom bottom sheet and check for arguments if any
     *
     * @param savedInstanceState
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null)
            model = arguments!!.getParcelable(KeyUtil.getArg_model())
    }

    /**
     * Setup your view un-binder here as well as inflating other views as needed
     * into your view stub
     *
     * @param savedInstanceState
     */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        binding = BottomSheetReviewBinding.inflate(CompatUtil.getLayoutInflater(activity))
        dialog.setContentView(binding!!.root)
        unbinder = ButterKnife.bind(this, dialog)
        createBottomSheetBehavior(binding!!.root)
        return dialog
    }

    override fun onStart() {
        super.onStart()
        binding!!.model = model
        seriesTitleView!!.setTitle(model)
    }

    class Builder : BottomSheetBase.BottomSheetBuilder() {

        override fun build(): BottomSheetBase<*> {
            return newInstance(bundle)
        }

        fun setReview(review: Review): BottomSheetBase.BottomSheetBuilder {
            bundle.putParcelable(KeyUtil.getArg_model(), review)
            return this
        }
    }

    companion object {

        fun newInstance(bundle: Bundle): BottomReviewReader {
            val fragment = BottomReviewReader()
            fragment.arguments = bundle
            return fragment
        }
    }
}
