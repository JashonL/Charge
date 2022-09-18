package com.shuoxd.charge.ui.mine.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.shuoxd.charge.BuildConfig
import com.shuoxd.charge.R
import com.shuoxd.charge.base.BaseActivity
import com.shuoxd.charge.base.BaseViewHolder
import com.shuoxd.charge.base.OnItemClickListener
import com.shuoxd.charge.component.image.crop.CropShape
import com.shuoxd.charge.component.image.crop.ImageCrop
import com.shuoxd.charge.databinding.ActivityUserBinding
import com.shuoxd.charge.databinding.ItemChargeInMineBinding
import com.shuoxd.charge.model.charge.ChargeModel
import com.shuoxd.charge.ui.authorize.activity.AuthListActivity
import com.shuoxd.charge.ui.charge.activity.AddYourChargeActivity
import com.shuoxd.charge.ui.charge.monitor.ChargeAactivityMonitor
import com.shuoxd.charge.ui.common.fragment.RequestPermissionHub
import com.shuoxd.charge.ui.mine.viewmodel.SettingViewModel
import com.shuoxd.charge.util.AppUtil
import com.shuoxd.charge.view.dialog.AlertDialog
import com.shuoxd.charge.view.dialog.OptionsDialog
import com.shuoxd.charge.view.itemdecoration.DividerItemDecoration
import com.shuoxd.lib.util.ActivityBridge
import com.shuoxd.lib.util.ToastUtil
import com.shuoxd.lib.util.Util
import com.shuoxd.lib.util.gone
import java.io.File

class MineActivity : BaseActivity(), View.OnClickListener {

    companion object {
        fun start(context: Context?) {
            context?.startActivity(Intent(context, MineActivity::class.java))
        }
    }


    private lateinit var bind: ActivityUserBinding;

    private val viewModel: SettingViewModel by viewModels()

    private var takePictureFile: File? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityUserBinding.inflate(layoutInflater)
        setContentView(bind.root)
        initViews()
        initData()
        setlisteners()
    }

    private fun setlisteners() {
        bind.itemAddCharger.setOnClickListener(this)
        bind.ivAvatar.setOnClickListener(this)
        bind.itemAuth.setOnClickListener(this)
        bind.btLogout.setOnClickListener(this)
        bind.itemPersionalInfo.setOnClickListener(this)
        bind.itemHelpSupport.setOnClickListener(this)
        bind.itemOther.setOnClickListener(this)
    }


    private fun initViews() {
        val user = accountService().user()
        user?.let {
            bind.tvUserName.text = user.email
        }

        bind.rlvCharge.addItemDecoration(
            DividerItemDecoration(
                this,
                LinearLayoutManager.HORIZONTAL,
                resources.getColor(R.color.nocolor),
                10f
            )
        )

        bind.rlvCharge.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        bind.rlvCharge.adapter = Adapter()


    }


    override fun onClick(v: View?) {
        when {
            v === bind.btLogout -> {
                showDialog()
                viewModel.logout(accountService().user()?.email)
            }
            v === bind.itemAddCharger -> AddYourChargeActivity.start(this)

            v === bind.itemAuth -> AuthListActivity.start(this)

            v === bind.itemPersionalInfo -> PersonalInfoActivity.start(this)
            v === bind.itemHelpSupport -> HelpSupportActivity.start(this)
            v === bind.itemOther -> OhterActivity.start(this)
            v === bind.ivAvatar -> selectPictureMode()
        }
    }


    private fun selectPictureMode() {
        val takeAPicture = getString(R.string.m156_take_photo)
        val fromTheAlbum = getString(R.string.m157_select_album)
        val options =
            arrayOf(takeAPicture, fromTheAlbum)
        OptionsDialog.show(supportFragmentManager, options) {
            when (options[it]) {
                takeAPicture -> takeAPicture()
                fromTheAlbum -> fromTheAlbum()
            }
        }
    }



    /**
     * Android官方说明：Intent(MediaStore.ACTION_IMAGE_CAPTURE) 调用系统相机拍照，不需要申请Camera权限
     * 1.小米手机不申请权限会崩溃，所以都申请权限进行适配
     */
    private fun takeAPicture() {
        RequestPermissionHub.requestPermission(
            supportFragmentManager,
            arrayOf(Manifest.permission.CAMERA)
        ) { isGranted ->
            if (isGranted) {
                ActivityBridge.startActivity(
                    this,
                    Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                        takePictureFile = AppUtil.createImageFile()?.apply {
                            putExtra(
                                MediaStore.EXTRA_OUTPUT,
                                FileProvider.getUriForFile(
                                    this@MineActivity,
                                    BuildConfig.APPLICATION_ID + ".fileProvider",
                                    this
                                )
                            )
                        }
                    },
                    object :
                        ActivityBridge.OnActivityForResult {
                        override fun onActivityForResult(
                            context: Context?,
                            resultCode: Int,
                            data: Intent?
                        ) {
                            if (resultCode == RESULT_OK) {
                                takePictureFile?.also {
                                    Util.galleryAddPic(it.absolutePath)
                                    cropImage(Uri.fromFile(it))
                                }
                            }
                        }
                    })
            }
        }
    }



    private fun fromTheAlbum() {
        RequestPermissionHub.requestPermission(
            supportFragmentManager,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        ) { isGranted ->
            if (isGranted) {
                ActivityBridge.startActivity(
                    this,
                    Intent(Intent.ACTION_PICK).apply {
                        type = "image/*"
                        data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    },
                    object :
                        ActivityBridge.OnActivityForResult {
                        override fun onActivityForResult(
                            context: Context?,
                            resultCode: Int,
                            data: Intent?
                        ) {
                            if (resultCode == RESULT_OK) {
                                cropImage(data?.data)
                            }
                        }
                    })
            }
        }
    }


    private fun cropImage(imageUri: Uri?) {
        imageUri?.also {
            ImageCrop.activity(it)
                .setCropShape(CropShape.CIRCLE)
                .start(this@MineActivity)
        }
    }



    private fun initData() {
        val chargeList = getChargeList()
        if (chargeList.isEmpty())bind.llList.gone()
        (bind.rlvCharge.adapter as Adapter).refresh(chargeList)
        viewModel.logoutLiveData.observe(this) {
            dismissDialog()
            if (it == null) {
                accountService().logout()
                accountService().login(this)
                finish()
            } else {
                ToastUtil.show(it)
            }
        }


        viewModel.deleteLiveData.observe(this) {
            dismissDialog()
            ToastUtil.show(it.second)
            if (it.first) {
                (bind.rlvCharge.adapter as Adapter).deleteItem()
                //通知充电页面刷新
                ChargeAactivityMonitor.notifyPlant()
            }

        }


    }

    inner class Adapter(var chargeList: MutableList<ChargeModel> = mutableListOf()) :
        RecyclerView.Adapter<ChargeViewHolder>(), OnItemClickListener {

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ChargeViewHolder {
            return ChargeViewHolder.create(parent, this)
        }

        override fun onBindViewHolder(holder: ChargeViewHolder, position: Int) {
            holder.bindData(chargeList[position], position)
        }

        override fun getItemCount(): Int {
            return chargeList.size
        }

        @SuppressLint("NotifyDataSetChanged")
        fun refresh(serviceList: List<ChargeModel>) {
            this.chargeList.clear()
            this.chargeList.addAll(serviceList)
            notifyDataSetChanged()
        }

        override fun onItemClick(v: View?, position: Int) {
            val tag = v?.tag
            if (tag is ChargeModel) {
                openWebPage(position)
            }
        }


        override fun onItemLongClick(v: View?, position: Int) {
            val tag = v?.tag
            if (tag is ChargeModel) {
                deleteCharge(tag)
            }

        }


        private fun deleteCharge(charge: ChargeModel) {
            AlertDialog.showDialog(
                supportFragmentManager,
                getString(R.string.m155_delete_tips),
                getString(R.string.m18_confirm),
                getString(R.string.m16_cancel),
                getString(R.string.m154_delete_charge)
            ) {
                showDialog()
                viewModel.delete(charge.chargerId)
            }
        }


        private fun openWebPage(position: Int) {
            setCurrenChargeModel(chargeList[position])
            finish()
        }


        @SuppressLint("NotifyDataSetChanged")
        fun deleteItem() {
            this.chargeList.removeAt(viewModel.position)
            notifyDataSetChanged()
        }


    }


    class ChargeViewHolder(itemView: View, onItemClickListener: OnItemClickListener? = null) :
        BaseViewHolder(itemView, onItemClickListener) {

        private lateinit var binding: ItemChargeInMineBinding

        companion object {
            fun create(
                parent: ViewGroup,
                onItemClickListener: OnItemClickListener?
            ): ChargeViewHolder {
                val binding = ItemChargeInMineBinding.inflate(LayoutInflater.from(parent.context))
                val holder = ChargeViewHolder(binding.root, onItemClickListener)
                holder.binding = binding
                holder.binding.root.setOnClickListener(holder)
                holder.binding.root.setOnLongClickListener(holder)
                return holder
            }
        }


        fun bindData(chargeModel: ChargeModel, position: Int) {
            binding.tvChageName.text = chargeModel.chargerId
            binding.root.tag = chargeModel
        }


    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            //剪裁图片回调
            if (requestCode == ImageCrop.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                ImageCrop.getActivityResult(data).uri?.path?.also {
                    refreshUserProfile(it)
                }
            }
        }
    }



    private fun refreshUserProfile(path:String) {
        Glide.with(this).load(path)
            .placeholder(R.drawable.big_user)
            .into(bind.ivAvatar)
    }



}