package com.jing.ddys.repository

import com.google.gson.Gson
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import java.util.regex.Pattern

object HttpUtil {

    const val USER_AGENT =
        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/105.0.0.0 Safari/537.36"
    private const val BASE_URL = "https://ddys.tv"
    private val gson = Gson()
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(Interceptor { chain ->
            val originalReq = chain.request()
            val builder = originalReq
                .newBuilder()
                .header(
                    "user-agent",
                    USER_AGENT
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

    fun queryDetailPage(pageUrl: String): VideoDetailInfo {
        val html = okHttpClient.newCall(Request.Builder().url(pageUrl).get().build())
            .execute().body!!.byteString().utf8()
        val document = Jsoup.parse(html, pageUrl)
        val seasonList = document.selectFirst(".page-links")?.children()?.map { season ->
            val url = season.absUrl("href")
            VideoSeason(
                seasonName = season.text().trim(),
                seasonUrl = url.takeIf { it.isNotEmpty() },
                currentSeason = url.isEmpty()
            )
        } ?: emptyList()
        val relatedVideos = document.select(".crp_related li").map { li ->
            val url = li.selectFirst("a")!!.absUrl("href")
            val imgSrc = li.selectFirst("img")?.dataset()?.get("src") ?: ""
            val title = li.selectFirst(".crp_title")!!.text().trim()
            VideoCardInfo(
                imageUrl = imgSrc,
                title = title,
                subTitle = null,
                url = url
            )
        }
        val infoArea = document.selectFirst(".doulist-subject")!!
        val title = infoArea.selectFirst(".title")!!.text().trim()
        val cover = infoArea.selectFirst(".post img")!!.dataset()["src"]
        val ratingNumber = infoArea.selectFirst(".rating_nums")!!.text()
        val description = infoArea.selectFirst(".abstract")!!
            .html()
            .split("<br>").joinToString(separator = System.getProperty("line.separator")!!) {
                it.replace(Regex("</?p>"), "").trim()
            }
        val tracks = gson.fromJson(
            document.select(".wp-playlist-script").html(),
            Map::class.java
        )["tracks"] as List<Map<String, Any>>
        val episodeList = tracks.map {
            VideoEpisode(
                id = it["src1"] as String,
                name = it["caption"] as String
            )
        }
        return VideoDetailInfo(
            title = title,
            coverUrl = cover ?: "",
            seasons = seasonList,
            episodes = episodeList,
            relatedVideo = relatedVideos,
            rating = ratingNumber,
            description = description,
            detailPageUrl = pageUrl
        )

    }

    fun queryVideoUrl(id: String, detailPageUrl: String): String {
        val req = Request.Builder()
            .header("referer", detailPageUrl)
            .url("$BASE_URL/getvddr/video?id=$id&dim=1080P&type=mix")
            .get()
            .build()
        val resp = okHttpClient.newCall(req).execute().body!!.byteString().utf8()!!
        val map = gson.fromJson(resp, Map::class.java)
        val err = map["err"]
        if (err != null) {
            throw RuntimeException("获取链接地址错误,请稍后再试:$err")
        }
        val url = map["url"] as String?
        if (url != null) {
            return url
        }
        val pin = map["pin"] as String?
        if (pin != null) {
            throw RuntimeException("暂不支持解析pin")
        }
        throw RuntimeException("无法识别的接口响应:$resp")
    }
}