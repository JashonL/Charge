package com.shuoxd.lib.view.statusbar

import android.animation.ValueAnimator
import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.shuoxd.lib.util.AttrUtils.getThemePrimaryDarkColor
import com.shuoxd.lib.view.statusbar.StatusBarCompat.BaseImpl
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.util.*

/**
 * After Lollipop use system method.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
internal open class StatusBarCompatLollipop : BaseImpl() {
    private val ui: UI by lazy(LazyThreadSafetyMode.NONE) {
        when {
            FlymeUI.isFlymeUI -> {
                FlymeUI()
            }
            MiUI.isMiUI -> {
                MiUI()
            }
            else -> {
                UI()
            }
        }
    }

    override fun getStatusBarHeight(context: Context): Int {
        var result = 0
        val resId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resId > 0) {
            result = context.resources.getDimensionPixelOffset(resId)
        }
        return result
    }

    /**
     * set StatusBarColor
     *
     *
     * 1. set Flags to call setStatusBarColor
     * 2. call setSystemUiVisibility to clear translucentStatusBar's Flag.
     * 3. set FitsSystemWindows to false
     */
    override fun setStatusBarColor(activity: Activity, statusColor: Int) {
        val window = activity.window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = statusColor
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
        val mContentView = window.findViewById<View>(Window.ID_ANDROID_CONTENT) as ViewGroup
        val mChildView = mContentView.getChildAt(0)
        if (mChildView != null) {
            ViewCompat.setFitsSystemWindows(mChildView, false)
            ViewCompat.requestApplyInsets(mChildView)
        }
    }

    /**
     * translucentStatusBar(full-screen)
     *
     *
     * 1. set Flags to full-screen
     * 2. set FitsSystemWindows to false
     *
     * @param hideStatusBarBackground hide statusBar's shadow
     */
    override fun translucentStatusBar(activity: Activity, hideStatusBarBackground: Boolean) {
        val window = activity.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        if (hideStatusBarBackground) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = getThemePrimaryDarkColor(activity)
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
        val mContentView = window.findViewById<View>(Window.ID_ANDROID_CONTENT) as ViewGroup
        val mChildView = mContentView.getChildAt(0)
        if (mChildView != null) {
            ViewCompat.setFitsSystemWindows(mChildView, false)
            ViewCompat.requestApplyInsets(mChildView)
        }
    }

    /**
     * compat for CollapsingToolbarLayout
     *
     *
     * 1. change to full-screen mode(like translucentStatusBar).
     * 2. set View's FitsSystemWindow to false.
     * 3. adjust toolbar's height to layout.
     * 4. cancel CollapsingToolbarLayout's WindowInsets, let it layout as normal(now setStatusBarScrimColor is useless).
     * 5. change statusBarColor by AppBarLayout's offset.
     */
    override fun setStatusBarColorForCollapsingToolbar(
        activity: Activity,
        appBarLayout: AppBarLayout,
        collapsingToolbarLayout: CollapsingToolbarLayout,
        toolbar: Toolbar,
        statusColor: Int
    ) {
        val window = activity.window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = getThemePrimaryDarkColor(activity)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
        val mContentView = window.findViewById<View>(Window.ID_ANDROID_CONTENT) as ViewGroup
        val mChildView = mContentView.getChildAt(0)
        if (mChildView != null) {
            ViewCompat.setFitsSystemWindows(mChildView, false)
            ViewCompat.requestApplyInsets(mChildView)
        }
        (appBarLayout.parent as View).fitsSystemWindows = false
        appBarLayout.fitsSystemWindows = false
        toolbar.fitsSystemWindows = true
        if (toolbar.tag == null) {
            val lp = toolbar.layoutParams as CollapsingToolbarLayout.LayoutParams
            lp.height += getStatusBarHeight(activity)
            toolbar.layoutParams = lp
            toolbar.tag = true
        }
        ViewCompat.setOnApplyWindowInsetsListener(collapsingToolbarLayout) { v, insets -> insets }
        collapsingToolbarLayout.fitsSystemWindows = false
        appBarLayout.addOnOffsetChangedListener(OnOffsetChangedListener { appBarLayout, verticalOffset ->
            if (Math.abs(verticalOffset) > appBarLayout.height - collapsingToolbarLayout.scrimVisibleHeightTrigger) {
                if (window.statusBarColor == Color.TRANSPARENT) {
                    val animator = ValueAnimator.ofArgb(Color.TRANSPARENT, statusColor)
                        .setDuration(collapsingToolbarLayout.scrimAnimationDuration)
                    animator.addUpdateListener { valueAnimator ->
                        window.statusBarColor = (valueAnimator.animatedValue as Int)
                    }
                    animator.start()
                }
            } else {
                if (window.statusBarColor == statusColor) {
                    val animator = ValueAnimator.ofArgb(statusColor, Color.TRANSPARENT)
                        .setDuration(collapsingToolbarLayout.scrimAnimationDuration)
                    animator.addUpdateListener { valueAnimator ->
                        window.statusBarColor = (valueAnimator.animatedValue as Int)
                    }
                    animator.start()
                }
            }
        })
        collapsingToolbarLayout.getChildAt(0).fitsSystemWindows = false
        collapsingToolbarLayout.setStatusBarScrimColor(statusColor)
    }

    /**
     * MIUI6+,Flyme4+ M以下系统 支持切换状态栏的文字颜色为暗色
     *
     * @param activity
     * @param light
     */
    override fun setWindowLightStatusBar(activity: Activity, light: Boolean) {
        ui.hackStatusBarMode(activity, light)
    }

    open class UI {
        /**
         * @param activity
         * @param lightMode
         * @return
         */
        open fun hackStatusBarMode(activity: Activity, lightMode: Boolean): Boolean {
            return false
        }
    }

    class MiUI : UI() {
        private var extraFlagField: Method? = null
        private var darkModeFlag = 0

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        override fun hackStatusBarMode(activity: Activity, lightMode: Boolean): Boolean {
            var result: Boolean
            try {
                if (extraFlagField == null) {
                    val clazz: Class<out Window> = activity.window.javaClass
                    extraFlagField = clazz.getMethod(
                        "setExtraFlags",
                        Int::class.javaPrimitiveType,
                        Int::class.javaPrimitiveType
                    )
                }
                if (darkModeFlag == 0) {
                    val layoutParams = Class.forName("android.view.MiuiWindowManager\$LayoutParams")
                    val darkModeField = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE")
                    darkModeFlag = darkModeField.getInt(layoutParams)
                }
                extraFlagField!!.invoke(
                    activity.window,
                    if (lightMode) darkModeFlag else 0,
                    darkModeFlag
                )
                result = true
            } catch (e: Exception) {
                result = false
            }
            return result
        }

        companion object {
            val isMiUI: Boolean
                get() {
                    var input: BufferedReader? = null
                    return try {
                        val p = Runtime.getRuntime().exec("getprop ro.miui.ui.version.name")
                        input = BufferedReader(InputStreamReader(p.inputStream), 1024)
                        !TextUtils.isEmpty(input.readLine())
                    } catch (ex: Exception) {
                        false
                    } finally {
                        if (input != null) {
                            try {
                                input.close()
                            } catch (e: Exception) {
                            }
                        }
                    }
                }
        }
    }

    class FlymeUI : UI() {
        private var darkFlag: Field? = null
        private var meizuFlags: Field? = null

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        override fun hackStatusBarMode(activity: Activity, lightMode: Boolean): Boolean {
            var result = false
            try {
                val lp = activity.window.attributes
                if (darkFlag == null) {
                    darkFlag = WindowManager.LayoutParams::class.java
                        .getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON")
                    darkFlag?.isAccessible = true
                }
                if (meizuFlags == null) {
                    meizuFlags = WindowManager.LayoutParams::class.java
                        .getDeclaredField("meizuFlags")
                    meizuFlags?.isAccessible = true
                }
                val bit = darkFlag!!.getInt(null)
                var value = meizuFlags!!.getInt(lp)
                value = if (lightMode) {
                    value or bit
                } else {
                    value and bit.inv()
                }
                meizuFlags!!.setInt(lp, value)
                activity.window.attributes = lp
                result = true
            } catch (e: Exception) {
                result = false
            }
            return result
        }

        companion object {
            val isFlymeUI: Boolean
                get() = !TextUtils.isEmpty(Build.DISPLAY) && Build.DISPLAY.lowercase(Locale.getDefault())
                    .startsWith("flyme")
        }
    }
}