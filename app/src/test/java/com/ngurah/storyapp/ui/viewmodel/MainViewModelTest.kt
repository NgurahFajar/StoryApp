package com.ngurah.storyapp.ui.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.ngurah.storyapp.DataStoryDummy
import com.ngurah.storyapp.MainDispatcherRule
import com.ngurah.storyapp.data.remote.response.StoryAppItem
import com.ngurah.storyapp.data.repository.StoryAppRepository
import com.ngurah.storyapp.getOrAwaitValue
import com.ngurah.storyapp.ui.adapter.MainAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest{
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()
    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()
    @Mock
    private lateinit var storyAppRepository: StoryAppRepository
    private  var bearer = ""

    @Test
    fun `Pengecekan ketika berhasil memuat data cerita`() = runTest {
        val  dummyDataStory = DataStoryDummy.generateDummyStoryAppResponse()
        val data: PagingData<StoryAppItem> = StoryAppPagingSource.snapshot(dummyDataStory)
        val expectedStory = MutableLiveData<PagingData<StoryAppItem>>()
        expectedStory.value = data
        Mockito.`when`(storyAppRepository.getStories(bearer)).thenReturn(expectedStory)
        val mainViewModel = MainViewModel(storyAppRepository)
        val actualStory: PagingData<StoryAppItem> = mainViewModel.getStoriesUser(bearer).getOrAwaitValue()
        val differ = AsyncPagingDataDiffer(
            diffCallback = MainAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualStory)

        // Memastikan data tidak null.
        val snapshot = differ.snapshot().items
        assertNotNull(snapshot)

        // Memastikan jumlah data sesuai dengan yang diharapkan.
        assertEquals(dummyDataStory.size, snapshot.size)

        // Memastikan data pertama yang dikembalikan sesuai.
        assertEquals(dummyDataStory[0], snapshot[0])        // testinng bagian ini sudah diperbaiki dengan menghilangkan if else condition.

    }

    @Test
    fun `Pengecekan ketika tidak ada data cerita`() = runTest {
        val bearer=""
        val data: PagingData<StoryAppItem> = PagingData.from(emptyList())
        val expectedQuote = MutableLiveData<PagingData<StoryAppItem>>()
        expectedQuote.value = data
        Mockito.`when`(storyAppRepository.getStories(bearer)).thenReturn(expectedQuote)
        val mainViewModel = MainViewModel(storyAppRepository)
        val actualStory: PagingData<StoryAppItem> = mainViewModel.getStoriesUser(bearer).getOrAwaitValue()
        val differ = AsyncPagingDataDiffer(
            diffCallback = MainAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualStory)

        // Memastikan jumlah data yang dikembalikan nol
        val snapshot = differ.snapshot().items
        assertEquals(0, snapshot.size)
    }
}


class StoryAppPagingSource: PagingSource<Int, LiveData<List<StoryAppItem>>>() {
    companion object {
        fun snapshot(items: List<StoryAppItem>): PagingData<StoryAppItem> {
            return PagingData.from(items)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, LiveData<List<StoryAppItem>>>): Int {
        return 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<StoryAppItem>>> {
        return LoadResult.Page(emptyList(), 0, 1)
    }
}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}