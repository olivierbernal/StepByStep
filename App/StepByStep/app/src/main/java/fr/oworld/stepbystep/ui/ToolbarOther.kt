package fr.oworld.stepbystep.ui

import android.R
import android.app.Application
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import fr.oworld.stepbystep.databinding.FragmentToolbarOtherBinding

class ToolbarOther : Fragment() {
    private lateinit var binding: FragmentToolbarOtherBinding
    private val model: VMOtherToolbar by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentToolbarOtherBinding.inflate(inflater, container, false)
        binding.model = model
        binding.lifecycleOwner = viewLifecycleOwner
        binding.view = this
        val root: View = binding!!.root

        return root
    }
}

class VMOtherToolbar(application: Application) : AndroidViewModel(application) {
    private val toolbarTitle = MutableLiveData<CharSequence>("")
    private val toolbarSubTitle = MutableLiveData<CharSequence>("")
    private val toolbarBackVisibility = MutableLiveData(false)
    private val toolbarSubTitleVisibility = MutableLiveData(false)
    private val toolbarSubTitleAloneVisibility = MutableLiveData(false)

    private val toolbarTitleVisibility = MutableLiveData(false)

    fun getTitle(): LiveData<CharSequence> = toolbarTitle
    fun getSubTitle(): LiveData<CharSequence> = toolbarSubTitle

    fun isBackVisible(): LiveData<Boolean> = toolbarSubTitleVisibility
    fun isSubtitleVisible(): LiveData<Boolean> = toolbarSubTitleVisibility
    fun isSubtitleAloneVisible(): LiveData<Boolean> = toolbarSubTitleAloneVisibility
    fun isTitleVisible(): LiveData<Boolean> = toolbarTitleVisibility

    fun setSpecs(
        title: CharSequence = "",
        subTitle: CharSequence = "",
        backVisibility: Boolean = false
    ) {
        toolbarTitle.value = title
        toolbarSubTitle.value = subTitle
        toolbarBackVisibility.value = backVisibility

        toolbarSubTitleVisibility.value = subTitle.isNotEmpty() && title.isNotEmpty()
        toolbarTitleVisibility.value = title.isNotEmpty()
        toolbarSubTitleAloneVisibility.value = !toolbarSubTitleVisibility.value!!
    }
}
