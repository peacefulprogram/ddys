package com.jing.ddys.repository

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import java.util.regex.Pattern

object HttpUtil {

    private const val BASE_URL = "https://ddys.tv"
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(Interceptor { chain ->
            val originalReq = chain.request()
            val builder = originalReq
                .newBuilder()
                .header(
                    "user-agent",
                    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/105.0.0.0 Safari/537.36"
                )
            if (originalReq.header("referer") == null) {
                builder.header("referer", "$BASE_URL/true-sight/")
            }
            builder.build()
                .let {
                    chain.proceed(it)
                }
        })
        .build()


    suspend fun queryVideoOfCategory(pageUrl: String, page: Int): BasePageResult<VideoCardInfo> {
        var finalUrl = BASE_URL + pageUrl
        if (page > 1) {
            finalUrl = "$finalUrl/page/$page/"
        }
        val resp =
            okHttpClient.newCall(Request.Builder().url(finalUrl).get().build()).execute()
        val html = resp.body!!.byteString().utf8()
        val pattern = Pattern.compile("background-image:\\s*url\\((.*?)\\);")
        fun findImageUrl(input: String): String {
            val matcher = pattern.matcher(input)
            matcher.find()
            return matcher.group(1) ?: ""
        }

        val document = Jsoup.parse(html)
        val videoList = document.select("article").map { article ->
            val img = article.selectFirst(".post-box-image")!!
            val imageUrl = findImageUrl(img.attr("style"))
            val title = article.selectFirst(".post-box-title")!!.text()
            val url = article.dataset()["href"]!!
            val subTitle = article.selectFirst(".post-box-text p")?.text()
            VideoCardInfo(
                imageUrl = imageUrl,
                title = title,
                url = url,
                subTitle = subTitle
            )
        }
        val hasNext = document.selectFirst(".nav-links")?.let {
            it.children().isNotEmpty() && it.child(it.childrenSize() - 1).text() == "下一页"
        } ?: false
        return BasePageResult(
            data = videoList,
            page = page,
            hasNext = hasNext
        )
    }
}