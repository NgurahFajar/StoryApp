package com.ngurah.storyapp.mediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.ngurah.storyapp.data.local.RemoteKeys
import com.ngurah.storyapp.data.local.StoryAppDatabase
import com.ngurah.storyapp.data.remote.response.StoryAppItem
import com.ngurah.storyapp.data.remote.retrofit.ApiService
import com.ngurah.storyapp.utils.*

@OptIn(ExperimentalPagingApi::class)
class RemoteMediator(
    private val storyAppDatabase: StoryAppDatabase,
    private val apiService: ApiService,
    private val bearer: String

) : RemoteMediator<Int, StoryAppItem>() {

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, StoryAppItem>
    ): MediatorResult {
        val page = when(loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: INITIAL_PAGE_INDEX
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                prevKey
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                nextKey
            }
        }

        try {
            val responseData = apiService.getAllStories(bearer, page, state.config.pageSize)

            val endOfPaginationReached = responseData.listStory?.isEmpty()

            storyAppDatabase.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    storyAppDatabase.remoteKeysDao().deleteRemoteKeys()
                    storyAppDatabase.storyAppDao().deleteAll()
                }
                val prevKey = if (page == 1) null else page - 1
                val nextKey = if (endOfPaginationReached == true) null else page + 1
                val keys = responseData.listStory?.map {
                    RemoteKeys(id = it.id, prevKey = prevKey, nextKey = nextKey)
                }
                keys?.let {
                    storyAppDatabase.remoteKeysDao().insertAll(it)
                }
                responseData.listStory?.let {
                    storyAppDatabase.storyAppDao().insertStory(it)
                }
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached == true)
        } catch (exception: Exception) {
            return MediatorResult.Error(exception)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, StoryAppItem>): RemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { data ->
            storyAppDatabase.remoteKeysDao().getRemoteKeysId(data.id)
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, StoryAppItem>): RemoteKeys? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let { data ->
            storyAppDatabase.remoteKeysDao().getRemoteKeysId(data.id)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, StoryAppItem>): RemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                storyAppDatabase.remoteKeysDao().getRemoteKeysId(id)
            }
        }
    }

}