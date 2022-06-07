//
//  Created by Olivier BERNAL on 15/03/17.
//  Copyright © 2022 oworld. All rights reserved.
//
package fr.oworld.stepbystep.datas.model

import android.app.Activity
import android.widget.Toast
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.orhanobut.hawk.Hawk
import es.dmoral.toasty.Toasty
import fr.oworld.mpp.datas.DataManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TipsCSV(map: Map<String, String>) {
    var sectionId: String = ""
    var tipsId: String = ""
    var tips: String = ""

    init {
        this.sectionId = (map["ref_rubrique"]) ?: ""
        this.tipsId = (map["n_notif"]) ?: ""
        this.tips = (map["notification"]) ?: ""
    }
}

class Tips(var sectionId: String = "",
    var tipsId: String = "",
    var tips: String = "") {

    override fun equals(other: Any?): Boolean {
        if (other is Tips) {
            return this.sectionId == other.sectionId && this.tipsId == other.tipsId
        }
        return false
    }

    companion object {
        fun getAllTips(activity: Activity?,
                        alreadyFail: Boolean, completion: ((res: MutableList<Tips>) -> Unit)? = null) {
            val callGet = ApiUtils.apiService.getNotifs()
            callGet.enqueue(object : Callback<String> {
                override fun onResponse(
                    call: Call<String>,
                    response: Response<String>
                ) {
                    if (response.code() == 200) {
                        var tipsList: MutableList<Tips> = mutableListOf()
                        val csvData = response.body() ?: ""
                        val rows: List<Map<String, String>> = csvReader{
                            delimiter = ';'
                        }.readAllWithHeader(csvData)
                        for (row in rows) {
                            val tipsCSV = TipsCSV(row)
                            val tips = Tips(tipsCSV.sectionId,
                                tipsCSV.tipsId,
                                tipsCSV.tips)
                            tipsList.add(tips)
                        }

                        if (completion != null) {
                            completion(tipsList)
                        }
                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    if(activity != null && !alreadyFail) {
                        Toasty.error(
                            activity,
                            "Pas d'acces internet.",
                            Toast.LENGTH_SHORT,
                            true
                        ).show()
                    }
                    if (completion != null) {
                        getAllTips(activity,true) {
                            completion(it)
                        }
                    }
                }
            })
        }
    }
}