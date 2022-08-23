package com.growatt.lib.service.http

/**
 * 分页数据结构
 */
data class PageModel<T>(
    val currentPage: Int,//当前页数，从1开始
    val pageSize: Int,//每页条数
    val totalCount: Int,//总条数
    val pageCount: Int,//总页数
    val list: Array<T>?//数据列表
) {

    /**
     * 是否最后一页
     */
    fun isLastPage(): Boolean {
        return currentPage == pageCount
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PageModel<*>

        if (currentPage != other.currentPage) return false
        if (pageSize != other.pageSize) return false
        if (totalCount != other.totalCount) return false
        if (pageCount != other.pageCount) return false
        if (!list.contentEquals(other.list)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = currentPage
        result = 31 * result + pageSize
        result = 31 * result + totalCount
        result = 31 * result + pageCount
        result = 31 * result + list.contentHashCode()
        return result
    }
}