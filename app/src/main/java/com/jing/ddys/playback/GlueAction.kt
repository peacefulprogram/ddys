package com.jing.bilibilitv.playback

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.leanback.widget.Action
import com.jing.ddys.R

class ReplayAction(context: Context) : Action(10) {
    init {
        icon = ContextCompat.getDrawable(context, R.drawable.replay)
    }
}

class PlayListAction(context: Context) : Action(30) {
    init {
        icon = ContextCompat.getDrawable(context, R.drawable.play_list)
    }
}
