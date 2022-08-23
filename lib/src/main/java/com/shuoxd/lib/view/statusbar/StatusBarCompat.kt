package com.shuoxd.lib.view.statusbar

import android.app.Activity
import android.content.Context
import android.os.Build
import androidx.annotation.ColorInt
import androidx.appcompat.widget.Toolbar
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout

/**
 * android:windowIsTranslucent
 * xml文件中的这个属性需要删除掉,是通过代码来设置沉浸式样式，如果在xml中也设置了，会导致冲突失效
 */
object StatusBarCompat {
    private val IMPL: BaseImpl by lazy(LazyThreadSafetyMode.NONE) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarCompatM()
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            StatusBarCompatLollipop()
        } else {
            BaseImpl()
        }
    }

    /**
     * set statusBarColor
     *
     * @param statusColor color
     * @param alpha       0 - 255
     */
    fun setStatusBarColor(activity: Activity, @ColorInt statusColor: Int, alpha: Int) {
        setStatusBarColor(activity, calculateStatusBarColor(statusColor, alpha))
    }

    fun setStatusBarColor(activity: Activity, @ColorInt statusColor: Int) {
        IMPL.setStatusBarColor(activity, statusColor)
    }

    fun translucentStatusBar(activity: Activity) {
        translucentStatusBar(activity, false)
    }

    fun setWindowLightStatusBar(activity: Activity, light: Boolean) {
        IMPL.setWindowLightStatusBar(activity, light)
    }

    /**
     * change to full screen mode
     *
     * @param hideStatusBarBackground hide status bar alpha Background when SDK > 21, true if hide it
     */
    fun translucentStatusBar(activity: Activity, hideStatusBarBackground: Boolean) {
        IMPL.translucentStatusBar(activity, hideStatusBarBackground)
    }

    fun setStatusBarColorForCollapsingToolbar(
        activity: Activity,
        appBarLayout: AppBarLayout,
        collapsingToolbarLayout: CollapsingToolbarLayout,
        toolbar: Toolbar,
        @ColorInt statusColor: Int
    ) {
        IMPL.setStatusBarColorForCollapsingToolbar(
            activity,
            appBarLayout,
            collapsingToolbarLayout,
            toolbar,
            statusColor
        )
    }

    //Get alpha color
    @ColorInt
    fun calculateStatusBarColor(color: Int, alpha: Int): Int {
        val a = 1 - alpha / 255f
        var red = color shr 16 and 0xff
        var green = color shr 8 and 0xff
        var blue = color and 0xff
        red = (red * a + 0.5).toInt()
        green = (green * a + 0.5).toInt()
        blue = (blue * a + 0.5).toInt()
        return 0xff shl 24 or (red shl 16) or (green shl 8) or blue
    }

    /**
     * return statusBar's Height in pixels
     */
    fun getStatusBarHeight(context: Context): Int {
        return IMPL.getStatusBarHeight(context)
    }

    open class BaseImpl {
        open fun getStatusBarHeight(context: Context): Int {
            return 0
        }

        open fun setStatusBarColor(activity: Activity, @ColorInt statusColor: Int) {}

        /**
         * 设置状态栏字体颜色，调用这个方法后
         * @param light true-黑色，false-白色
         */
        open fun setWindowLightStatusBar(activity: Activity, light: Boolean) {}

        /**
         * 设置透明状态栏,这时候为全屏模式，内容会显示到状态栏上面，这时候状态栏上面的内容还是展示出来的
         * @param hideStatusBarBackground true-隐藏状态栏背景，false-显示状态栏背景
         */
        open fun translucentStatusBar(activity: Activity, hideStatusBarBackground: Boolean) {}
        open fun setStatusBarColorForCollapsingToolbar(
            activity: Activity,
            appBarLayout: AppBarLayout,
            collapsingToolbarLayout: CollapsingToolbarLayout,
            toolbar: Toolbar,
            @ColorInt statusColor: Int
        ) {
        }
    }
}