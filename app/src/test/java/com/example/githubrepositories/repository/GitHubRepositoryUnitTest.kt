package com.example.githubrepositories.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.githubrepositories.livedata.LiveDataUtils
import com.example.githubrepositories.repository.model.Result
import com.example.githubrepositories.repository.remote.GitHubService
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.junit.MockitoRule
import rx.observers.TestSubscriber
import rx.schedulers.TestScheduler
import java.util.concurrent.TimeUnit

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(MockitoJUnitRunner::class)
class GitHubRepositoryUnitTest {
    @get:Rule
    var rule: MockitoRule = MockitoJUnit.rule()
    @get:Rule
    var rule2: TestRule = InstantTaskExecutorRule()
    private val testScheduler = TestScheduler()

    @Test
    fun gitHubServiceTest() {
        val testObserver = TestSubscriber<Result>()
        GitHubService.create()
            .searchListRepos("android", 5, 1)
            .subscribe(testObserver)
        testScheduler.advanceTimeBy(60, TimeUnit.SECONDS)
//        testObserver.assertValue(--)
        testObserver.assertNoErrors()
        testObserver.assertCompleted()
    }

    @Test
    fun gitHubServiceLiveDataTest() {
        val liveData = LiveDataUtils.asLiveData(
            GitHubService.create()
                .searchListRepos("android", 5, 1)
        )
        liveData.observeForever { t ->
            if (t.error != null)
                t.error!!.printStackTrace()
            else
                println(t.value)
            assertNull(t.error)
            assertNotNull(t.value)
        }
    }
}
