package com.shuoxd.lib.service.http

/**
 * 分页数据结构
 */
data class PageModel<T>(
    val pageNow: Int,//当前页数，从1开始
    val pageSize: Int,//每页条数
    val totalNum: Int,//总条数
    val pageTotal: Int,//总页数
    val dataList: Array<T>?//数据列表
) {

    /**
     * 是否最后一页
     */
    fun isLastPage(): Boolean {
        return pageNow == pageTotal
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PageModel<*>

        if (pageNow != other.pageNow) return false
        if (pageSize != other.pageSize) return false
        if (totalNum != other.totalNum) return false
        if (pageTotal != other.pageTotal) return false
        if (!dataList.contentEquals(other.dataList)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = pageNow
        result = 31 * result + pageSize
        result = 31 * result + totalNum
        result = 31 * result + pageTotal
        result = 31 * result + dataList.contentHashCode()
        return result
    }
}