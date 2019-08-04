package com.mxt.anitrend.base.custom.view.widget

import androidx.lifecycle.Lifecycle
import android.content.Context
import android.content.Intent
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast

import com.mxt.anitrend.R
import com.mxt.anitrend.base.custom.consumer.BaseConsumer
import com.mxt.anitrend.base.custom.sheet.BottomSheetBase
import com.mxt.anitrend.base.interfaces.event.RetroCallback
import com.mxt.anitrend.base.interfaces.view.CustomView
import com.mxt.anitrend.databinding.WidgetProfileAboutPanelBinding
import com.mxt.anitrend.model.entity.anilist.Favourite
import com.mxt.anitrend.model.entity.base.UserBase
import com.mxt.anitrend.model.entity.container.attribute.PageInfo
import com.mxt.anitrend.model.entity.container.body.ConnectionContainer
import com.mxt.anitrend.model.entity.container.body.PageContainer
import com.mxt.anitrend.model.entity.container.request.QueryContainerBuilder
import com.mxt.anitrend.presenter.widget.WidgetPresenter
import com.mxt.anitrend.util.*
import com.mxt.anitrend.view.activity.detail.FavouriteActivity
import com.mxt.anitrend.view.sheet.BottomSheetListUsers

import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.core.KoinComponent
import org.koin.core.inject

import retrofit2.Call
import retrofit2.Response
import timber.log.Timber

/**
 * Created by max on 2017/11/27.
 * following, followers & favourites
 */

class AboutPanelWidget : FrameLayout, CustomView, View.OnClickListener, BaseConsumer.onRequestModelChange<UserBase>, KoinComponent {

    private var binding: WidgetProfileAboutPanelBinding? = null
    private var lifecycle: Lifecycle? = null
    private var userId: Long = 0

    private var mLastSynced: Long = 0

    private var queryContainer: QueryContainerBuilder? = null

    private var followers: PageInfo? = null
    private var following: PageInfo? = null
    private var favourites: Int = 0

    private val usersPresenter: WidgetPresenter<PageContainer<UserBase>> by inject()
    private val favouritePresenter: WidgetPresenter<ConnectionContainer<Favourite>> by inject()
    
    private var mBottomSheet: BottomSheetBase<*>? = null
    private var fragmentManager: FragmentManager? = null

    private val placeHolder = ".."

    private val isAlive: Boolean
        get() = lifecycle != null && lifecycle?.currentState.isAtLeast(Lifecycle.State.STARTED)

    constructor(context: Context) : super(context) {
        onInit()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        onInit()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        onInit()
    }

    /**
     * Optionally included when constructing custom views
     */
    override fun onInit() {
        binding = WidgetProfileAboutPanelBinding.inflate(LayoutInflater.from(context), this, true)
        binding?.onClickListener = this
    }

    fun setUserId(userId: Long, lifecycle: Lifecycle) {
        this.userId = userId
        this.lifecycle = lifecycle
        queryContainer = GraphUtil.getDefaultQuery(false)
            .putVariable(KeyUtil.arg_id, userId)
            .putVariable(KeyUtil.arg_page_limit, 1)

        if (DateUtil.timeDifferenceSatisfied(KeyUtil.TIME_UNIT_MINUTES, mLastSynced, 5)) {
            binding?.userFavouritesCount?.text = placeHolder
            binding?.userFollowersCount?.text = placeHolder
            binding?.userFollowingCount?.text = placeHolder

            mLastSynced = System.currentTimeMillis()
            requestFavourites()
            requestFollowers()
            requestFollowing()
        }
    }

    private fun requestFollowers() {
        with (usersPresenter) {

            params.putParcelable(KeyUtil.arg_graph_params, queryContainer)
            requestData(
                KeyUtil.USER_FOLLOWERS_REQ,
                context,
                object : RetroCallback<PageContainer<UserBase>> {
                    override fun onResponse(
                        call: Call<PageContainer<UserBase>>,
                        response: Response<PageContainer<UserBase>>
                    ) {
                        if (isAlive) {
                            val pageContainer = response.body()
                            if (response.isSuccessful && pageContainer != null)
                                if (pageContainer.hasPageInfo()) {
                                    followers = pageContainer.pageInfo
                                    binding?.userFollowersCount?.text = WidgetPresenter.valueFormatter(followers?.total)
                                }
                        }
                    }

                    override fun onFailure(call: Call<PageContainer<UserBase>>, throwable: Throwable) {
                        if (isAlive) {
                            throwable.printStackTrace()
                            Timber.tag(toString()).e(throwable)
                        }
                    }
                })
        }
    }

    private fun requestFollowing() {
        with (usersPresenter) {
            params.putParcelable(KeyUtil.arg_graph_params, queryContainer)
            requestData(
                KeyUtil.USER_FOLLOWING_REQ,
                context,
                object : RetroCallback<PageContainer<UserBase>> {
                    override fun onResponse(
                        call: Call<PageContainer<UserBase>>,
                        response: Response<PageContainer<UserBase>>
                    ) {
                        if (isAlive) {
                            val pageContainer = response.body()
                            if (response.isSuccessful && pageContainer != null) {
                                if (pageContainer.hasPageInfo()) {
                                    following = pageContainer.pageInfo
                                    binding?.userFollowingCount?.text = WidgetPresenter.valueFormatter(following?.total)
                                }
                            }
                        }
                    }

                    override fun onFailure(call: Call<PageContainer<UserBase>>, throwable: Throwable) {
                        if (isAlive) {
                            throwable.printStackTrace()
                            Timber.tag(toString()).e(throwable)
                        }
                    }
                })
        }
    }

    private fun requestFavourites() {
        with (favouritePresenter) {
            params.putParcelable(KeyUtil.arg_graph_params, queryContainer)
            requestData(
                KeyUtil.USER_FAVOURITES_COUNT_REQ,
                context,
                object : RetroCallback<ConnectionContainer<Favourite>> {
                    override fun onResponse(
                        call: Call<ConnectionContainer<Favourite>>,
                        response: Response<ConnectionContainer<Favourite>>
                    ) {
                        if (isAlive) {
                            val connectionContainer = response.body()
                            if (response.isSuccessful && connectionContainer != null) {
                                val favouriteConnection = connectionContainer.connection
                                if (favouriteConnection != null) {
                                    if (favouriteConnection.anime != null && favouriteConnection.anime.hasPageInfo())
                                        favourites += favouriteConnection.anime.pageInfo.total

                                    if (favouriteConnection.manga != null && favouriteConnection.manga.hasPageInfo())
                                        favourites += favouriteConnection.manga.pageInfo.total

                                    if (favouriteConnection.characters != null && favouriteConnection.characters.hasPageInfo())
                                        favourites += favouriteConnection.characters.pageInfo.total

                                    if (favouriteConnection.staff != null && favouriteConnection.staff.hasPageInfo())
                                        favourites += favouriteConnection.staff.pageInfo.total

                                    if (favouriteConnection.studios != null && favouriteConnection.studios.hasPageInfo())
                                        favourites += favouriteConnection.studios.pageInfo.total

                                    binding?.userFavouritesCount?.text = WidgetPresenter.valueFormatter(favourites)
                                }
                            }
                        }
                    }

                    override fun onFailure(call: Call<ConnectionContainer<Favourite>>, throwable: Throwable) {
                        if (isAlive) {
                            throwable.printStackTrace()
                            Timber.tag(toString()).e(throwable)
                        }
                    }
                })
        }
    }

    /**
     * Clean up any resources that won't be needed
     */
    override fun onViewRecycled() {
        favouritePresenter.onDestroy()
        usersPresenter.onDestroy()
        fragmentManager = null
        mBottomSheet = null
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.user_favourites_container -> if (favourites < 1)
                context.makeText(stringRes = R.string.text_activity_loading, duration = Toast.LENGTH_SHORT).show()
            else {
                val intent = Intent(context, FavouriteActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                intent.putExtra(KeyUtil.arg_id, userId)
                context.startActivity(intent)
            }
            R.id.user_followers_container -> if (followers == null || followers?.total < 1)
                context.makeText(stringRes = R.string.text_activity_loading, duration = Toast.LENGTH_SHORT).show()
            else if (fragmentManager != null) {
                mBottomSheet = BottomSheetListUsers.Builder().setUserId(userId)
                    .setModelCount(followers?.total)
                    .setRequestType(KeyUtil.USER_FOLLOWERS_REQ)
                    .setTitle(R.string.title_bottom_sheet_followers)
                    .build()
                mBottomSheet?.show(fragmentManager!!, mBottomSheet?.tag)
            }
            R.id.user_following_container -> if (following == null || following?.total < 1)
                context.makeText(stringRes = R.string.text_activity_loading, duration = Toast.LENGTH_SHORT).show()
            else if (fragmentManager != null) {
                mBottomSheet = BottomSheetListUsers.Builder().setUserId(userId)
                    .setModelCount(following?.total)
                    .setRequestType(KeyUtil.USER_FOLLOWING_REQ)
                    .setTitle(R.string.title_bottom_sheet_following)
                    .build()
                mBottomSheet?.show(fragmentManager!!, mBottomSheet?.tag)
            }
        }
    }

    fun setFragmentActivity(activity: FragmentActivity?) {
        if (activity != null)
            fragmentManager = activity.supportFragmentManager
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    override fun onModelChanged(consumer: BaseConsumer<UserBase>) {
        if (consumer.requestMode == KeyUtil.getMUT_TOGGLE_FOLLOW()) {
            if (followers != null) {
                var total = followers?.total
                followers?.total = if (!consumer.changeModel.isFollowing) --total else ++total
                if (isAlive)
                    binding?.userFollowersCount.text = WidgetPresenter.valueFormatter(followers?.total)
            } else if (isAlive)
                requestFollowers()
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this)
    }

    override fun onDetachedFromWindow() {
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this)
        super.onDetachedFromWindow()
    }
}
