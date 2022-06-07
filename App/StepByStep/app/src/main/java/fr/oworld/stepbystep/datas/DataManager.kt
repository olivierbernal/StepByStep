package fr.oworld.mpp.datas

import android.app.Activity
import fr.oworld.stepbystep.datas.model.Skill
import fr.oworld.stepbystep.datas.model.Step
import fr.oworld.stepbystep.datas.model.Tips

interface OnChangeData {
    fun onDataChange()
}

class DataManager {
    companion object {
        val sharedInstance = DataManager()
    }

    var TAG = "DataManager"

    var initOver:Boolean = false

    var stepMap: Map<String,Step> = hashMapOf()
    var skillList: MutableList<Skill> = mutableListOf()
    var tipsList: MutableList<Tips> = mutableListOf()

    fun initDatas(c: Activity, completion: ((res: Boolean) -> Unit)? = null) {
        Step.getAllSteps(c, false) {
            stepMap = it
            initOver = true
            if (completion != null) {
                completion(true)
            }
        }

        Skill.getAllSkills(c, false) {
            skillList = it
        }

        Tips.getAllTips(c, false) {
            tipsList = it
        }
    }
}
