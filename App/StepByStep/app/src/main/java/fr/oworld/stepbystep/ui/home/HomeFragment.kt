package fr.oworld.stepbystep.ui.home

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.*
import fr.oworld.mpp.datas.DataManager
import fr.oworld.stepbystep.databinding.FragmentHomeBinding
import fr.oworld.stepbystep.datas.model.Step
import fr.oworld.stepbystep.datas.model.SubStep
import fr.oworld.stepbystep.ui.home.section.SectionTaskFragmentModel
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment(), StepRecyclerAdapter.StepRecyclerAdapterListener,
    SubStepRecyclerAdapter.SubStepRecyclerAdapterListener {
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    protected val model: HomeFragmentModel by viewModels(ownerProducer = {
        requireParentFragment()
    })

    class HomeFragmentModel: ViewModel() {
        var currentStep = MutableLiveData<Step>()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onStart() {
        super.onStart()
        if(DataManager.sharedInstance.stepMap.count() == 0){
            DataManager.sharedInstance.initDatas(requireActivity()) {
                setupStep()
            }
        } else {
            setupStep()
        }
    }

    fun setupStep() {
        stepList.layoutManager = GridLayoutManager(context, 2)
        val stepAdapter = StepRecyclerAdapter(this)
        stepList.adapter = stepAdapter

        val spacingInPixels = 30
        stepList.addItemDecoration(SpacesItemDecoration(spacingInPixels))

        guideline.setGuidelinePercent(1f)
        subStepList.visibility = GONE

        var currentStep: Step? = null
        for ((_,step) in DataManager.sharedInstance.stepMap) {
            if (step.getPercentageDone() != 100.0) {
                currentStep = step
                break
            }
        }
        if (currentStep == null && DataManager.sharedInstance.stepMap.count() > 0) {
            currentStep= DataManager.sharedInstance.stepMap[
                    DataManager.sharedInstance.stepMap.keys.sorted()[0]]
        }
        onStepClicked(currentStep!!)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onStepClicked(step: Step) {
        stepList.adapter?.notifyDataSetChanged()
        setupSubStep(step)
        guideline.setGuidelinePercent(0.6f)
        subStepList.visibility = VISIBLE
        model.currentStep.value = step
    }

    fun setupSubStep(step: Step) {
        subStepList.layoutManager = LinearLayoutManager(context)
        val subStepAdapter = SubStepRecyclerAdapter(step, this)
        subStepList.adapter = subStepAdapter
    }

    override fun onSubStepClicked(subStep: SubStep) {
        val modelSectionTask: SectionTaskFragmentModel by viewModels(ownerProducer = {
            requireParentFragment()
        })
        modelSectionTask.subStep.value = subStep
        modelSectionTask.step.value = model.currentStep.value

        val navBuilder = NavOptions.Builder()
        val navOptions: NavOptions = navBuilder
            .build()
        findNavController().navigate(fr.oworld.stepbystep.R.id.navigation_section_task, null, navOptions)
    }

    class SpacesItemDecoration(private val space: Int) : ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect, view: View,
            parent: RecyclerView, state: RecyclerView.State
        ) {
            outRect.left = space
            outRect.right = space
            outRect.bottom = space

            // Add top margin only for the first item to avoid double space between items
            if (parent.getChildLayoutPosition(view) == 0 || parent.getChildLayoutPosition(view) == 1) {
                outRect.top = 0
            } else {
                outRect.top = 0
            }
        }
    }
}
