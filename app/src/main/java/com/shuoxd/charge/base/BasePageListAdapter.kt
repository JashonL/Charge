package com.shuoxd.charge.base

import android.annotation.SuppressLint
import android.util.Log
import android.view.ViewGroup
import androidx.annotation.IntDef
import androidx.recyclerview.widget.RecyclerView
import com.shuoxd.charge.base.viewholder.ItemLoadingEndViewHolder
import com.shuoxd.charge.base.viewholder.ItemLoadingErrorViewHolder
import com.shuoxd.charge.base.viewholder.ItemLoadingViewHolder
import com.shuoxd.lib.service.http.PageModel
import com.scwang.smart.refresh.layout.SmartRefreshLayout

/**
 * RecyclerView分页Adapter
 */
abstract class BasePageListAdapter<T>(
    val dataList: MutableList<T> = mutableListOf(),
    var refreshView: SmartRefreshLayout? = null
) :
    RecyclerView.Adapter<BaseViewHolder>() {

    companion object {
        /**
         * 加载中
         */
        const val ITEM_PAGE_LOADING_ID = Int.MAX_VALUE

        /**
         * 加载失败
         */
        const val ITEM_PAGING_LOADING_ERROR_ID = Int.MAX_VALUE - 1

        /**
         * 全部加载完成
         */
        const val ITEM_PAGING_END_ID = Int.MAX_VALUE - 2
    }

    /**
     * 加载状态
     */
    @LoadStateType
    private var loadState = LoadStateType.LOADING_INIT

    /**
     * 是否正在加载中
     */
    private var isLoading = false

    /**
     * 是否正在下拉刷新中
     */
    private var isRefreshing = false

    var currentPage = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return when (viewType) {
            ITEM_PAGE_LOADING_ID -> ItemLoadingViewHolder.create(parent)
            ITEM_PAGING_LOADING_ERROR_ID -> ItemLoadingErrorViewHolder.create(parent) {
                loadNext()
                notifyItemChanged(itemCount - 1)
            }
            ITEM_PAGING_END_ID -> ItemLoadingEndViewHolder.create(parent)
            else -> onCreateItemViewHolder(parent, viewType)
        }
    }

    abstract fun onCreateItemViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        when (getItemViewType(position)) {
            ITEM_PAGE_LOADING_ID -> loadNext()
            ITEM_PAGING_LOADING_ERROR_ID -> {}
            ITEM_PAGING_END_ID -> {}
            else -> onBindItemViewHolder(holder, position)
        }
    }

    private fun loadNext() {
        if (isRefreshing || loadState == LoadStateType.LOADING_END) {
            return
        }
        isLoading = true
        loadState = LoadStateType.LOADING
        refreshView?.setEnableRefresh(false)
        onLoadNext()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setResultSuccess(pageModel: PageModel<T>) {
        this.currentPage = pageModel.pageNow
        refreshView?.setEnableRefresh(true)
        if (pageModel.pageNow == 1) {
            isRefreshing = false
            refreshView?.finishRefresh()
            dataList.clear()
            dataList.addAll(pageModel.dataList?.toList() ?: mutableListOf())
            if (dataList.size == 0) {
                showEmptyView()
            } else {
                hideEmptyView()
            }
            if (pageModel.isLastPage()) {
                loadState = LoadStateType.LOADING_END
            } else {
                loadState = LoadStateType.LOADING
            }
            notifyDataSetChanged()
        } else {
            isLoading = false
            val positionStart = dataList.size
            val count = pageModel.dataList?.size ?: 0
            dataList.addAll(pageModel.dataList?.toList() ?: mutableListOf())
            if (pageModel.isLastPage()) {
                loadState = LoadStateType.LOADING_END
                notifyItemRangeInserted(positionStart, count)
            } else {
                loadState = LoadStateType.LOADING
                notifyItemRangeInserted(positionStart, count)
            }
        }
    }

    fun setResultError() {
        if (isRefreshing) {
            isRefreshing = false
            refreshView?.finishRefresh()
        } else if (isLoading) {
            isLoading = false
            loadState = LoadStateType.LOADING_ERROR
            notifyItemChanged(itemCount - 1)
        }
    }

    protected abstract fun onLoadNext()

    fun refresh() {
        if (isLoading) {
            return
        }
        isRefreshing = true
        onRefresh()
    }

    protected abstract fun onRefresh()

    open fun showEmptyView() {}

    open fun hideEmptyView() {}

    abstract fun onBindItemViewHolder(holder: BaseViewHolder, position: Int)

    override fun getItemCount(): Int {
        if (dataList.isEmpty() || isRefreshing) {
            return 0
        }
        return 1 + dataList.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        if (position == dataList.size) {
            when (loadState) {
                LoadStateType.LOADING -> return ITEM_PAGE_LOADING_ID
                LoadStateType.LOADING_ERROR -> return ITEM_PAGING_LOADING_ERROR_ID
                LoadStateType.LOADING_END -> return ITEM_PAGING_END_ID
            }
        }
        return getItemViewTypeForItem(position)
    }

    /**
     * 是否是分页ViewHolder
     */
    fun isPagingItem(position: Int): Boolean {
        return when (getItemViewType(position)) {
            ITEM_PAGE_LOADING_ID -> true
            ITEM_PAGING_LOADING_ERROR_ID -> true
            ITEM_PAGING_END_ID -> true
            else -> false
        }
    }

    protected fun getItemViewTypeForItem(position: Int): Int {
        return 0
    }

    /**
     * 移除某个item
     */
    fun removePosition(position: Int) {
        dataList.removeAt(position)
        if (dataList.isEmpty()) {
            loadState = LoadStateType.LOADING_INIT
            notifyItemRangeRemoved(position, 2)
            showEmptyView()
        } else {
            notifyItemRemoved(position)
        }
    }

}

@IntDef(
    LoadStateType.LOADING_INIT,
    LoadStateType.LOADING,
    LoadStateType.LOADING_ERROR,
    LoadStateType.LOADING_END,
)
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.FIELD)
annotation class LoadStateType {
    companion object {
        /**
         * 初始化状态
         */
        const val LOADING_INIT = 0

        /**
         * 加载中
         */
        const val LOADING = 1

        /**
         * 加载失败
         */
        const val LOADING_ERROR = 2

        /**
         * 全部加载完成
         */
        const val LOADING_END = 3
    }
}