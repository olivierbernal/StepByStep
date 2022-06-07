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

class SkillCSV(map: Map<String, String>) {
    var skillId: String = ""

    var skillName: String = ""
    var assignedTask: MutableList<String> = mutableListOf()

    init {
        this.skillId = (map["n_competence"]) ?: ""
        this.skillName = (map["competence"]) ?: ""
        this.assignedTask = (map["ref_taches"])?.split(",")?.toMutableList() ?: mutableListOf()
    }
}

class Skill(var skillId: String = "",
    var skillName: String = "",
    var assignedTask: MutableList<String> = mutableListOf()) {

    override fun equals(other: Any?): Boolean {
        if (other is Skill) {
            return this.skillId == other.skillId
        }
        return false
    }

    fun numberTask(): Int {
        return assignedTask.count()
    }

    fun numberTaskDone(): Int {
        var done = 0
        for(step in DataManager.sharedInstance.stepMap){
            for(substep in step.value.subSteps){
                for(section in substep.value.sections){
                    for(task in section.value.tasks){
                        if(assignedTask.contains(section.value.sectionId + "_" + task.value.taskId)) {
                            if (task.value.status == StatusTask.done) {
                                done += 1
                            }
                        }
                    }
                }
            }
        }
        return done
    }

    companion object {
        fun getAllSkills(activity: Activity?,
                        alreadyFail: Boolean, completion: ((res: MutableList<Skill>) -> Unit)? = null) {
            val callGet = ApiUtils.apiService.getSkills()
            callGet.enqueue(object : Callback<String> {
                override fun onResponse(
                    call: Call<String>,
                    response: Response<String>
                ) {
                    if (response.code() == 200) {
                        var skills: MutableList<Skill> = mutableListOf()
                        val csvData = response.body() ?: ""
                        val rows: List<Map<String, String>> = csvReader{
                            delimiter = ';'
                        }.readAllWithHeader(csvData)
                        for (row in rows) {
                            val skillCSV = SkillCSV(row)
                            val skill = Skill(skillCSV.skillId,
                                skillCSV.skillName,
                                skillCSV.assignedTask)
                            skills.add(skill)
                        }

                        if (completion != null) {
                            completion(skills)
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
                        getAllSkills(activity,true) {
                            completion(it)
                        }
                    }
                }
            })
        }
    }
}