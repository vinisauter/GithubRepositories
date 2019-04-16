package com.example.githubrepositories.repository.model

import androidx.room.*

@Entity
class Repository {
    @PrimaryKey
    var id: Int? = null
    @ColumnInfo(name = "name")
    var name: String? = null
    @ColumnInfo(name = "full_name")
    var full_name: String? = null
    @ColumnInfo(name = "html_url")
    var html_url: String? = null
    @ColumnInfo(name = "description")
    var description: String? = null
    @ColumnInfo(name = "created_at")
    var created_at: String? = null
    @ColumnInfo(name = "updated_at")
    var updated_at: String? = null
    @ColumnInfo(name = "pushed_at")
    var pushed_at: String? = null
    @ColumnInfo(name = "language")
    var language: String? = null
    @ColumnInfo(name = "score")
    var score: Double? = null
    @ColumnInfo(name = "size")
    var size: Int? = null
    @ColumnInfo(name = "stargazers_count")
    var stargazers_count: Int? = null
    @ColumnInfo(name = "watchers_count")
    var watchers_count: Int? = null
    @ColumnInfo(name = "forks_count")
    var forks_count: Int? = null
    @Embedded
    var owner: Owner? = null
    @Ignore
    var archive_url: String? = null
    @Ignore
    var archived: Boolean? = null
    @Ignore
    var assignees_url: String? = null
    @Ignore
    var blobs_url: String? = null
    @Ignore
    var branches_url: String? = null
    @Ignore
    var clone_url: String? = null
    @Ignore
    var collaborators_url: String? = null
    @Ignore
    var comments_url: String? = null
    @Ignore
    var commits_url: String? = null
    @Ignore
    var compare_url: String? = null
    @Ignore
    var contents_url: String? = null
    @Ignore
    var contributors_url: String? = null
    @Ignore
    var default_branch: String? = null
    @Ignore
    var deployments_url: String? = null
    @Ignore
    var disabled: Boolean? = null
    @Ignore
    var downloads_url: String? = null
    @Ignore
    var events_url: String? = null
    @Ignore
    var fork: Boolean? = null
    @Ignore
    var forks: Int? = null
    @Ignore
    var forks_url: String? = null
    @Ignore
    var git_commits_url: String? = null
    @Ignore
    var git_refs_url: String? = null
    @Ignore
    var git_tags_url: String? = null
    @Ignore
    var git_url: String? = null
    @Ignore
    var has_downloads: Boolean? = null
    @Ignore
    var has_issues: Boolean? = null
    @Ignore
    var has_pages: Boolean? = null
    @Ignore
    var has_projects: Boolean? = null
    @Ignore
    var has_wiki: Boolean? = null
    @Ignore
    var homepage: String? = null
    @Ignore
    var hooks_url: String? = null
    @Ignore
    var issue_comment_url: String? = null
    @Ignore
    var issue_events_url: String? = null
    @Ignore
    var issues_url: String? = null
    @Ignore
    var keys_url: String? = null
    @Ignore
    var labels_url: String? = null
    @Ignore
    var languages_url: String? = null
    @Ignore
    var license: License? = null
    @Ignore
    var merges_url: String? = null
    @Ignore
    var milestones_url: String? = null
    @Ignore
    var mirror_url: Any? = null
    @Ignore
    var node_id: String? = null
    @Ignore
    var notifications_url: String? = null
    @Ignore
    var open_issues: Int? = null
    @Ignore
    var open_issues_count: Int? = null
    @Ignore
    var pulls_url: String? = null
    @Ignore
    var releases_url: String? = null
    @Ignore
    var ssh_url: String? = null
    @Ignore
    var stargazers_url: String? = null
    @Ignore
    var statuses_url: String? = null
    @Ignore
    var subscribers_url: String? = null
    @Ignore
    var subscription_url: String? = null
    @Ignore
    var svn_url: String? = null
    @Ignore
    var tags_url: String? = null
    @Ignore
    var teams_url: String? = null
    @Ignore
    var trees_url: String? = null
    @Ignore
    var url: String? = null
    @Ignore
    var watchers: Int? = null
}