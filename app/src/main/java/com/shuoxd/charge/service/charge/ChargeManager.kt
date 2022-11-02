package com.shuoxd.charge.service.charge

import com.shuoxd.charge.application.MainApplication
import com.shuoxd.charge.model.charge.ChargeModel
import com.shuoxd.lib.service.storage.DefaultStorageService

class ChargeManager private constructor() {

    private var chargeList = mutableListOf<ChargeModel>()
    private var chargeModel: ChargeModel? = null

    companion object {
        fun getInstance(): ChargeManager {
            return Holder.instance
        }
    }

    private object Holder {
        val instance = ChargeManager()
    }


    fun setChargeList(list: MutableList<ChargeModel>) {
        this.chargeList = list
    }


    fun getChargeList() = chargeList


    fun setCurrentCharge(chargeModel: ChargeModel?) {
        MainApplication.instance().storageService().put(DefaultStorageService.CURRENT_CHARGE, chargeModel?.chargerId)
        this.chargeModel = chargeModel
    }

    fun getCurrentCharge() = chargeModel

    interface ServiceInterface {

        fun setChargeList(list: MutableList<ChargeModel>)

        fun getChargeList(): List<ChargeModel>

        fun setCurrenChargeModel(chargeModel: ChargeModel)

        fun getCurrentChargeModel(): ChargeModel?

    }

}