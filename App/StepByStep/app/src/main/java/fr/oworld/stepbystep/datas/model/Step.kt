//
//  Created by Olivier BERNAL on 15/03/17.
//  Copyright © 2022 oworld. All rights reserved.
//
package fr.oworld.stepbystep.datas.model

import android.app.Activity
import android.graphics.Color
import android.widget.Toast
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.orhanobut.hawk.Hawk
import es.dmoral.toasty.Toasty
import fr.oworld.stepbystep.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StepCSV(map: Map<String, String>) {
    var stepId: String = ""
    var stepName: String = ""

    var subStepId: String = ""
    var subStepName: String = ""

    var sectionId: String = ""
    var sectionName: String = ""

    init {
        this.stepId = (map["n_etape"]) ?: ""
        this.stepName = (map["etape"]) ?: ""
        this.subStepId = this.stepId + "_" + (map["n_sous_etape"]) ?: ""
        this.subStepName = (map["sous_etape"]) ?: ""
        this.sectionId = this.subStepId + "_" + (map["n_rubrique"]) ?: ""
        this.sectionName = (map["rubrique"]) ?: ""
    }
}

class Step(var stepId: String, var stepName: String, subStep: HashMap<String, SubStep>) {
    var subSteps: HashMap<String, SubStep> = hashMapOf()

    var done: Boolean = false

    init {
        if (Hawk.contains(stepId + "Done")) {
            this.done = Hawk.get(stepId + "Done")
        } else {
            this.done = false
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other is Step) {
            return this.stepId == other.stepId
        }
        return false
    }

    override fun toString() : String {
        return this.stepName
    }

    /*
    * Return percentage done task between 0 and 100
    */
    fun getPercentageDone(): Double {
        var percentageSubStep = 0.0
        var numberSubStep = 0
        for((_, subStep) in subSteps){
            percentageSubStep += subStep.getPercentageDone()
            numberSubStep += 1
        }
        if (numberSubStep == 0) {
            return 100.0
        }
        return (percentageSubStep / numberSubStep)
    }

    /*
    * Return percentage bar color
    */
    fun getPercentageColor(): Int {
        val percentage = getPercentageDone()
        return if (percentage == 100.0) {
            R.color.green
        } else if (percentage != 0.0) {
            R.color.orange
        } else {
            R.color.grey
        }
    }

    /*
    * Return percentage text color
    */
    fun getPercentageTextColor(): Int {
        val percentage = getPercentageDone()
        return if (percentage == 100.0) {
            R.color.white
        } else if (percentage != 0.0) {
            R.color.black
        } else {
            R.color.black
        }
    }

    companion object {
        fun getAllSteps(activity: Activity?, alreadyFail: Boolean, completion: ((res: Map<String, Step>) -> Unit)? = null) {
            val callGet = ApiUtils.apiService.getBD()
            callGet.enqueue(object : Callback<String> {
                override fun onResponse(
                    call: Call<String>,
                    response: Response<String>
                ) {
                    val stepMap : HashMap<String, Step> = hashMapOf()

                    if (response.code() == 200) {
                        val csvData = response.body() ?: ""
                        val rows: List<Map<String, String>> = csvReader{
                            delimiter = ';'
                        }.readAllWithHeader(csvData)
                        for (row in rows) {
                            val csvStep = StepCSV(row)
                            var imported = false
                            for (aStep in stepMap) {
                                if(aStep.key == csvStep.stepId) {
                                    for (aSubStep in aStep.value.subSteps) {
                                        if(aSubStep.key == csvStep.subStepId) {
                                            for (aSection in aSubStep.value.sections) {
                                                if(aSection.key == csvStep.sectionId) {
                                                    //do nothing
                                                    imported = true
                                                }
                                            }
                                            if (!imported) {
                                                val section = Section(csvStep.sectionId,
                                                    csvStep.sectionName)
                                                aSubStep.value.sections[csvStep.sectionId] = section

                                                imported = true
                                            }
                                        }
                                    }
                                    if (!imported) {
                                        val subStep = SubStep(csvStep.subStepId,
                                            csvStep.subStepName, hashMapOf())

                                        val section = Section(csvStep.sectionId, csvStep.sectionName)
                                        subStep.sections[section.sectionId] = section

                                        aStep.value.subSteps[csvStep.subStepId] = subStep

                                        imported = true
                                    }
                                }
                            }
                            if (!imported) {
                                val step = Step(csvStep.stepId, csvStep.stepName, hashMapOf())

                                val subStep = SubStep(csvStep.subStepId, csvStep.subStepName, hashMapOf())

                                val section = Section(csvStep.sectionId, csvStep.sectionName)
                                subStep.sections[section.sectionId] = section

                                step.subSteps[subStep.subStepId] = subStep

                                stepMap[step.stepId] = step
                            }
                        }

                        Task.getAllTasks(activity, stepMap, false) {
                            if (completion != null) {
                                completion(stepMap)
                            }
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
                        getAllSteps(activity, true) {
                            completion(it)
                        }
                    }
                }
            })
        }
    }
}