package fr.oworld.stepbystep.ui.MyTasks

import android.animation.Animator
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import fr.oworld.mpp.datas.DataManager
import fr.oworld.mpp.datas.inflate
import fr.oworld.stepbystep.R
import fr.oworld.stepbystep.databinding.FragmentMyTasksBinding
import fr.oworld.stepbystep.datas.model.StatusTask
import fr.oworld.stepbystep.datas.model.Task
import fr.oworld.stepbystep.ui.ToolbarOther
import fr.oworld.stepbystep.ui.VMOtherToolbar
import fr.oworld.stepbystep.ui.camera.CameraModel
import fr.oworld.stepbystep.ui.home.HomeFragment
import kotlinx.android.synthetic.main.fragment_my_tasks.*
import kotlinx.android.synthetic.main.fragment_my_tasks.closeBtn
import kotlinx.android.synthetic.main.fragment_my_tasks.menu
import kotlinx.android.synthetic.main.fragment_my_tasks.menu_add_audios
import kotlinx.android.synthetic.main.fragment_my_tasks.menu_add_notes
import kotlinx.android.synthetic.main.fragment_my_tasks.menu_add_photos
import kotlinx.android.synthetic.main.fragment_my_tasks.menu_add_videos
import kotlinx.android.synthetic.main.item_my_task.view.*
import kotlinx.android.synthetic.main.item_my_task.view.taskChevron
import kotlinx.android.synthetic.main.item_my_task.view.taskDescription
import kotlinx.android.synthetic.main.item_my_task.view.taskMore
import kotlinx.android.synthetic.main.item_my_task.view.taskName
import kotlinx.android.synthetic.main.item_my_task.view.taskStatus

class MyTasksFragment : Fragment(), MyTaskRecyclerAdapter.MyTaskRecyclerAdapterListener {
    private var _binding: FragmentMyTasksBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val toolbarModel: VMOtherToolbar by activityViewModels()

    protected val model: MyTasksFragmentModel by viewModels(ownerProducer = {
        requireParentFragment()
    })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyTasksBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onStart() {
        super.onStart()

        toolbarModel.setSpecs("",getString(R.string.title_tasks),
            false)

        closeMenu()
        closeBtn.setOnClickListener {
            closeMenu()
        }

        Thread(Runnable {
            val tasks: HashMap<String, Task> = hashMapOf()
            for(step in DataManager.sharedInstance.stepMap){
                for(substep in step.value.subSteps){
                    for(section in substep.value.sections){
                        for(task in section.value.tasks){
                            tasks[task.value.sectionId + "_" + task.value.taskId] = task.value
                        }
                    }
                }
            }
            requireActivity().runOnUiThread {
                model.tasks.value = tasks

                setupToDoTask()
                setupDoneTask()
                setupVoidTask()
            }
        }).start()

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

        confettiAnimMyTask.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                Log.e("Animation:", "start")
            }

            override fun onAnimationEnd(animation: Animator) {
                confettiAnimMyTask.pauseAnimation()
                confettiAnimMyTask.visibility = View.GONE
            }

            override fun onAnimationCancel(animation: Animator) {
                Log.e("Animation:", "cancel")
            }

            override fun onAnimationRepeat(animation: Animator) {
                Log.e("Animation:", "repeat")
            }
        })
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

    fun setupToDoTask() {
        toDoList.layoutManager = LinearLayoutManager(context)
        val taskAdapter = MyTaskRecyclerAdapter(this, model.getTasks(StatusTask.todo))
        toDoList.adapter = taskAdapter

        val spacingInPixels = 10
        toDoList.addItemDecoration(HomeFragment.SpacesItemDecoration(spacingInPixels))
    }

    fun setupDoneTask() {
        doneList.layoutManager = LinearLayoutManager(context)
        val taskAdapter = MyTaskRecyclerAdapter(this, model.getTasks(StatusTask.done))
        doneList.adapter = taskAdapter

        val spacingInPixels = 10
        toDoList.addItemDecoration(HomeFragment.SpacesItemDecoration(spacingInPixels))
    }

    fun setupVoidTask() {
        listedList.layoutManager = LinearLayoutManager(context)
        val taskAdapter = MyTaskRecyclerAdapter(this, model.getTasks(StatusTask.void))
        listedList.adapter = taskAdapter

        val spacingInPixels = 10
        listedList.addItemDecoration(HomeFragment.SpacesItemDecoration(spacingInPixels))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onTaskClicked(task: Task) {
        super.onTaskClicked(task)
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

    override fun playAnimation(){
        confettiAnimMyTask.visibility = View.VISIBLE
        confettiAnimMyTask.playAnimation()
    }

}

class MyTaskRecyclerAdapter(var delegate: MyTaskRecyclerAdapterListener, var tasks: HashMap<String, Task>) :
    RecyclerView.Adapter<MyTaskRecyclerAdapter.MyTaskHolder>() {

    override fun onBindViewHolder(holder: MyTaskHolder, position: Int) {
        val key = tasks.keys.sorted()[position]
        val task = tasks[key]
        holder.bindTask(task!!)
    }

    override fun getItemCount(): Int {
        return tasks.keys.sorted().count()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyTaskHolder {
        val inflatedView = parent.inflate(fr.oworld.stepbystep.R.layout.item_my_task, false)
        return MyTaskHolder(inflatedView, this)
    }

    class MyTaskHolder(v: View, var adapter: MyTaskRecyclerAdapter) : RecyclerView.ViewHolder(v),
        View.OnClickListener {
        private var view: View = v
        private var task: Task? = null

        init {
            view.taskTxt.setOnClickListener(this)
            view.taskChevron.setOnClickListener(this)
            view.taskStatus.setOnClickListener(this)

            configureFabMenu()
            view.unicornAnimMyTask.addAnimatorListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
                    Log.e("Animation:", "start")
                }

                override fun onAnimationEnd(animation: Animator) {
                    view.unicornAnimMyTask.pauseAnimation()
                    view.unicornAnimMyTask.visibility = View.GONE
                }

                override fun onAnimationCancel(animation: Animator) {
                    Log.e("Animation:", "cancel")
                }

                override fun onAnimationRepeat(animation: Animator) {
                    Log.e("Animation:", "repeat")
                }
            })
        }

        private fun configureFabMenu(){
            // main menu
            view.taskMore.setOnClickListener {
                (adapter.delegate as MyTasksFragment).showContentMenu(task!!)
            }
        }

        override fun onClick(p0: View?) {
            if(p0 == view.taskTxt || p0 == view.taskChevron) {
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
                    view.unicornAnimMyTask.visibility = View.VISIBLE
                    view.unicornAnimMyTask.playAnimation()

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
            view.taskOrigin.text = task.getStep()!!.stepName +"/" + task.getSubStep()!!.subStepName
            updateTaskImage()
        }

        fun updateTaskImage() {
            if(task!!.status == StatusTask.void) {
                view.taskStatus.setImageDrawable(
                    ContextCompat.getDrawable((adapter.delegate as MyTasksFragment).requireContext(),
                    R.drawable.ic_task_void))
                view.taskStatusTxt.text = (adapter.delegate as MyTasksFragment).getString(R.string.tache_liste)
            } else if(task!!.status == StatusTask.todo) {
                view.taskStatus.setImageDrawable(
                    ContextCompat.getDrawable((adapter.delegate as MyTasksFragment).requireContext(),
                        R.drawable.ic_task_todo))
                view.taskStatusTxt.text = (adapter.delegate as MyTasksFragment).getString(R.string.tache_a_faire)
            } else {
                view.taskStatus.setImageDrawable(
                    ContextCompat.getDrawable((adapter.delegate as MyTasksFragment).requireContext(),
                        R.drawable.ic_checked))
                view.taskStatusTxt.text = (adapter.delegate as MyTasksFragment).getString(R.string.tache_fait)
            }
        }
    }

    interface MyTaskRecyclerAdapterListener {
        fun onTaskClicked(task: Task) {
        }
        fun playAnimation(){
        }
    }
}
