package fr.oworld.stepbystep.ui.MyTasks

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import fr.oworld.stepbystep.datas.model.Section
import fr.oworld.stepbystep.datas.model.StatusTask
import fr.oworld.stepbystep.datas.model.SubStep
import fr.oworld.stepbystep.datas.model.Task

class MyTasksFragmentModel: ViewModel() {
    var tasks = MutableLiveData<HashMap<String, Task>>()

    fun getTasks(statusTask: StatusTask): HashMap<String, Task> {
        var returnTasks = hashMapOf<String, Task>()
        for ((_,t) in tasks.value!!) {
            if (t.status == statusTask) {
                returnTasks[t.sectionId + "_" + t.taskId] = t
            }
        }
        return returnTasks
    }
}