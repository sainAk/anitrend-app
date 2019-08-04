package com.mxt.anitrend.view.activity.detail

import android.content.Intent
import android.os.Bundle
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast

import com.mxt.anitrend.R
import com.mxt.anitrend.adapter.pager.detail.CharacterPageAdapter
import com.mxt.anitrend.base.custom.activity.ActivityBase
import com.mxt.anitrend.base.custom.view.widget.FavouriteToolbarWidget
import com.mxt.anitrend.model.entity.base.CharacterBase
import com.mxt.anitrend.model.entity.container.request.QueryContainerBuilder
import com.mxt.anitrend.presenter.base.BasePresenter
import com.mxt.anitrend.util.GraphUtil
import com.mxt.anitrend.util.KeyUtil
import com.mxt.anitrend.util.NotifyUtil
import com.ogaclejapan.smarttablayout.SmartTabLayout

import java.util.Locale

import butterknife.BindView
import butterknife.ButterKnife

/**
 * Created by max on 2017/12/14.
 * character activity
 */

class CharacterActivity : ActivityBase<CharacterBase, BasePresenter>() {

    @BindView(R.id.toolbar)
    var toolbar: Toolbar? = null
    @BindView(R.id.page_container)
    var viewPager: ViewPager? = null
    @BindView(R.id.smart_tab)
    var smartTabLayout: SmartTabLayout? = null
    @BindView(R.id.coordinator)
    var coordinatorLayout: CoordinatorLayout? = null

    private var model: CharacterBase? = null

    private var favouriteWidget: FavouriteToolbarWidget? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pager_generic)
        ButterKnife.bind(this)
        setSupportActionBar(toolbar)
        setPresenter(BasePresenter(this))
        setViewModel(true)
        if (intent.hasExtra(KeyUtil.getArg_id()))
            id = intent.getLongExtra(KeyUtil.getArg_id(), -1)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        viewModel.params.putLong(KeyUtil.getArg_id(), id)
        onActivityReady()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val isAuth = presenter.settings.isAuthenticated
        menuInflater.inflate(R.menu.custom_menu, menu)
        menu.findItem(R.id.action_favourite).isVisible = isAuth
        if (isAuth) {
            val favouriteMenuItem = menu.findItem(R.id.action_favourite)
            favouriteWidget = favouriteMenuItem.actionView as FavouriteToolbarWidget
            if (model != null)
                favouriteWidget!!.setModel(model)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (model != null) {
            when (item.itemId) {
                R.id.action_share -> {
                    val intent = Intent()
                    intent.action = Intent.ACTION_SEND
                    intent.putExtra(
                        Intent.EXTRA_TEXT, String.format(
                            Locale.getDefault(),
                            "%s - %s", model!!.name.fullName, model!!.siteUrl
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
        val pageAdapter = CharacterPageAdapter(supportFragmentManager, applicationContext)
        pageAdapter.setParams(viewModel.params)
        viewPager!!.adapter = pageAdapter
        viewPager!!.offscreenPageLimit = offScreenLimit
        smartTabLayout!!.setViewPager(viewPager)
    }

    override fun onResume() {
        super.onResume()
        if (model == null)
            makeRequest()
        else
            updateUI()
    }

    override fun updateUI() {
        if (model != null)
            if (favouriteWidget != null)
                favouriteWidget!!.setModel(model)
    }


    override fun makeRequest() {
        val queryContainer = GraphUtil.getDefaultQuery(false)
            .putVariable(KeyUtil.getArg_id(), id)
        viewModel.params.putParcelable(KeyUtil.getArg_graph_params(), queryContainer)
        viewModel.requestData(KeyUtil.getCHARACTER_BASE_REQ(), applicationContext)
    }

    /**
     * Called when the model state is changed.
     *
     * @param model The new data
     */
    override fun onChanged(model: CharacterBase?) {
        super.onChanged(model)
        this.model = model
        updateUI()
    }
}