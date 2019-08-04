package com.mxt.anitrend.view.activity.detail

import android.content.Intent
import androidx.databinding.DataBindingUtil
import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast

import com.mxt.anitrend.R
import com.mxt.anitrend.adapter.pager.detail.ProfilePageAdapter
import com.mxt.anitrend.base.custom.activity.ActivityBase
import com.mxt.anitrend.base.custom.view.image.WideImageView
import com.mxt.anitrend.databinding.ActivityProfileBinding
import com.mxt.anitrend.model.entity.base.UserBase
import com.mxt.anitrend.model.entity.container.request.QueryContainerBuilder
import com.mxt.anitrend.presenter.base.BasePresenter
import com.mxt.anitrend.util.CompatUtil
import com.mxt.anitrend.util.GraphUtil
import com.mxt.anitrend.util.KeyUtil
import com.mxt.anitrend.util.NotifyUtil
import com.mxt.anitrend.util.TutorialUtil
import com.mxt.anitrend.view.sheet.BottomSheetComposer
import com.ogaclejapan.smarttablayout.SmartTabLayout

import butterknife.BindView
import butterknife.ButterKnife

/**
 * Created by max on 2017/11/14.
 * Profile activity
 */

class ProfileActivity : ActivityBase<UserBase, BasePresenter>(), View.OnClickListener {

    @BindView(R.id.toolbar)
    var toolbar: Toolbar? = null
    @BindView(R.id.page_container)
    var viewPager: ViewPager? = null
    @BindView(R.id.smart_tab)
    var smartTabLayout: SmartTabLayout? = null

    private var binding: ActivityProfileBinding? = null
    private var userName: String? = null
    private var model: UserBase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile)
        setPresenter(BasePresenter(applicationContext))
        ButterKnife.bind(this)
        setSupportActionBar(toolbar)
        disableToolbarTitle()
        setViewModel(true)
        if (intent.hasExtra(KeyUtil.getArg_id()))
            id = intent.getLongExtra(KeyUtil.getArg_id(), -1)
        if (intent.hasExtra(KeyUtil.getArg_userName()))
            userName = intent.getStringExtra(KeyUtil.getArg_userName())
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        getMActionBar().setHomeAsUpIndicator(CompatUtil.getDrawable(this, R.drawable.ic_arrow_back_white_24dp))
        val profilePageAdapter = ProfilePageAdapter(supportFragmentManager, applicationContext)
        profilePageAdapter.setParams(intent.extras)
        viewPager!!.adapter = profilePageAdapter
        viewPager!!.offscreenPageLimit = offScreenLimit
        smartTabLayout!!.setViewPager(viewPager)
    }

    override fun onPostResume() {
        super.onPostResume()
        if (model == null)
            onActivityReady()
        else
            updateUI()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.profile_menu, menu)
        if (!presenter.isCurrentUser(id, userName))
            menu.findItem(R.id.action_notification).isVisible = false
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_notification -> {
                startActivity(Intent(this@ProfileActivity, NotificationActivity::class.java))
                //CompatUtil.startRevealAnim(this, item.getActionView(), new Intent(ProfileActivity.this, NotificationActivity.class));
                return true
            }
            R.id.action_message -> {
                if (model != null) {
                    if (presenter.isCurrentUser(model!!.id))
                        startActivity(Intent(this@ProfileActivity, MessageActivity::class.java))
                    else {
                        mBottomSheet = BottomSheetComposer.Builder().setUserModel(model)
                            .setRequestMode(KeyUtil.getMUT_SAVE_MESSAGE_FEED())
                            .setTitle(R.string.text_message_to)
                            .build()
                        mBottomSheet!!.show(supportFragmentManager, mBottomSheet!!.tag)
                    }
                } else
                    NotifyUtil.INSTANCE.makeText(this, R.string.text_activity_loading, Toast.LENGTH_SHORT).show()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Make decisions, check for permissions or fire background threads from this method
     * N.B. Must be called after onPostCreate
     */
    override fun onActivityReady() {
        if (id == -1 && userName == null)
            NotifyUtil.INSTANCE.createAlerter(
                this,
                R.string.text_user_model,
                R.string.layout_empty_response,
                R.drawable.ic_warning_white_18dp,
                R.color.colorStateRed
            )
        else
            makeRequest()
    }

    override fun updateUI() {
        binding!!.onClickListener = this
        binding!!.profileStatsWidget.setParams(intent.extras)
        WideImageView.setImage(binding!!.profileBanner, model!!.bannerImage)
        if (presenter.isCurrentUser(model!!.id)) {
            TutorialUtil().setContext(this)
                .setFocalColour(R.color.colorGrey600)
                .setTapTarget(KeyUtil.getKEY_NOTIFICATION_TIP())
                .setSettings(presenter.settings)
                .showTapTarget(
                    R.string.tip_notifications_title,
                    R.string.tip_notifications_text, R.id.action_notification
                )
        } else {
            TutorialUtil().setContext(this)
                .setFocalColour(R.color.colorGrey600)
                .setTapTarget(KeyUtil.getKEY_MESSAGE_TIP())
                .setSettings(presenter.settings)
                .showTapTarget(
                    R.string.tip_compose_message_title,
                    R.string.tip_compose_message_text, R.id.action_message
                )
        }
    }

    override fun makeRequest() {
        val queryContainer = GraphUtil.getDefaultQuery(false)
            .putVariable(KeyUtil.getArg_userName(), userName)
        if (id > 0)
            queryContainer.putVariable(KeyUtil.getArg_id(), id)

        viewModel.params.putParcelable(KeyUtil.getArg_graph_params(), queryContainer)
        viewModel.requestData(KeyUtil.getUSER_BASE_REQ(), applicationContext)
    }

    /**
     * Called when the model state is changed.
     *
     * @param model The new data
     */
    override fun onChanged(model: UserBase?) {
        super.onChanged(model)
        if (model != null) {
            this.id = model.id
            this.model = model
            updateUI()
        } else
            NotifyUtil.INSTANCE.createAlerter(
                this,
                R.string.text_user_model,
                R.string.layout_empty_response,
                R.drawable.ic_warning_white_18dp,
                R.color.colorStateRed
            )
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.profile_banner -> CompatUtil.imagePreview(
                this,
                view,
                model!!.bannerImage,
                R.string.image_preview_error_profile_banner
            )
        }
    }
}