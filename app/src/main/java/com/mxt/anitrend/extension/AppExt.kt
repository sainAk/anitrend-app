package com.mxt.anitrend.extension

import android.content.Context
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import com.mxt.anitrend.App
import com.mxt.anitrend.base.custom.viewmodel.ViewModelBase
import com.mxt.anitrend.presenter.widget.WidgetPresenter
import com.mxt.anitrend.util.Settings
import org.koin.core.definition.BeanDefinition
import org.koin.core.definition.Definition
import org.koin.core.definition.DefinitionFactory
import org.koin.core.definition.Options
import org.koin.core.module.Module
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import kotlin.reflect.KClass

fun Context.getPreference() = (applicationContext as App).settings

fun Context.getAnalytics() = (applicationContext as App).analytics



inline fun <reified T> FragmentActivity?.createViewModel(kClass: KClass<*>): ViewModelBase<*>? {
    return this?.let {
        ViewModelProviders.of(it).get(ViewModelBase::class.java)
    }
}