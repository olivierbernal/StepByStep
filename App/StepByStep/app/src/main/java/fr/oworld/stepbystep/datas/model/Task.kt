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

class TaskCSV(map: Map<String, String>) {
    var sectionId: String = ""
    var taskId: String = ""

    var taskName: String = ""
    var description: String = ""

    init {
        this.sectionId = (map["ref_rubrique"]) ?: ""
        this.taskId = (map["n_tache"]) ?: ""
        this.taskName = (map["tache"]) ?: ""
        this.description = (map["description"]) ?: ""
    }
}

enum class StatusTask {
    done, todo, void
}

enum class ContentPrefix {
    img, mov, mov_snap, mp3, txt
}

class Task(var sectionId: String = "",
    var taskId: String = "",
    var taskName: String = "",
    var description: String = "") {

    private var _status: StatusTask = StatusTask.void
    var contentList: MutableList<String> = mutableListOf()

    var status: StatusTask
        get() = _status
        set(newStatus) {
            this._status = newStatus
            Hawk.put(sectionId + "_" + taskId + "Done", newStatus.toString())
        }

    init {
        if (Hawk.contains(sectionId + "_" + taskId + "Done")) {
            val status = Hawk.get<String>(sectionId + "_" + taskId + "Done")
            this.status = StatusTask.valueOf(status)
        } else {
            this.status = StatusTask.void
        }
        loadContent()
    }

    fun loadContent() {
        if(Hawk.contains(sectionId + "_" + taskId + "Content")) {
            this.contentList = Hawk.get(sectionId + "_" + taskId + "Content")
        } else {
            contentList = mutableListOf()
        }
    }

    fun addContent(path: String) {
        this.contentList.add(path)
        Hawk.put(sectionId + "_" + taskId + "Content", contentList)
    }

    override fun equals(other: Any?): Boolean {
        if (other is Task) {
            return this.taskId == other.taskId && this.sectionId == other.sectionId
        }
        return false
    }

    fun getStep(): Step? {
        val ids = sectionId.split("_")
        for((_, step) in DataManager.sharedInstance.stepMap){
            if (step.stepId == ids[0]){
                return step
            }
        }
        return null
    }

    fun getSubStep(): SubStep? {
        val ids = sectionId.split("_")
        val step = getStep()!!
        for((_, subStep) in step.subSteps){
            if (subStep.subStepId == ids[0] + "_" + ids[1]){
                return subStep
            }
        }
        return null
    }

    companion object {
        fun getAllTasks(activity: Activity?,
                        steps: HashMap<String, Step>,
                        alreadyFail: Boolean, completion: ((res: Map<String, Step>) -> Unit)? = null) {
            val callGet = ApiUtils.apiService.getTasks()
            callGet.enqueue(object : Callback<String> {
                override fun onResponse(
                    call: Call<String>,
                    response: Response<String>
                ) {
                    if (response.code() == 200) {
                        val csvData = response.body() ?: ""
                        val rows: List<Map<String, String>> = csvReader{
                            delimiter = ';'
                        }.readAllWithHeader(csvData)
                        for (row in rows) {
                            val taskCSV = TaskCSV(row)
                            for(aStep in steps) {
                                for (aSubStep in aStep.value.subSteps) {
                                    for(aSection in aSubStep.value.sections){
                                        if (aSection.value.sectionId == taskCSV.sectionId) {
                                            aSection.value.tasks[taskCSV.taskId] = Task(taskCSV.sectionId,
                                            taskCSV.taskId,
                                            taskCSV.taskName,
                                            taskCSV.description)
                                        }
                                    }
                                }
                            }
                        }

                        if (completion != null) {
                            completion(steps)
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
                        getAllTasks(activity, steps, true) {
                            completion(it)
                        }
                    }
                }
            })
        }
    }
}