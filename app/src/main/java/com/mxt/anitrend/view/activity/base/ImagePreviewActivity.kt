package com.mxt.anitrend.view.activity.base

import android.Manifest
import android.app.DownloadManager
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import androidx.core.app.ActivityCompat
import androidx.appcompat.widget.Toolbar
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.Window
import android.view.WindowManager
import android.view.animation.DecelerateInterpolator
import android.widget.Toast

import com.bumptech.glide.Glide
import com.github.chrisbanes.photoview.PhotoView
import com.mxt.anitrend.R
import com.mxt.anitrend.base.custom.activity.ActivityBase
import com.mxt.anitrend.presenter.base.BasePresenter
import com.mxt.anitrend.util.DialogUtil
import com.mxt.anitrend.util.KeyUtil
import com.mxt.anitrend.util.NotifyUtil

import butterknife.BindView
import butterknife.ButterKnife

/**
 * Created by max on 2017/11/14.
 * ImagePreviewActivity
 */

class ImagePreviewActivity : ActivityBase<Void, BasePresenter>() {

    @BindView(R.id.preview_image)
    internal var mImageView: PhotoView? = null
    @BindView(R.id.toolbar_preview_image)
    internal var mToolbar: Toolbar? = null

    private var mImageUri: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_preview)
        ButterKnife.bind(this)

        setSupportActionBar(mToolbar)
        if (supportActionBar != null)
            supportActionBar!!.setTitle("")

        mImageView!!.setOnClickListener { view ->
            mToolbar!!.animate()
                .alpha((if (mToolbar!!.alpha == 1f) 0 else 1).toFloat())
                .setDuration(500).interpolator = DecelerateInterpolator()
        }
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        if (intent.hasExtra(KeyUtil.getArg_model()) && !TextUtils.isEmpty(intent.getStringExtra(KeyUtil.getArg_model()))) {
            mImageUri = intent.getStringExtra(KeyUtil.getArg_model())
            Glide.with(this).load(mImageUri).into(mImageView!!)
        } else
            NotifyUtil.INSTANCE.makeText(
                this,
                R.string.layout_empty_response,
                R.drawable.ic_warning_white_18dp,
                Toast.LENGTH_SHORT
            ).show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (mImageUri != null && !mImageUri!!.isEmpty())
            menuInflater.inflate(R.menu.image_preview_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val intent: Intent
        when (item.itemId) {
            R.id.image_preview_download -> {
                if (requestPermissionIfMissing(Manifest.permission.WRITE_EXTERNAL_STORAGE))
                    downloadAttachment()
                else if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                )
                    DialogUtil.Companion.createMessage(
                        this,
                        R.string.title_permission_write,
                        R.string.text_permission_write,
                        { dialog, which ->
                            when (which) {
                                POSITIVE -> ActivityCompat.requestPermissions(
                                    this,
                                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                                    ActivityBase.Companion.getREQUEST_PERMISSION()
                                )
                                NEGATIVE -> NotifyUtil.INSTANCE.makeText(
                                    this,
                                    R.string.canceled_by_user,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        })
                return true
            }
            R.id.image_preview_share, R.id.action_share -> {
                intent = Intent()
                intent.action = Intent.ACTION_SEND
                intent.type = "text/plain"
                intent.putExtra(android.content.Intent.EXTRA_TEXT, mImageUri)
                startActivity(Intent.createChooser(intent, resources.getText(R.string.image_preview_share)))
                return true
            }
            R.id.image_preview_link -> {
                try {
                    intent = Intent()
                    intent.action = Intent.ACTION_VIEW
                    intent.data = Uri.parse(mImageUri)
                    startActivity(intent)
                } catch (e: Exception) {
                    Log.e(toString(), e.localizedMessage!!)
                    NotifyUtil.INSTANCE.makeText(this, R.string.text_unknown_error, Toast.LENGTH_SHORT).show()
                }

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
        updateUI()
    }

    override fun updateUI() {

    }

    override fun makeRequest() {

    }

    private fun downloadAttachment() {
        val imageUri = Uri.parse(mImageUri)
        val r = DownloadManager.Request(imageUri)
        r.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, imageUri.lastPathSegment)
        r.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

        // Start download
        val dm = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        if (dm != null) {
            dm.enqueue(r)
            NotifyUtil.INSTANCE.createAlerter(
                this, R.string.title_download_info, R.string.text_download_info,
                R.drawable.ic_cloud_download_white_24dp, R.color.colorStateGreen, KeyUtil.getDURATION_SHORT()
            )
        } else
            NotifyUtil.INSTANCE.createAlerter(
                this, R.string.title_download_info, R.string.text_unknown_error,
                R.drawable.ic_cloud_download_white_24dp, R.color.colorStateRed, KeyUtil.getDURATION_SHORT()
            )
    }

    /**
     * Called for each of the requested permissions as they are granted
     *
     * @param permission the current permission granted
     */
    override fun onPermissionGranted(permission: String) {
        super.onPermissionGranted(permission)
        if (permission == Manifest.permission.WRITE_EXTERNAL_STORAGE)
            downloadAttachment()
    }
}
