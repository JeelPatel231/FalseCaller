package tel.jeelpa.falsecaller.paging

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import tel.jeelpa.falsecaller.paging.core.Page
import tel.jeelpa.falsecaller.paging.core.cached

class IteratorPagingSource<TData : Any>(
    iterator: Iterator<Page<TData>>
): PagingSource<Int, TData>() {

    private val cachedPages = iterator.cached()

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, TData> {
        val pageNumber = params.key ?: 0
        val items = cachedPages[pageNumber].items

        val prevKey = if (pageNumber > 0) pageNumber - 1 else null
        val nextKey = if (cachedPages.hasNext()) pageNumber + 1 else null

        return LoadResult.Page(
            items,
            prevKey,
            nextKey
        )
    }

    override fun getRefreshKey(state: PagingState<Int, TData>): Int? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
        }
    }

}


fun <T : Any> getPager(iterator: Iterator<Page<T>>, pageSize: Int = 10): Pager<Int, T> {
    return Pager(
        config = PagingConfig(
            pageSize = pageSize,
            enablePlaceholders = false
        ),
        pagingSourceFactory = { IteratorPagingSource(iterator) }
    )
}