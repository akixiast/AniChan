package com.aki.anichan.data.response.github

import com.google.gson.annotations.SerializedName

data class GitHubReleaseResponse(
    @SerializedName("tag_name")
    val tagName: String = "",
    @SerializedName("name")
    val name: String = "",
    @SerializedName("body")
    val body: String = "",
    @SerializedName("html_url")
    val htmlUrl: String = "",
    @SerializedName("assets")
    val assets: List<GitHubReleaseAsset> = listOf()
)

data class GitHubReleaseAsset(
    @SerializedName("name")
    val name: String = "",
    @SerializedName("browser_download_url")
    val browserDownloadUrl: String = "",
    @SerializedName("size")
    val size: Long = 0L
)
