package com.jing.ddys.util

import android.view.ViewGroup
import androidx.leanback.widget.Presenter

fun Presenter(init: PresenterScope.() -> Unit): Presenter {
    val scope = PresenterScope()
    scope.init()
    return scope.buildPresenter()
}

class PresenterScope : Presenter() {
    private var createViewHolder: ((ViewGroup) -> ViewHolder)? = null

    private var bindViewHolder: ((ViewHolder, item: Any?) -> Unit)? = null
    private var unbindViewHolder: ((ViewHolder?) -> Unit)? = null

    fun onCreateViewHolder(func: (parent: ViewGroup) -> ViewHolder) {
        createViewHolder = func
    }

    fun onBindViewHolder(func: (ViewHolder, item: Any?) -> Unit) {
        bindViewHolder = func
    }


    fun onUnBindViewHolder(func: (ViewHolder?) -> Unit) {
        unbindViewHolder = func
    }

    fun buildPresenter(): Presenter = object : Presenter() {
        override fun onCreateViewHolder(parent: ViewGroup?): ViewHolder {
            return createViewHolder!!.invoke(parent!!)
        }

        override fun onBindViewHolder(viewHolder: ViewHolder?, item: Any?) {
            bindViewHolder!!.invoke(viewHolder!!, item)
        }

        override fun onUnbindViewHolder(viewHolder: ViewHolder?) {
            unbindViewHolder?.invoke(viewHolder)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup?): ViewHolder {
        return createViewHolder!!.invoke(parent!!)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder?, item: Any?) {
        bindViewHolder!!.invoke(viewHolder!!, item)
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder?) {
        unbindViewHolder?.invoke(viewHolder)
    }
}