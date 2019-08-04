package com.mxt.anitrend.view.fragment.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import com.annimon.stream.Stream
import com.mxt.anitrend.R
import com.mxt.anitrend.base.custom.fragment.FragmentBase
import com.mxt.anitrend.databinding.FragmentUserAboutBinding
import com.mxt.anitrend.model.entity.anilist.User
import com.mxt.anitrend.model.entity.base.StatsRing
import com.mxt.anitrend.model.entity.container.request.QueryContainerBuilder
import com.mxt.anitrend.presenter.base.BasePresenter
import com.mxt.anitrend.util.CompatUtil
import com.mxt.anitrend.util.GraphUtil
import com.mxt.anitrend.util.KeyUtil
import com.mxt.anitrend.util.NotifyUtil

import java.util.ArrayList

import butterknife.ButterKnife
import butterknife.OnClick

/**
 * Created by max on 2017/11/27.
 * about user fragment for the profile
 */

class UserOverviewFragment : FragmentBase<User, BasePresenter, User>() {

    private var binding: FragmentUserAboutBinding? = null
    private var model: User? = null

    private var userId: Long = 0
    private var userName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            if (arguments!!.containsKey(KeyUtil.getArg_id()))
                userId = arguments!!.getLong(KeyUtil.getArg_id())
            else
                userName = arguments!!.getString(KeyUtil.getArg_userName())
        }
        setIsMenuDisabled(true)
        setPresenter(BasePresenter(context))
        setViewModel(true)
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
        binding = FragmentUserAboutBinding.inflate(inflater, container, false)
        unbinder = ButterKnife.bind(this, binding!!.root)
        binding!!.stateLayout.showLoading()
        return binding!!.root
    }

    override fun onStart() {
        super.onStart()
        makeRequest()
    }

    /**
     * Is automatically called in the @onStart Method if overridden in list implementation
     */
    override fun updateUI() {
        binding!!.model = model
        binding!!.stateLayout.showContent()
        binding!!.widgetStatus.setTextData(model!!.about)

        binding!!.userFollowStateWidget.setUserModel(model)
        binding!!.userAboutPanelWidget.setFragmentActivity(activity)
        binding!!.userAboutPanelWidget.setUserId(model!!.id, lifecycle)
        showRingStats()
    }

    /**
     * All new or updated network requests should be handled in this method
     */
    override fun makeRequest() {
        val queryContainer = GraphUtil.getDefaultQuery(false)
            .putVariable(KeyUtil.getArg_userName(), userName)
        if (userId > 0)
            queryContainer.putVariable(KeyUtil.getArg_id(), userId)
        viewModel.params.putParcelable(KeyUtil.getArg_graph_params(), queryContainer)
        viewModel.requestData(KeyUtil.getUSER_OVERVIEW_REQ(), context!!)
    }

    /**
     * Called when the model state is changed.
     *
     * @param model The new data
     */
    override fun onChanged(model: User?) {
        if (model != null) {
            this.model = model
            updateUI()
        } else
            binding!!.stateLayout.showError(
                CompatUtil.getDrawable(context, R.drawable.ic_emoji_sweat),
                getString(R.string.layout_empty_response), getString(R.string.try_again)
            ) { view ->
                binding!!.stateLayout.showLoading()
                makeRequest()
            }
    }

    /**
     * Called when the view previously created by [.onCreateView] has
     * been detached from the fragment.  The next time the fragment needs
     * to be displayed, a new view will be created.  This is called
     * after [.onStop] and before [.onDestroy].  It is called
     * *regardless* of whether [.onCreateView] returned a
     * non-null view.  Internally it is called after the view's state has
     * been saved but before it has been removed from its parent.
     */
    override fun onDestroyView() {
        if (binding != null)
            binding!!.userAboutPanelWidget.onViewRecycled()
        super.onDestroyView()
    }

    private fun generateStatsData(): List<StatsRing> {
        var userGenreStats: List<StatsRing> = ArrayList()
        if (model!!.stats != null && !CompatUtil.isEmpty(model!!.stats.favouredGenres)) {
            val highestValue = Stream.of<GenreStats>(model!!.stats.favouredGenres)
                .max({ o1, o2 -> if (o1.getAmount() > o2.getAmount()) 1 else -1 })
                .get().getAmount()

            userGenreStats = Stream.of<GenreStats>(model!!.stats.favouredGenres)
                .sortBy({ s -> -s.getAmount() }).map({ genreStats ->
                    val percentage = genreStats.getAmount().toFloat() / highestValue.toFloat() * 100f
                    StatsRing(percentage.toInt(), genreStats.getGenre(), genreStats.getAmount().toString())
                }).limit(5).toList()
        }

        return userGenreStats
    }

    private fun showRingStats() {
        val ringList = generateStatsData()
        if (ringList.size > 1) {
            binding!!.userStats.setDrawBg(
                CompatUtil.isLightTheme(context),
                CompatUtil.getColorFromAttr(context, R.attr.subtitleColor)
            )
            binding!!.userStats.setData(ringList, 500)
        }
    }

    /**
     * Called when a view has been clicked.
     *
     * @param view The view that was clicked.
     */
    @OnClick(R.id.user_avatar, R.id.user_stats_container)
    override fun onClick(view: View) {
        when (view.id) {
            R.id.user_avatar -> CompatUtil.imagePreview(
                activity,
                view,
                model!!.avatar.large,
                R.string.image_preview_error_user_avatar
            )
            R.id.user_stats_container -> {
                val ringList = generateStatsData()
                if (ringList.size > 1) {
                    binding!!.userStats.setDrawBg(
                        CompatUtil.isLightTheme(context),
                        CompatUtil.getColorFromAttr(context, R.attr.subtitleColor)
                    )
                    binding!!.userStats.setData(ringList, 500)
                } else
                    NotifyUtil.INSTANCE.makeText(activity, R.string.text_error_request, Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {

        fun newInstance(args: Bundle): UserOverviewFragment {
            val fragment = UserOverviewFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
