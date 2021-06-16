package com.zhanghaochen.smalldemos.demos.recyclerstudy.layoutmanager

import androidx.recyclerview.widget.RecyclerView

/**
 * @author created by zhanghaochen
 * @date 2020-08-24 14:35
 * 描述：自定义LinearLayoutManager
 */
class MyLinearLayoutManager : RecyclerView.LayoutManager() {

    /**
     * RecyclerView 子 item 的 LayoutParameters，
     * 若是想修改子Item的布局参数（比如：宽/高/margin/padding等等），那么可以在该方法内进行设置。
     */
    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT,
                RecyclerView.LayoutParams.WRAP_CONTENT)
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) {
        try {

        } catch (e: Exception) {

        }
    }
}