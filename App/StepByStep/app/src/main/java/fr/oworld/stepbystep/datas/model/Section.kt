//
//  Created by Olivier BERNAL on 15/03/17.
//  Copyright © 2022 oworld. All rights reserved.
//
package fr.oworld.stepbystep.datas.model

import com.orhanobut.hawk.Hawk

class Section(var sectionId: String = "",
    var sectionName: String = "",
    var tasks: HashMap<String, Task> = hashMapOf()) {

    override fun equals(other: Any?): Boolean {
        if (other is Section) {
            return this.sectionId == other.sectionId
        }
        return false
    }

    override fun toString() : String {
        return this.sectionName
    }

    /*
    * Return percentage done task between 0 and 100
    */
    fun getPercentageDone(): Double {
        var taskDone = 0.0
        var taskNumber = tasks.count().toDouble()
        for((_, task) in tasks){
            if (task.status == StatusTask.done) {
                taskDone++
            }

        }
        if (taskNumber == 0.0 && tasks.count() == 0) {
            return 100.0
        } else if (taskNumber == 0.0) {
            return  0.0
        }
        return (taskDone / taskNumber) * 100.0
    }
}