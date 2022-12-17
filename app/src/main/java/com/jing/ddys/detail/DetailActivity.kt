package com.jing.ddys.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.jing.ddys.R

class DetailActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
        supportFragmentManager.beginTransaction()
            .replace(R.id.details_fragment, DetailFragment(intent.extras!![URL_KEY] as String))
            .commit()
    }

    companion object {
        const val URL_KEY = "url"
        fun navigateTo(context: Context, url: String) {
            Intent(context, DetailActivity::class.java).apply {
                putExtra(URL_KEY, url)
                context.startActivity(this)
            }
        }
    }
}