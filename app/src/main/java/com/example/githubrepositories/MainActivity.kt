package com.example.githubrepositories

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.paging.PagedList
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.githubrepositories.livedata.TaskResult
import com.example.githubrepositories.repository.model.Repository
import com.example.githubrepositories.repository.model.Result
import com.example.githubrepositories.viewmodel.GitHubViewModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    companion object {
        const val KEY_QUERY = "git_query"
        const val DEFAULT_QUERY = "android"
    }

    lateinit var model: GitHubViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        model = ViewModelProviders.of(this).get(GitHubViewModel::class.java)

        val query = savedInstanceState?.getString(KEY_QUERY) ?: DEFAULT_QUERY
        model.setQuery(query)

        val adapter = RepositoryAdapter {
            model.retry()
        }
        list.adapter = adapter
        model.posts.observe(this, Observer<PagedList<Repository>> {
            adapter.submitList(it)
        })
        model.refreshState.observe(this, Observer {
            adapter.setState(it)
        })
        model.refreshState.observe(this, Observer {
            swipe_refresh.isRefreshing = it.isLoading()
        })
        swipe_refresh.setOnRefreshListener {
            model.refresh()
        }

        input.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                updatedQueryFromInput()
                true
            } else {
                false
            }
        }
        input.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                updatedQueryFromInput()
                true
            } else {
                false
            }
        }
    }

    private fun updatedQueryFromInput() {
        input.text.trim().toString().let {
            if (it.isNotEmpty()) {
                if (model.setQuery(it)) {
                    list.scrollToPosition(0)
                    (list.adapter as? RepositoryAdapter)?.submitList(null)
                }
            }
        }
    }

    class RepositoryAdapter(private val retryCallback: () -> Unit) :
        PagedListAdapter<Repository, RecyclerView.ViewHolder>(COMPARATOR) {
        private var networkState: TaskResult<Result>? = null
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return when (viewType) {
                R.layout.repo_item -> RepositoryViewHolder.create(parent)
                R.layout.state_item -> NetworkStateItemViewHolder.create(parent, retryCallback)
                else -> throw IllegalArgumentException("unknown view type $viewType")
            }
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            when (getItemViewType(position)) {
                R.layout.repo_item -> (holder as RepositoryViewHolder).bind(getItem(position))
                R.layout.state_item -> (holder as NetworkStateItemViewHolder).bindTo(
                    networkState
                )
            }
        }

        override fun onBindViewHolder(
            holder: RecyclerView.ViewHolder,
            position: Int,
            payloads: MutableList<Any>
        ) {
            if (payloads.isNotEmpty()) {
                val item = getItem(position)
                (holder as RepositoryViewHolder).updateScore(item)
            } else {
                onBindViewHolder(holder, position)
            }
        }

        private fun hasExtraRow() = networkState != null && networkState!!.value != null

        override fun getItemViewType(position: Int): Int {
            return if (hasExtraRow() && position == itemCount - 1) {
                R.layout.state_item
            } else {
                R.layout.repo_item
            }
        }

        override fun getItemCount(): Int {
            return super.getItemCount() + if (hasExtraRow()) 1 else 0
        }

        fun setState(newNetworkState: TaskResult<Result>?) {
            val previousState = this.networkState
            val hadExtraRow = hasExtraRow()
            this.networkState = newNetworkState
            val hasExtraRow = hasExtraRow()
            if (hadExtraRow != hasExtraRow) {
                if (hadExtraRow) {
                    notifyItemRemoved(super.getItemCount())
                } else {
                    notifyItemInserted(super.getItemCount())
                }
            } else if (hasExtraRow && previousState != newNetworkState) {
                notifyItemChanged(itemCount - 1)
            }
        }

        companion object {
            val COMPARATOR = object : DiffUtil.ItemCallback<Repository>() {
                override fun areContentsTheSame(oldItem: Repository, newItem: Repository): Boolean =
                    oldItem == newItem

                override fun areItemsTheSame(oldItem: Repository, newItem: Repository): Boolean =
                    oldItem.id?.equals(newItem.id) ?: (newItem.id == null)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_QUERY, model.currentQuery())
    }

    class RepositoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val title: TextView = view.findViewById(R.id.tv_title)
        private val subtitle: TextView = view.findViewById(R.id.tv_desc)
        private val score: TextView = view.findViewById(R.id.tv_score)
        private val thumbnail: ImageView = view.findViewById(R.id.iv_thumbnail)
        private var post: Repository? = null

        init {
            view.setOnClickListener {
                post?.html_url?.let { url ->
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    view.context.startActivity(intent)
                }
            }
        }

        fun bind(post: Repository?) {
            this.post = post
            title.text = post?.name ?: "loading"
            subtitle.text = post?.description
            score.text = "${post?.score ?: 0}"
            if (post?.owner?.avatar_url?.startsWith("http") == true) {
                thumbnail.visibility = View.VISIBLE
                Picasso.get()
                    .load(post.owner?.avatar_url)
//                    .centerCrop()
                    .into(thumbnail)
            } else {
                thumbnail.visibility = View.INVISIBLE
            }
        }

        companion object {
            fun create(parent: ViewGroup): RepositoryViewHolder {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.repo_item, parent, false)
                return RepositoryViewHolder(view)
            }
        }

        fun updateScore(item: Repository?) {
            post = item
            score.text = "${item?.score ?: 0}"
        }
    }

    class NetworkStateItemViewHolder(
        view: View,
        private val retryCallback: () -> Unit
    ) : RecyclerView.ViewHolder(view) {
        private val progressBar = view.findViewById<ProgressBar>(R.id.progress_bar)
        private val retry = view.findViewById<Button>(R.id.retry_button)
        private val errorMsg = view.findViewById<TextView>(R.id.error_msg)

        init {
            retry.setOnClickListener {
                retryCallback()
            }
        }

        fun bindTo(networkState: TaskResult<Result>?) {
            progressBar.visibility = toVisibility(networkState?.isLoading())
            retry.visibility = toVisibility(networkState?.isFailed())
            errorMsg.visibility = toVisibility(networkState?.error != null)
            errorMsg.text = networkState?.error?.localizedMessage ?: ""
        }

        companion object {
            fun create(parent: ViewGroup, retryCallback: () -> Unit): NetworkStateItemViewHolder {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.state_item, parent, false)
                return NetworkStateItemViewHolder(view, retryCallback)
            }

            fun toVisibility(constraint: Boolean?): Int {
                return if (constraint != null && constraint) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
            }
        }
    }
}
