package com.shuoxd.charge.ui.guide.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.shuoxd.charge.application.MainApplication
import com.shuoxd.charge.base.BaseActivity
import com.shuoxd.charge.databinding.ActivityGuideBinding
import com.shuoxd.charge.ui.guide.view.GuidePager
import com.shuoxd.charge.ui.mine.activity.LoginActivity

class GuideActivity : BaseActivity() {

    companion object {
        fun start(context: Context?) {
            val intent = Intent(context, GuideActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            context?.startActivity(intent)
        }
    }


    private lateinit var guideBinding: ActivityGuideBinding

    private lateinit var pagers: MutableList<GuidePager>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        guideBinding = ActivityGuideBinding.inflate(layoutInflater)
        setContentView(guideBinding.root)
        pagers= mutableListOf()
        for (i in 0..3) {
            val pager = GuidePager(this, null, i)
            pager.setOnclickListeners {
                //跳转到登录页面
                LoginActivity.start(this)
                MainApplication.instance().storageService().put(MainApplication.APP_FIRST,false);
                finish()
            }
            pagers.add(pager)

        }
        guideBinding.viewPager.adapter=VpAdapter(pagers)

    }


    class VpAdapter(var pagers: MutableList<GuidePager>) : PagerAdapter() {
        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val view = pagers.get(position)
            container.addView(view)
            return view
        }
        override fun getCount(): Int {
            return pagers.size
        }
        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view==`object`
        }
        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(pagers.get(position));//销毁的item
        }
    }


}