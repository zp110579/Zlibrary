package com.lzf.easyfloat.interfaces

import android.content.Context

/**
 * @author: liuzhenfeng
 * @function: 通过接口获取屏幕的有效显示高度
 * @date: 2020-02-16  16:21
 */
interface OnDisplayHeight {
    /**
     * 获取屏幕有效的显示高度，不包含虚拟导航栏
     *
     * @param context ApplicationContext
     * @return 高度值（int类型）
     */
    fun getDisplayRealHeight(context: Context): Int
}