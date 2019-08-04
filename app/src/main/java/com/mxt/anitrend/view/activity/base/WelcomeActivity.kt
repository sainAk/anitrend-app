package com.mxt.anitrend.view.activity.base

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.core.content.ContextCompat
import android.view.View

import com.codemybrainsout.onboarder.AhoyOnboarderActivity
import com.codemybrainsout.onboarder.AhoyOnboarderCard
import com.mxt.anitrend.R
import com.mxt.anitrend.util.CompatUtil
import com.mxt.anitrend.view.activity.index.MainActivity

import java.util.ArrayList

/**
 * Created by max on 2017/11/09.
 */

class WelcomeActivity : AhoyOnboarderActivity() {

    private var ahoyPages: List<AhoyOnboarderCard>? = null

    private fun applyStyle(ahoyOnboarderCard: AhoyOnboarderCard): AhoyOnboarderCard {
        ahoyOnboarderCard.setBackgroundColor(R.color.black_transparent)
        ahoyOnboarderCard.setTitleColor(R.color.grey_200)
        ahoyOnboarderCard.setDescriptionColor(R.color.grey_300)
        return ahoyOnboarderCard
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ahoyPages = ArrayList(
                CompatUtil.constructListFrom(
                    applyStyle(
                        AhoyOnboarderCard(
                            getString(R.string.app_name),
                            getString(R.string.app_greeting) + " " + getString(R.string.app_provider),
                            R.mipmap.ic_launcher
                        )
                    ),
                    applyStyle(
                        AhoyOnboarderCard(
                            getString(R.string.app_intro_colors_title),
                            getString(R.string.app_intro_colors_text),
                            R.drawable.ic_format_paint_white_24dp
                        )
                    ),
                    applyStyle(
                        AhoyOnboarderCard(
                            getString(R.string.app_intro_content_title),
                            getString(R.string.app_intro_content_text),
                            R.drawable.ic_bubble_chart_white_24dp
                        )
                    ),
                    applyStyle(
                        AhoyOnboarderCard(
                            getString(R.string.app_intro_search_title),
                            getString(R.string.app_intro_search_text),
                            R.drawable.ic_search_white_24dp
                        )
                    ),
                    applyStyle(
                        AhoyOnboarderCard(
                            getString(R.string.app_intro_videos_title),
                            getString(R.string.app_intro_videos_text),
                            R.drawable.ic_slow_motion_video_white_24dp
                        )
                    )
                )
            )
        } else {
            ahoyPages = ArrayList(
                CompatUtil.constructListFrom(
                    applyStyle(
                        AhoyOnboarderCard(
                            getString(R.string.app_name),
                            getString(R.string.app_greeting) + " " + getString(R.string.app_provider),
                            R.mipmap.ic_launcher
                        )
                    ),
                    applyStyle(
                        AhoyOnboarderCard(
                            getString(R.string.app_intro_colors_title),
                            getString(R.string.app_intro_colors_text),
                            R.drawable.ic_format_paint_white_48dp
                        )
                    ),
                    applyStyle(
                        AhoyOnboarderCard(
                            getString(R.string.app_intro_content_title),
                            getString(R.string.app_intro_content_text),
                            R.drawable.ic_bubble_chart_white_48dp
                        )
                    ),
                    applyStyle(
                        AhoyOnboarderCard(
                            getString(R.string.app_intro_search_title),
                            getString(R.string.app_intro_search_text),
                            R.drawable.ic_search_white_48dp
                        )
                    ),
                    applyStyle(
                        AhoyOnboarderCard(
                            getString(R.string.app_intro_videos_title),
                            getString(R.string.app_intro_videos_text),
                            R.drawable.ic_slow_motion_video_white_48dp
                        )
                    )
                )
            )
        }

        setFinishButtonDrawableStyle(ContextCompat.getDrawable(this, R.drawable.finish_button_style))
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        setFinishButtonTitle(R.string.get_started)
        showNavigationControls(true)
        setGradientBackground()
        setOnboardPages(ahoyPages!!)
    }

    override fun onFinishButtonPressed() {
        val target = findViewById<View>(com.codemybrainsout.onboarder.R.id.btn_skip)
        CompatUtil.startRevealAnim(this, target, Intent(this@WelcomeActivity, MainActivity::class.java), true)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            if (hasFocus)
                window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
    }
}
