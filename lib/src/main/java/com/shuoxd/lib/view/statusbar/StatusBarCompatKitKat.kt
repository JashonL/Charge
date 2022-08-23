package com.shuoxd.lib.view.statusbar

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.os.Build
import android.view.*
import android.widget.FrameLayout
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.shuoxd.lib.view.statusbar.StatusBarCompat.BaseImpl
import kotlin.math.abs

/**
 * After kitkat add fake status bar
 */
@TargetApi(Build.VERSION_CODES.KITKAT)
internal class StatusBarCompatKitKat : BaseImpl() {
    /**
     * set StatusBarColor
     *
     *
     * 1. set Window Flag : WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
     * 2. removeFakeStatusBarViewIfExist
     * 3. addFakeStatusBarView
     * 4. addMarginTopToContentChild
     * 5. cancel ContentChild's fitsSystemWindow
     */
    override fun setStatusBarColor(activity: Activity, statusColor: Int) {
        val window = activity.window
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        val mContentView = window.findViewById<View>(Window.ID_ANDROID_CONTENT) as ViewGroup
        val mContentChild = mContentView.getChildAt(0)
        val statusBarHeight = getStatusBarHeight(activity)
        removeFakeStatusBarViewIfExist(activity)
        addFakeStatusBarView(activity, statusColor, statusBarHeight)
        addMarginTopToContentChild(mContentChild, statusBarHeight)
        if (mContentChild != null) {
            ViewCompat.setFitsSystemWindows(mContentChild, false)
        }
    }

    /**
     * translucentStatusBar
     *
     *
     * 1. set Window Flag : WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
     * 2. removeFakeStatusBarViewIfExist
     * 3. removeMarginTopOfContentChild
     * 4. cancel ContentChild's fitsSystemWindow
     */
    override fun translucentStatusBar(activity: Activity, hideStatusBarBackground: Boolean) {
        val window = activity.window
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        val mContentView = activity.findViewById<View>(Window.ID_ANDROID_CONTENT) as ViewGroup
        val mContentChild = mContentView.getChildAt(0)
        removeFakeStatusBarViewIfExist(activity)
        removeMarginTopOfContentChild(mContentChild, getStatusBarHeight(activity))
        if (mContentChild != null) {
            ViewCompat.setFitsSystemWindows(mContentChild, false)
        }
    }

    /**
     * compat for CollapsingToolbarLayout
     *
     *
     * 1. set Window Flag : WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
     * 2. set FitsSystemWindows for views.
     * 3. removeFakeStatusBarViewIfExist
     * 4. removeMarginTopOfContentChild
     * 5. add OnOffsetChangedListener to change statusBarView's alpha
     */
    override fun setStatusBarColorForCollapsingToolbar(
        activity: Activity,
        appBarLayout: AppBarLayout,
        collapsingToolbarLayout: CollapsingToolbarLayout,
        toolbar: Toolbar,
        statusColor: Int
    ) {
        val window = activity.window
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        val mContentView = window.findViewById<View>(Window.ID_ANDROID_CONTENT) as ViewGroup
        val mContentChild = mContentView.getChildAt(0)
        mContentChild.fitsSystemWindows = false
        (appBarLayout.parent as View).fitsSystemWindows = false
        appBarLayout.fitsSystemWindows = false
        collapsingToolbarLayout.fitsSystemWindows = false
        collapsingToolbarLayout.getChildAt(0).fitsSystemWindows = false
        toolbar.fitsSystemWindows = true
        if (toolbar.tag == null) {
            val lp = toolbar.layoutParams as CollapsingToolbarLayout.LayoutParams
            lp.height += getStatusBarHeight(activity)
            toolbar.layoutParams = lp
            toolbar.tag = true
        }
        val statusBarHeight = getStatusBarHeight(activity)
        removeFakeStatusBarViewIfExist(activity)
        removeMarginTopOfContentChild(mContentChild, statusBarHeight)
        val statusView = addFakeStatusBarView(activity, statusColor, statusBarHeight)
        appBarLayout.addOnOffsetChangedListener(OnOffsetChangedListener { appBarLayout, verticalOffset ->
            if (abs(verticalOffset) > appBarLayout.height - collapsingToolbarLayout.scrimVisibleHeightTrigger) {
                if (statusView.alpha == 0f) {
                    statusView.animate().cancel()
                    statusView.animate().alpha(1f)
                        .setDuration(collapsingToolbarLayout.scrimAnimationDuration).start()
                }
            } else {
                if (statusView.alpha == 1f) {
                    statusView.animate().cancel()
                    statusView.animate().alpha(0f)
                        .setDuration(collapsingToolbarLayout.scrimAnimationDuration).start()
                }
            }
        })
    }

    override fun getStatusBarHeight(context: Context): Int {
        var result = 0
        val resId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resId > 0) {
            result = context.resources.getDimensionPixelOffset(resId)
        }
        return result
    }

    companion object {
        private const val TAG_FAKE_STATUS_BAR_VIEW = "statusBarView"
        private const val TAG_MARGIN_ADDED = "marginAdded"

        /**
         * 1. Add fake statusBarView.
         * 2. set tag to statusBarView.
         */
        private fun addFakeStatusBarView(
            activity: Activity,
            statusBarColor: Int,
            statusBarHeight: Int
        ): View {
            val window = activity.window
            val mDecorView = window.decorView as ViewGroup
            val mStatusBarView = View(activity)
            val layoutParams =
                FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, statusBarHeight)
            layoutParams.gravity = Gravity.TOP
            mStatusBarView.layoutParams = layoutParams
            mStatusBarView.setBackgroundColor(statusBarColor)
            mStatusBarView.tag = TAG_FAKE_STATUS_BAR_VIEW
            mDecorView.addView(mStatusBarView)
            return mStatusBarView
        }

        /**
         * use reserved order to remove is more quickly.
         */
        private fun removeFakeStatusBarViewIfExist(activity: Activity) {
            val window = activity.window
            val mDecorView = window.decorView as ViewGroup
            val fakeView = mDecorView.findViewWithTag<View>(TAG_FAKE_STATUS_BAR_VIEW)
            if (fakeView != null) {
                mDecorView.removeView(fakeView)
            }
        }

        /**
         * add marginTop to simulate set FitsSystemWindow true
         */
        private fun addMarginTopToContentChild(mContentChild: View?, statusBarHeight: Int) {
            if (mContentChild == null) {
                return
            }
            if (TAG_MARGIN_ADDED != mContentChild.tag) {
                val lp = mContentChild.layoutParams as FrameLayout.LayoutParams
                lp.topMargin += statusBarHeight
                mContentChild.layoutParams = lp
                mContentChild.tag = TAG_MARGIN_ADDED
            }
        }

        /**
         * remove marginTop to simulate set FitsSystemWindow false
         */
        private fun removeMarginTopOfContentChild(mContentChild: View?, statusBarHeight: Int) {
            if (mContentChild == null) {
                return
            }
            if (TAG_MARGIN_ADDED == mContentChild.tag) {
                val lp = mContentChild.layoutParams as FrameLayout.LayoutParams
                lp.topMargin -= statusBarHeight
                mContentChild.layoutParams = lp
                mContentChild.tag = null
            }
        }
    }
}