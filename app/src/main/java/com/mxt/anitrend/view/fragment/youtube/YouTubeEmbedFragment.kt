package com.mxt.anitrend.view.fragment.youtube

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import butterknife.ButterKnife
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.mxt.anitrend.R
import com.mxt.anitrend.base.custom.fragment.FragmentBase
import com.mxt.anitrend.databinding.AdapterFeedSlideBinding
import com.mxt.anitrend.model.entity.anilist.meta.MediaTrailer
import com.mxt.anitrend.presenter.base.BasePresenter
import com.mxt.anitrend.util.KeyUtil
import com.mxt.anitrend.util.RegexUtil
import com.mxt.anitrend.util.makeText

class YouTubeEmbedFragment : FragmentBase<MediaTrailer, BasePresenter, MediaTrailer>() {

    private var mediaTrailer: MediaTrailer? = null

    private lateinit var binding: AdapterFeedSlideBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mediaTrailer = arguments?.getParcelable(KeyUtil.arg_media_trailer)
    }

    /**
     * Called to have the fragment instantiate its user interface view.
     * This is optional, and non-graphical fragments can return null (which
     * is the default implementation).  This will be called between
     * [.onCreate] and [.onActivityCreated].
     *
     *
     *
     * If you return a View from here, you will later be called in
     * [.onDestroyView] when the view is being released.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container          If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = AdapterFeedSlideBinding.inflate(inflater, container, false)
        unbinder = ButterKnife.bind(this, binding.root)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        makeRequest()
    }

    /**
     * Is automatically called in the @onStart Method if overridden in list implementation
     */
    override fun updateUI() {
        context?.also { ctx ->
            mediaTrailer?.also {
                val youtubeLink = RegexUtil.buildYoutube(it.id)
                val thumbnailUrl = RegexUtil.getYoutubeThumb(youtubeLink)
                Glide.with(ctx).load(thumbnailUrl)
                    .transition(DrawableTransitionOptions.withCrossFade(250))
                    .apply(RequestOptions.centerCropTransform())
                    .into(binding.feedStatusImage)
            }
        }
    }

    /**
     * All new or updated network requests should be handled in this method
     */
    override fun makeRequest() {
        binding.setOnClickListener { v ->
            try {
                val youtubeLink = RegexUtil.buildYoutube(mediaTrailer?.id)
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(youtubeLink)
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                e.printStackTrace()
                activity?.makeText(
                    stringRes = R.string.init_youtube_missing,
                    duration = Toast.LENGTH_SHORT
                )?.show()
            }
        }
        updateUI()
    }

    /**
     * Called when the model state is changed.
     *
     * @param model The new data
     */
    override fun onChanged(model: MediaTrailer?) {

    }

    companion object {

        fun newInstance(model: MediaTrailer): YouTubeEmbedFragment {
            val args = Bundle()
            args.putParcelable(KeyUtil.getArg_media_trailer(), model)
            val fragment = YouTubeEmbedFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
