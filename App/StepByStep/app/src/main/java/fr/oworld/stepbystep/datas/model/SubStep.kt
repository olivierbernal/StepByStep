//
//  Created by Olivier BERNAL on 15/03/17.
//  Copyright © 2022 oworld. All rights reserved.
//
package fr.oworld.stepbystep.datas.model

import com.orhanobut.hawk.Hawk
import fr.oworld.stepbystep.R

enum class StatusSubStep {
    todo, done
}

class SubStep {
    var subStepId: String = ""
    var subStepName: String = ""
    var sections: HashMap<String, Section> = hashMapOf()

    private var _status: StatusSubStep = StatusSubStep.todo
    var status: StatusSubStep
        get() = _status
        set(newStatus) {
            this._status = newStatus
            Hawk.put(subStepId + "Done", newStatus.toString())
        }

    constructor(subStepId: String, subStepName: String, sections: HashMap<String, Section>) {
        this.subStepId = subStepId
        this.subStepName = subStepName
        this.sections = sections
        if (Hawk.contains(subStepId + "Done")) {
            val status = Hawk.get<String>(subStepId + "Done")
            this.status = StatusSubStep.valueOf(status)
        } else {
            this.status = StatusSubStep.todo
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other is SubStep) {
            return this.subStepId == other.subStepId
        }
        return false
    }

    override fun toString() : String {
        return this.subStepName
    }

    /*
    * Return percentage done task between 0 and 100
    */
    fun getPercentageDone(): Double {
        if (status == StatusSubStep.done) {
            return  100.0
        }

        var percentageSection = 0.0
        var numberSection = 0
        for((_, section) in sections){
            percentageSection += section.getPercentageDone()
            numberSection += 1
        }
        if (numberSection == 0) {
            return 100.0
        }
        return (percentageSection / numberSection)
    }

    /*
    * Return percentage color
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
}