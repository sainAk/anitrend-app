package com.mxt.anitrend.koin

import android.graphics.drawable.Drawable
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.target.Target
import com.mxt.anitrend.R
import com.mxt.anitrend.analytics.AnalyticsLogging
import com.mxt.anitrend.model.entity.MyObjectBox
import com.mxt.anitrend.presenter.base.BasePresenter
import com.mxt.anitrend.presenter.fragment.MediaPresenter
import com.mxt.anitrend.util.Settings
import io.noties.markwon.Markwon
import io.noties.markwon.html.HtmlPlugin
import io.noties.markwon.image.AsyncDrawable
import io.noties.markwon.image.glide.GlideImagesPlugin
import io.noties.markwon.linkify.LinkifyPlugin
import com.mxt.anitrend.analytics.contract.ISupportAnalytics
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module


val appModules = module {
    single {
        Markwon.builder(androidApplication())
            .usePlugin(HtmlPlugin.create())
            .usePlugin(LinkifyPlugin.create())
            .usePlugin(GlideImagesPlugin.create(androidApplication()))
            .usePlugin(GlideImagesPlugin.create(Glide.with(androidApplication())))
            .usePlugin(GlideImagesPlugin.create(object : GlideImagesPlugin.GlideStore {
                override fun cancel(target: Target<*>) {
                    Glide.with(androidApplication()).clear(target)
                }

                override fun load(drawable: AsyncDrawable): RequestBuilder<Drawable> {
                    return Glide.with(androidApplication()).load(drawable.destination)
                        .transition(DrawableTransitionOptions.withCrossFade(250))
                        .transform(
                            CenterCrop(),
                            RoundedCorners(androidApplication().resources.getDimensionPixelSize(R.dimen.md_margin))
                        )
                }
            }))
            .build()
    }

    single<ISupportAnalytics> {
        AnalyticsLogging(
            context = androidContext()
        )
    }

    single {
        MyObjectBox.builder()
            .androidContext(androidApplication())
            .build()
    }

    factory {
        Settings(androidApplication())
    }
}

val appPresentersModules = module {

    factory {
        BasePresenter(
            context = androidApplication(),
            applicationPref = get()
        )
    }

    factory {
        MediaPresenter(
            context = androidApplication(),
            applicationPref = get())
    }
}