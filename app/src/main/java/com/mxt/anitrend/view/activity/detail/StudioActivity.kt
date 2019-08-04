package com.mxt.anitrend.view.activity.detail

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.appcompat.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast

import com.mxt.anitrend.R
import com.mxt.anitrend.base.custom.activity.ActivityBase
import com.mxt.anitrend.base.custom.view.widget.FavouriteToolbarWidget
import com.mxt.anitrend.model.entity.base.StudioBase
import com.mxt.anitrend.model.entity.container.request.QueryContainerBuilder
import com.mxt.anitrend.presenter.base.BasePresenter
import com.mxt.anitrend.util.GraphUtil
import com.mxt.anitrend.util.KeyUtil
import com.mxt.anitrend.util.NotifyUtil
import com.mxt.anitrend.view.fragment.detail.StudioMediaFragment

import java.util.Locale

import butterknife.BindView
import butterknife.ButterKnife

/**
 * Created by max on 2017/12/14.
 * StudioActivity
 */

class StudioActivity : ActivityBase<StudioBase, BasePresenter>() {

    @BindView(R.id.toolbar)
    var toolbar: Toolbar? = null

    private var model: StudioBase? = null

    private var favouriteWidget: FavouriteToolbarWidget? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_frame_generic)
        ButterKnife.bind(this)
        setSupportActionBar(toolbar)
        setViewModel(true)
        setPresenter(BasePresenter(this))
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
                            "%s - %s", model!!.name, model!!.siteUrl
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

    override fun onResume() {
        super.onResume()
        if (model == null)
            makeRequest()
        else
            updateUI()
    }

    /**
     * Make decisions, check for permissions or fire background threads from this method
     * N.B. Must be called after onPostCreate
     */
    override fun onActivityReady() {
        mFragment = StudioMediaFragment.newInstance(intent.extras)
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.content_frame, mFragment!!, mFragment!!.TAG)
        fragmentTransaction.commit()
    }

    override fun updateUI() {
        if (model != null) {
            if (favouriteWidget != null)
                favouriteWidget!!.setModel(model)
            getMActionBar().setTitle(model!!.name)
        }
    }

    override fun makeRequest() {
        val queryContainer = GraphUtil.getDefaultQuery(false)
            .putVariable(KeyUtil.getArg_id(), id)
        viewModel.params.putParcelable(KeyUtil.getArg_graph_params(), queryContainer)
        viewModel.requestData(KeyUtil.getSTUDIO_BASE_REQ(), applicationContext)
    }

    /**
     * Called when the model state is changed.
     *
     * @param model The new data
     */
    override fun onChanged(model: StudioBase?) {
        super.onChanged(model)
        this.model = model
        updateUI()
    }
}