package com.dicoding.c_finance.utils

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.dicoding.c_finance.model.api.ApiService
import com.dicoding.c_finance.model.response.log.LogItem

class LogPagingSource(private val apiService: ApiService): PagingSource<Int,LogItem>(){
    override fun getRefreshKey(state: PagingState<Int, LogItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LogItem> {
        return try {
            val position = params.key ?: INITIAL_PAGE_INDEX
            val responseData = apiService.getLogs(position, params.loadSize)
            LoadResult.Page(
                data = responseData.log,
                prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1,
                nextKey = if (responseData.log.isEmpty()) null else position + 1
            )
        } catch (exception: Exception) {
            return LoadResult.Error(exception)
        }
    }
    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }
}
