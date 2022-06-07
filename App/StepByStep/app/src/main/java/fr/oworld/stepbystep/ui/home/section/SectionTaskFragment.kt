package fr.oworld.stepbystep.ui.home.section

import android.animation.Animator
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import fr.oworld.mpp.datas.inflate
import fr.oworld.stepbystep.MainActivity
import fr.oworld.stepbystep.R
import fr.oworld.stepbystep.databinding.FragmentSectionTaskBinding
import fr.oworld.stepbystep.datas.model.*
import fr.oworld.stepbystep.ui.MyTasks.MyTasksFragment
import fr.oworld.stepbystep.ui.VMOtherToolbar
import fr.oworld.stepbystep.ui.camera.CameraModel
import fr.oworld.stepbystep.ui.home.HomeFragment
import kotlinx.android.synthetic.main.fragment_section_task.*
import kotlinx.android.synthetic.main.item_my_task.view.*
import kotlinx.android.synthetic.main.item_section.view.*
import kotlinx.android.synthetic.main.item_step.view.*
import kotlinx.android.synthetic.main.item_task.view.*
import kotlinx.android.synthetic.main.item_task.view.taskChevron
import kotlinx.android.synthetic.main.item_task.view.taskDescription
import kotlinx.android.synthetic.main.item_task.view.taskMore
import kotlinx.android.synthetic.main.item_task.view.taskName
import kotlinx.android.synthetic.main.item_task.view.taskStatus
import kotlinx.android.synthetic.main.item_task.view.taskStatusTxt


class SectionTaskFragment : Fragment(), SectionRecyclerAdapter.SectionRecyclerAdapterListener,
    TaskRecyclerAdapter.TaskRecyclerAdapterListener {
    private var _binding: FragmentSectionTaskBinding? = null
    private val toolbarModel: VMOtherToolbar by activityViewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    protected val model: SectionTaskFragmentModel by viewModels(ownerProducer = {
        requireParentFragment()
    })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSectionTaskBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onStart() {
        super.onStart()

        val stepNumber = model.step.value!!.stepId.removePrefix("E")
        toolbarModel.setSpecs(stepNumber + " " + model.step.value!!.stepName,
            model.subStep.value!!.subStepName,
            true)

        val firstKey = model.subStep.value!!.sections.keys.sorted()[0]
        model.section.value = model.subStep.value!!.sections[firstKey]

        setupSection()
        setupTask()

        confettiAnim.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                Log.e("Animation:", "start")
            }

            override fun onAnimationEnd(animation: Animator) {
                confettiAnim.pauseAnimation()
                confettiAnim.visibility = View.GONE
            }

            override fun onAnimationCancel(animation: Animator) {
                Log.e("Animation:", "cancel")
            }

            override fun onAnimationRepeat(animation: Animator) {
                Log.e("Animation:", "repeat")
            }
        })

        closeMenu()
        closeBtn.setOnClickListener {
            closeMenu()
        }
        menu_add_audios.setOnClickListener {
            addAudio()
        }
        menu_add_notes.setOnClickListener {
            addNote()
        }
        menu_add_photos.setOnClickListener {
            addImage()
        }
        menu_add_videos.setOnClickListener {
            addVideo()
        }
        setHasOptionsMenu(true)

        subStepDoneBtn.setOnClickListener {
            model.subStep.value!!.status = StatusSubStep.done
            requireActivity().onBackPressed()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            android.R.id.home -> {
                getActivity()?.onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun setupSection() {
        sectionList.layoutManager = GridLayoutManager(context, 2)
        val sectionAdapter = SectionRecyclerAdapter(model.subStep.value!!, this)
        sectionList.adapter = sectionAdapter

        val spacingInPixels = 10
        sectionList.addItemDecoration(HomeFragment.SpacesItemDecoration(spacingInPixels))
    }

    fun setupTask() {
        taskList.layoutManager = LinearLayoutManager(context)
        val taskAdapter = TaskRecyclerAdapter(this)
        taskList.adapter = taskAdapter

        val spacingInPixels = 10
        taskList.addItemDecoration(HomeFragment.SpacesItemDecoration(spacingInPixels))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onSectionClicked(section: Section) {
        model.section.value = section
        sectionList.adapter!!.notifyDataSetChanged()
        taskList.adapter!!.notifyDataSetChanged()
    }

    override fun onTaskClicked(task: Task) {

    }

    fun getSelectedSection() : Section {
        return model.section.value!!
    }

    var contentTask: Task? = null
    fun showContentMenu(task: Task) {
        menu.visibility = View.VISIBLE

        contentTask = task
    }

    fun closeMenu(){
        contentTask = null
        menu.visibility = View.GONE
    }

    fun addNote(){

    }

    fun addVideo(){
        val model: CameraModel by viewModels(ownerProducer = {
            requireParentFragment()
        })
        model.isImage.value = false
        model.task.value = contentTask

        val navBuilder = NavOptions.Builder()
        val navOptions: NavOptions = navBuilder
            .build()

        findNavController().navigate(R.id.navigation_camera,
            null, navOptions)
    }

    fun addImage(){
        val model: CameraModel by viewModels(ownerProducer = {
            requireParentFragment()
        })
        model.isImage.value = true
        model.task.value = contentTask

        val navBuilder = NavOptions.Builder()
        val navOptions: NavOptions = navBuilder
            .build()

        findNavController().navigate(R.id.navigation_camera,
            null, navOptions)
    }

    fun addAudio(){

    }

    override fun playAnimation(){
        confettiAnim.visibility = View.VISIBLE
        confettiAnim.playAnimation()
    }
}

class SectionRecyclerAdapter(var subStep: SubStep, var delegate: SectionRecyclerAdapterListener) :
    RecyclerView.Adapter<SectionRecyclerAdapter.SectionHolder>() {

    override fun onBindViewHolder(holder: SectionHolder, position: Int) {
        val key = subStep.sections.keys.sorted()[position]
        val section = subStep.sections[key]
        holder.bindSection(section!!)
    }

    override fun getItemCount(): Int {
        return subStep.sections.keys.sorted().count()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SectionHolder {
        val inflatedView = parent.inflate(fr.oworld.stepbystep.R.layout.item_section, false)
        val lp = inflatedView.layoutParams
        lp.width = (parent.measuredWidth / 2.1).toInt()
        lp.height = 250
        inflatedView.setLayoutParams(lp)
        return SectionHolder(inflatedView, this)
    }

    class SectionHolder(v: View, var adapter: SectionRecyclerAdapter) : RecyclerView.ViewHolder(v),
        View.OnClickListener {
        private var view: View = v
        private var section: Section? = null

        init {
            v.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            if (adapter != null && section != null) {
                adapter.delegate.onSectionClicked(section!!)
            }
        }

        fun bindSection(section: Section) {
            this.section = section
            view.sectionName.setText(section!!.sectionName)

            if ((adapter.delegate as SectionTaskFragment).getSelectedSection().sectionId == this.section!!.sectionId) {
                view.sectionCard.setCardBackgroundColor(
                    (adapter.delegate as Fragment).resources.getColor(
                        fr.oworld.stepbystep.R.color.orange
                    )
                )
            } else {
                view.sectionCard.setCardBackgroundColor(
                    (adapter.delegate as Fragment).resources.getColor(
                        fr.oworld.stepbystep.R.color.white
                    )
                )
            }
        }
    }

    interface SectionRecyclerAdapterListener {
        fun onSectionClicked(section: Section) {
        }
    }
}

class TaskRecyclerAdapter(var delegate: TaskRecyclerAdapterListener) :
    RecyclerView.Adapter<TaskRecyclerAdapter.TaskHolder>() {

    override fun onBindViewHolder(holder: TaskHolder, position: Int) {
        val key = (delegate as SectionTaskFragment).getSelectedSection().tasks.keys.sorted()[position]
        val task = (delegate as SectionTaskFragment).getSelectedSection().tasks[key]
        holder.bindTask(task!!)
    }

    override fun getItemCount(): Int {
        return (delegate as SectionTaskFragment).getSelectedSection().tasks.keys.sorted().count()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskHolder {
        val inflatedView = parent.inflate(fr.oworld.stepbystep.R.layout.item_task, false)
        return TaskHolder(inflatedView, this)
    }

    class TaskHolder(v: View, var adapter: TaskRecyclerAdapter) : RecyclerView.ViewHolder(v),
        View.OnClickListener {
        private var view: View = v
        private var task: Task? = null

        init {
            view.taskName.setOnClickListener(this)
            view.taskChevron.setOnClickListener(this)
            view.taskStatus.setOnClickListener(this)

            configureFabMenu()

            view.unicornAnim.addAnimatorListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
                    Log.e("Animation:", "start")
                }

                override fun onAnimationEnd(animation: Animator) {
                    view.unicornAnim.pauseAnimation()
                    view.unicornAnim.visibility = View.GONE
                }

                override fun onAnimationCancel(animation: Animator) {
                    Log.e("Animation:", "cancel")
                }

                override fun onAnimationRepeat(animation: Animator) {
                    Log.e("Animation:", "repeat")
                }
            })
        }

        fun configureFabMenu(){
            // main menu
            view.taskMore.setOnClickListener {
                (adapter.delegate as SectionTaskFragment).showContentMenu(task!!)
            }
        }

        override fun onClick(p0: View?) {
            if(p0 == view.taskName || p0 == view.taskChevron) {
                if(view.taskDescription.visibility == View.GONE) {
                    view.taskDescription.visibility = View.VISIBLE
                    view.taskChevron.rotation = 180f
                } else {
                    view.taskChevron.rotation = 0f
                    view.taskDescription.visibility = View.GONE
                }
            } else if(p0 == view.taskStatus) {
                if(task!!.status == StatusTask.void) {
                    task!!.status = StatusTask.todo
                } else if(task!!.status == StatusTask.todo) {
                    task!!.status = StatusTask.done
                    view.unicornAnim.visibility = View.VISIBLE
                    view.unicornAnim.playAnimation()

                    adapter.delegate.playAnimation()
                } else {
                    task!!.status = StatusTask.void
                }
                updateTaskImage()
            } else {
                if (adapter != null && task != null) {

                }
            }
        }

        fun bindTask(task: Task) {
            this.task = task
            view.taskName.setText(task!!.taskName)
            view.taskDescription.setText(task!!.description)
            view.taskDescription.visibility = View.GONE
            updateTaskImage()
        }

        fun updateTaskImage() {
            if(task!!.status == StatusTask.void) {
                view.taskStatus.setImageDrawable(
                    ContextCompat.getDrawable((adapter.delegate as SectionTaskFragment).requireContext(),
                        fr.oworld.stepbystep.R.drawable.ic_task_void))
                view.taskStatusTxt.text = (adapter.delegate as SectionTaskFragment).getString(R.string.tache_liste)
            } else if(task!!.status == StatusTask.todo) {
                view.taskStatus.setImageDrawable(
                    ContextCompat.getDrawable((adapter.delegate as SectionTaskFragment).requireContext(),
                        fr.oworld.stepbystep.R.drawable.ic_task_todo))
                view.taskStatusTxt.text = (adapter.delegate as SectionTaskFragment).getString(R.string.tache_a_faire)
            } else {
                view.taskStatus.setImageDrawable(
                    ContextCompat.getDrawable((adapter.delegate as SectionTaskFragment).requireContext(),
                        fr.oworld.stepbystep.R.drawable.ic_checked))
                view.taskStatusTxt.text = (adapter.delegate as SectionTaskFragment).getString(R.string.tache_fait)
            }
        }
    }

    interface TaskRecyclerAdapterListener {
        fun onTaskClicked(task: Task) {
        }
        fun playAnimation(){
        }
    }
}
