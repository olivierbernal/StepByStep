package fr.oworld.stepbystep.ui.home.section

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import fr.oworld.stepbystep.datas.model.Section
import fr.oworld.stepbystep.datas.model.Step
import fr.oworld.stepbystep.datas.model.SubStep

class SectionTaskFragmentModel: ViewModel() {
    var subStep = MutableLiveData<SubStep>()
    var section = MutableLiveData<Section>()
    var step = MutableLiveData<Step>()
}