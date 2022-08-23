package com.shuoxd.charge.ui.common.activity

import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.amap.api.maps.AMap
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.model.CameraPosition
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.MyLocationStyle
import com.shuoxd.charge.R
import com.shuoxd.charge.base.BaseActivity
import com.shuoxd.charge.databinding.ActivityAmapBinding
import com.shuoxd.charge.ui.common.viewmodel.AMapViewModel


/**
 * 高德地图页面
 */
class AMapActivity : BaseActivity(), View.OnClickListener, AMap.OnMyLocationChangeListener,
    AMap.OnCameraChangeListener {

    companion object {

        /**
         * 缩放等级
         */
        private const val ZOOM_LEVEL = 15f
        const val KEY_SELECT_ADDRESS = "key_select_address"

        fun start(context: Context?) {
            context?.startActivity(getIntent(context))
        }

        fun getIntent(context: Context?): Intent {
            return Intent(context, AMapActivity::class.java)
        }

    }

    private lateinit var binding: ActivityAmapBinding
    private lateinit var map: AMap
    private val viewModel: AMapViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAmapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.mapView.onCreate(savedInstanceState)
        initData()
        initView()
        setListener()
    }

    private fun initData() {
        viewModel.regeocodeSearchedLiveData.observe(this) {
            if (it.second == null) {
                val locationInfo = it.first
                binding.tvLocationAddress.text = locationInfo?.address
                binding.tvLocationCity.text = "${locationInfo?.city}(${
                    getString(
                        R.string.longitude_format,
                        locationInfo?.longitudeStr()
                    )
                },${getString(R.string.latitude_format, locationInfo?.latitudeStr())})"
            }
        }
    }

    private fun setListener() {
        binding.ivMoveCenter.setOnClickListener(this)
        binding.title.setOnRightTextClickListener {
            if (viewModel.regeocodeSearchedLiveData.value?.second == null) {
                setResult(RESULT_OK, Intent().also {
                    it.putExtra(
                        KEY_SELECT_ADDRESS,
                        viewModel.regeocodeSearchedLiveData.value?.first
                    )
                })
                finish()
            }
        }
    }

    private fun initView() {
        map = binding.mapView.map.also {
            it.myLocationStyle = MyLocationStyle().apply {
                //定位一次，且将视角移动到地图中心点
                myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE)
            }
            //设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
            it.isMyLocationEnabled = true
            it.setOnMyLocationChangeListener(this)
            it.setOnCameraChangeListener(this)
            it.moveCamera(CameraUpdateFactory.zoomTo(ZOOM_LEVEL))
        }
        //设置缩放按钮不显示
        map.uiSettings.isZoomControlsEnabled = false
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.mapView.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapView.onSaveInstanceState(outState)
    }

    override fun onClick(v: View?) {
        when {
            v === binding.ivMoveCenter -> moveToCenter()
        }
    }

    private fun moveToCenter() {
        val myLocation = map.myLocation
        map.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(
                    myLocation.latitude,
                    myLocation.longitude
                ), ZOOM_LEVEL
            )
        )
    }

    override fun onMyLocationChange(location: Location?) {
        location?.let {
            viewModel.fetchAddressFromLocation(it.latitude, it.longitude)
        }
    }

    override fun onCameraChange(position: CameraPosition?) {

    }

    override fun onCameraChangeFinish(position: CameraPosition?) {
        position?.target?.also {
            viewModel.fetchAddressFromLocation(it.latitude, it.longitude)
        }
    }
}