package fr.oworld.stepbystep.ui.MyTasks

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import fr.oworld.stepbystep.datas.model.Section
import fr.oworld.stepbystep.datas.model.StatusTask
import fr.oworld.stepbystep.datas.model.SubStep
import fr.oworld.stepbystep.datas.model.Task

class MyContentFragmentModel: ViewModel() {
    var allContents = MutableLiveData<MutableList<String>>()
    var typeContent = MutableLiveData<TypeContent>()

    enum class TypeContent {
        video, photo, none, note, audio
    }

    fun getContent(typeContent: TypeContent): MutableList<String> {
        var returnContent = mutableListOf<String>()
        for (content in allContents.value!!) {
            if (typeContent == TypeContent.none) {
                returnContent.add(content)
            } else if (typeContent == TypeContent.video && content.contains("mov")) {
                returnContent.add(content)
            } else if (typeContent == TypeContent.photo && content.contains("img")) {
                returnContent.add(content)
            }
        }
        return returnContent
    }
}