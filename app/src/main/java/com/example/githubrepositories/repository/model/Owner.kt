package com.example.githubrepositories.repository.model

import androidx.room.ColumnInfo
import androidx.room.Ignore

class Owner {
    @ColumnInfo(name = "login")
    var login: String? = null
    @ColumnInfo(name = "avatar_url")
    var avatar_url: String? = null
    @Ignore
    var events_url: String? = null
    @Ignore
    var followers_url: String? = null
    @Ignore
    var following_url: String? = null
    @Ignore
    var gists_url: String? = null
    @Ignore
    var gravatar_id: String? = null
    @Ignore
    var html_url: String? = null
    @Ignore
    var node_id: String? = null
    @Ignore
    var organizations_url: String? = null
    @Ignore
    var received_events_url: String? = null
    @Ignore
    var repos_url: String? = null
    @Ignore
    var site_admin: Boolean? = null
    @Ignore
    var starred_url: String? = null
    @Ignore
    var subscriptions_url: String? = null
    @Ignore
    var type: String? = null
    @Ignore
    var url: String? = null
}