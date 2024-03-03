package com.jing.ddys.view

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout

typealias VisibilityListener = (Int) -> Unit

class VisibilityListenableLinearLayout(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int,
    defStyleRes: Int
) : LinearLayout(context, attrs, defStyleAttr, defStyleRes) {


    private var _visibilityListener: VisibilityListener? = null

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : this(context, attrs, defStyleAttr, 0)

    constructor(
        context: Context,
        attrs: AttributeSet?
    ) : this(context, attrs, 0)

    constructor(context: Context) : this(context, null)

    override fun setVisibility(visibility: Int) {
        super.setVisibility(visibility)
        _visibilityListener?.invoke(visibility)
    }

    fun setVisibilityListener(listener: VisibilityListener) {
        _visibilityListener = listener
    }

    fun removeVisibilityListener() {
        _visibilityListener = null
    }
}

