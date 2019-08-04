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
import com.mxt.anitrend.adapter.pager.detail.AnimePageAdapter
import com.mxt.anitrend.adapter.pager.detail.MangaPageAdapter
import com.mxt.anitrend.base.custom.activity.ActivityBase
import com.mxt.anitrend.base.custom.pager.BaseStatePageAdapter
import com.mxt.anitrend.base.custom.view.image.WideImageView
import com.mxt.anitrend.base.custom.view.widget.FavouriteToolbarWidget
import com.mxt.anitrend.databinding.ActivitySeriesBinding
import com.mxt.anitrend.model.entity.base.MediaBase
import com.mxt.anitrend.model.entity.container.request.QueryContainerBuilder
import com.mxt.anitrend.presenter.fragment.MediaPresenter
import com.mxt.anitrend.util.CompatUtil
import com.mxt.anitrend.util.GraphUtil
import com.mxt.anitrend.util.KeyUtil
import com.mxt.anitrend.util.MediaActionUtil
import com.mxt.anitrend.util.NotifyUtil
import com.mxt.anitrend.util.TapTargetUtil
import com.mxt.anitrend.util.TutorialUtil
import com.ogaclejapan.smarttablayout.SmartTabLayout

import java.util.Locale

import butterknife.BindView
import butterknife.ButterKnife
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt

/**
 * Created by max on 2017/12/01.
 * Media activity
 */

class MediaActivity : ActivityBase<MediaBase, MediaPresenter>(), View.OnClickListener {

    private var binding: ActivitySeriesBinding? = null
    private var model: MediaBase? = null

    @KeyUtil.MediaType
    private var mediaType: String? = null

    private var favouriteWidget: FavouriteToolbarWidget? = null
    private var manageMenuItem: MenuItem? = null

    @BindView(R.id.toolbar)
    var toolbar: Toolbar? = null
    @BindView(R.id.page_container)
    var viewPager: ViewPager? = null
    @BindView(R.id.smart_tab)
    var smartTabLayout: SmartTabLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_series)
        setPresenter(MediaPresenter(applicationContext))
        ButterKnife.bind(this)
        setSupportActionBar(toolbar)
        disableToolbarTitle()
        setViewModel(true)
        if (intent.hasExtra(KeyUtil.getArg_id()))
            id = intent.getLongExtra(KeyUtil.getArg_id(), -1)
        if (intent.hasExtra(KeyUtil.getArg_mediaType()))
            mediaType = intent.getStringExtra(KeyUtil.getArg_mediaType())
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        getMActionBar().setHomeAsUpIndicator(CompatUtil.getDrawable(this, R.drawable.ic_arrow_back_white_24dp))
        onActivityReady()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val isAuth = presenter.settings.isAuthenticated
        menuInflater.inflate(R.menu.media_base_menu, menu)
        menu.findItem(R.id.action_favourite).isVisible = isAuth

        manageMenuItem = menu.findItem(R.id.action_manage)
        manageMenuItem!!.isVisible = isAuth
        setManageMenuItemIcon()

        if (isAuth) {
            val favouriteMenuItem = menu.findItem(R.id.action_favourite)
            favouriteWidget = favouriteMenuItem.actionView as FavouriteToolbarWidget
            setFavouriteWidgetMenuItemIcon()
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (model != null) {
            when (item.itemId) {
                R.id.action_manage -> {
                    mediaActionUtil = MediaActionUtil.Builder()
                        .setId(model!!.id).build(this)
                    mediaActionUtil!!.startSeriesAction()
                }
                R.id.action_share -> {
                    val intent = Intent()
                    intent.action = Intent.ACTION_SEND
                    intent.putExtra(
                        Intent.EXTRA_TEXT, String.format(
                            Locale.getDefault(),
                            "%s - %s", model!!.title.userPreferred, model!!.siteUrl
                        )
                    )
                    intent.type = "text/plain"
                    startActivity(intent)
                }
            }
        } else
            NotifyUtil.INSTANCE.makeText(applicationContext, R.string.text_activity_loading, Toast.LENGTH_SHORT).show()
        return super.onOptionsItemSelected(item)
    }

    /**
     * Make decisions, check for permissions or fire background threads from this method
     * N.B. Must be called after onPostCreate
     */
    override fun onActivityReady() {
        if (mediaType != null) {
            var baseStatePageAdapter: BaseStatePageAdapter =
                AnimePageAdapter(supportFragmentManager, applicationContext)
            if (!CompatUtil.equals(mediaType, KeyUtil.getANIME()))
                baseStatePageAdapter = MangaPageAdapter(supportFragmentManager, applicationContext)
            baseStatePageAdapter.setParams(intent.extras)
            viewPager!!.adapter = baseStatePageAdapter
            viewPager!!.offscreenPageLimit = offScreenLimit
            smartTabLayout!!.setViewPager(viewPager)
        } else
            NotifyUtil.INSTANCE.createAlerter(
                this,
                R.string.text_error_request,
                R.string.text_unknown_error,
                R.drawable.ic_warning_white_18dp,
                R.color.colorStateRed
            )
    }

    override fun onResume() {
        super.onResume()
        if (model == null)
            makeRequest()
        else
            updateUI()
    }

    override fun updateUI() {
        if (model != null) {
            binding!!.model = model
            binding!!.onClickListener = this
            WideImageView.setImage(binding!!.seriesBanner, model!!.bannerImage)
            setFavouriteWidgetMenuItemIcon()
            setManageMenuItemIcon()
            if (presenter.settings.isAuthenticated) {
                val favouritesPrompt = TutorialUtil().setContext(this)
                    .setFocalColour(R.color.secondaryTextColor)
                    .setTapTarget(KeyUtil.getKEY_DETAIL_TIP())
                    .setSettings(presenter.settings)
                    .createTapTarget(
                        R.string.tip_series_options_title,
                        R.string.tip_series_options_message, R.id.action_manage
                    )
                TapTargetUtil.showMultiplePrompts(favouritesPrompt)
            }
        }
    }

    override fun makeRequest() {
        val queryContainer = GraphUtil.getDefaultQuery(false)
            .putVariable(KeyUtil.getArg_mediaType(), mediaType)
            .putVariable(KeyUtil.getArg_id(), id)

        viewModel.params.putParcelable(KeyUtil.getArg_graph_params(), queryContainer)
        viewModel.requestData(KeyUtil.getMEDIA_BASE_REQ(), applicationContext)
    }

    /**
     * Called when the model state is changed.
     *
     * @param model The new data
     */
    override fun onChanged(model: MediaBase?) {
        super.onChanged(model)
        this.model = model
        updateUI()
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.series_banner -> CompatUtil.imagePreview(
                this,
                view,
                model!!.bannerImage,
                R.string.image_preview_error_series_banner
            )
        }
    }

    override fun onDestroy() {
        if (favouriteWidget != null)
            favouriteWidget!!.onViewRecycled()
        super.onDestroy()
    }

    private fun setManageMenuItemIcon() {
        if (model != null && model!!.mediaListEntry != null && manageMenuItem != null)
            manageMenuItem!!.setIcon(CompatUtil.getDrawable(this, R.drawable.ic_mode_edit_white_24dp))
    }

    private fun setFavouriteWidgetMenuItemIcon() {
        if (model != null && favouriteWidget != null)
            favouriteWidget!!.setModel(model)
    }
}
