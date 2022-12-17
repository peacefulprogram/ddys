package com.jing.ddys.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.leanback.widget.VerticalGridView

class CustomVerticalGridView(context: Context, attrs: AttributeSet?, defStyle: Int) :
    VerticalGridView(context, attrs, defStyle) {

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context) : this(context, null)

    var focusSearchInterceptor: ((focused: View?, focusedPosition: Int, direction: Int) -> View?)? =
        null

    override fun focusSearch(focused: View?, direction: Int): View {
        val focusedPosition =
            if (focused == null || focused.parent != this) -1 else getChildAdapterPosition(focused)
        return focusSearchInterceptor?.invoke(focused, focusedPosition, direction)
            ?: super.focusSearch(focused, direction)
    }
}