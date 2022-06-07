package fr.oworld.stepbystep.ui.MyContent

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexboxLayoutManager
import fr.oworld.mpp.datas.DataManager
import fr.oworld.mpp.datas.inflate
import fr.oworld.stepbystep.R
import fr.oworld.stepbystep.databinding.FragmentMyContentBinding
import fr.oworld.stepbystep.datas.model.ContentPrefix
import fr.oworld.stepbystep.ui.MyTasks.MyContentFragmentModel
import fr.oworld.stepbystep.ui.VMOtherToolbar
import fr.oworld.stepbystep.ui.camera.PicturePreviewFragment
import kotlinx.android.synthetic.main.fragment_my_content.*
import kotlinx.android.synthetic.main.item_content.view.*
import kotlinx.android.synthetic.main.item_content_title.view.*


class MyContentFragment : Fragment(), MyContentRecyclerAdapter.MyContentRecyclerAdapterListener {
    private var _binding: FragmentMyContentBinding? = null

    private val binding get() = _binding!!

    protected val model: MyContentFragmentModel by viewModels(ownerProducer = {
        requireParentFragment()
    })

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
    private val toolbarModel: VMOtherToolbar by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyContentBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onStart() {
        super.onStart()

        toolbarModel.setSpecs("",getString(R.string.title_contents),
            false)

        sortByType()
        model.typeContent.value = MyContentFragmentModel.TypeContent.none
        setupContentList()

        model.typeContent.observe(viewLifecycleOwner) {
            updateContentList()
        }
        model.allContents.observe(viewLifecycleOwner) {
            updateContentList()
        }

        videoBtn.setOnClickListener {
            if(model.typeContent.value == MyContentFragmentModel.TypeContent.video) {
                model.typeContent.value = MyContentFragmentModel.TypeContent.none
                videoBtn.setCardBackgroundColor(requireContext().getColor(fr.oworld.stepbystep.R.color.white))
                return@setOnClickListener
            }
            model.typeContent.value = MyContentFragmentModel.TypeContent.video
            videoBtn.setCardBackgroundColor(requireContext().getColor(fr.oworld.stepbystep.R.color.orange))
            photoBtn.setCardBackgroundColor(requireContext().getColor(fr.oworld.stepbystep.R.color.white))
            audioBtn.setCardBackgroundColor(requireContext().getColor(fr.oworld.stepbystep.R.color.white))
            noteBtn.setCardBackgroundColor(requireContext().getColor(fr.oworld.stepbystep.R.color.white))
        }

        photoBtn.setOnClickListener {
            if(model.typeContent.value == MyContentFragmentModel.TypeContent.photo) {
                model.typeContent.value = MyContentFragmentModel.TypeContent.none
                photoBtn.setCardBackgroundColor(requireContext().getColor(fr.oworld.stepbystep.R.color.white))
                return@setOnClickListener
            }
            model.typeContent.value = MyContentFragmentModel.TypeContent.photo
            videoBtn.setCardBackgroundColor(requireContext().getColor(fr.oworld.stepbystep.R.color.white))
            photoBtn.setCardBackgroundColor(requireContext().getColor(fr.oworld.stepbystep.R.color.orange))
            audioBtn.setCardBackgroundColor(requireContext().getColor(fr.oworld.stepbystep.R.color.white))
            noteBtn.setCardBackgroundColor(requireContext().getColor(fr.oworld.stepbystep.R.color.white))
        }

        audioBtn.setOnClickListener {
            if(model.typeContent.value == MyContentFragmentModel.TypeContent.audio) {
                model.typeContent.value = MyContentFragmentModel.TypeContent.none
                audioBtn.setCardBackgroundColor(requireContext().getColor(fr.oworld.stepbystep.R.color.white))
                return@setOnClickListener
            }
            model.typeContent.value = MyContentFragmentModel.TypeContent.audio
            videoBtn.setCardBackgroundColor(requireContext().getColor(fr.oworld.stepbystep.R.color.white))
            photoBtn.setCardBackgroundColor(requireContext().getColor(fr.oworld.stepbystep.R.color.white))
            audioBtn.setCardBackgroundColor(requireContext().getColor(fr.oworld.stepbystep.R.color.orange))
            noteBtn.setCardBackgroundColor(requireContext().getColor(fr.oworld.stepbystep.R.color.white))
        }

        noteBtn.setOnClickListener {
            if(model.typeContent.value == MyContentFragmentModel.TypeContent.note) {
                model.typeContent.value = MyContentFragmentModel.TypeContent.none
                noteBtn.setCardBackgroundColor(requireContext().getColor(fr.oworld.stepbystep.R.color.white))
                return@setOnClickListener
            }
            model.typeContent.value = MyContentFragmentModel.TypeContent.note
            videoBtn.setCardBackgroundColor(requireContext().getColor(fr.oworld.stepbystep.R.color.white))
            photoBtn.setCardBackgroundColor(requireContext().getColor(fr.oworld.stepbystep.R.color.white))
            audioBtn.setCardBackgroundColor(requireContext().getColor(fr.oworld.stepbystep.R.color.white))
            noteBtn.setCardBackgroundColor(requireContext().getColor(fr.oworld.stepbystep.R.color.orange))
        }

        sortCard.setOnClickListener {
            if (sortType.text != getString(R.string.par_etape)) {
                sortView.visibility = View.INVISIBLE
                sortType.text = getString(R.string.par_etape)
                sortByStep()

                val currentLayout = contentList.layoutParams as ConstraintLayout.LayoutParams // btn is a View here
                currentLayout.topToBottom = topView.id // resource ID of new parent field
                contentList.layoutParams = currentLayout

            } else {
                sortView.visibility = View.VISIBLE
                sortType.text = getString(R.string.par_type)
                sortByType()

                val currentLayout = contentList.layoutParams as ConstraintLayout.LayoutParams // btn is a View here
                currentLayout.topToBottom = sortView.id // resource ID of new parent field
                contentList.layoutParams = currentLayout
            }
        }
    }

    fun sortByType(){
        val contents: MutableList<String> = mutableListOf()
        for(step in DataManager.sharedInstance.stepMap){
            for(substep in step.value.subSteps){
                for(section in substep.value.sections){
                    for(task in section.value.tasks){
                        contents.addAll(task.value.contentList)
                    }
                }
            }
        }
        model.allContents.value = contents
    }

    fun sortByStep(){
        model.typeContent.value = MyContentFragmentModel.TypeContent.none
        val contents: MutableList<String> = mutableListOf()
        var added = false
        for(step in DataManager.sharedInstance.stepMap){
            for(substep in step.value.subSteps){
                added = false
                for(section in substep.value.sections){
                    for(task in section.value.tasks){
                        if(!added && task.value.contentList.count() > 0){
                            contents.add("title_" + step.value.stepName + " / " + substep.value.subStepName)
                            added = true
                        }
                        contents.addAll(task.value.contentList)
                    }
                }
            }
        }
        model.allContents.value = contents
    }

    fun setupContentList() {
        contentList.layoutManager = FlexboxLayoutManager(requireContext())

        val stepAdapter = MyContentRecyclerAdapter(this,
            model.getContent(model.typeContent.value!!))
        contentList.adapter = stepAdapter
    }

    fun updateContentList() {
        (contentList.adapter  as MyContentRecyclerAdapter).contents =
            model.getContent(model.typeContent.value!!)
        contentList.adapter!!.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onContentClick(content: String) {
        if(content.contains(ContentPrefix.img.toString())) {
            openPhotoContent(content)
        } else if(content.contains(ContentPrefix.mov.toString())) {
            openVideoContent(content)
        }
    }

    fun openPhotoContent(content: String) {
        val model: PicturePreviewFragment.ContentPreviewModel by viewModels(ownerProducer = {
            requireParentFragment()
        })
        model.contentImage.value = content

        val navBuilder = NavOptions.Builder()
        val navOptions: NavOptions = navBuilder
            .build()
        findNavController().navigate(
            fr.oworld.stepbystep.R.id.navigation_preview_image,
            null,
            navOptions
        )
    }

    fun openVideoContent(content: String) {
        val model: PicturePreviewFragment.ContentPreviewModel by viewModels(ownerProducer = {
            requireParentFragment()
        })
        model.contentImage.value = content

        val navBuilder = NavOptions.Builder()
        val navOptions: NavOptions = navBuilder
            .build()
        findNavController().navigate(
            fr.oworld.stepbystep.R.id.navigation_preview_video,
            null,
            navOptions
        )
    }
}

class MyContentRecyclerAdapter(var delegate: MyContentRecyclerAdapterListener, var contents: MutableList<String>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MyContentHolder) {
            holder.bindContent(contents[position])
        } else if (holder is MyContentTitleHolder) {
            holder.bindTitle(contents[position])
        }
    }

    override fun getItemCount(): Int {
        return contents.count()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if(viewType == 1){
            val inflatedView = parent.inflate(R.layout.item_content, false)
            val lp = inflatedView.layoutParams
            lp.width = parent.measuredWidth / 4
            return MyContentHolder(inflatedView, this)
        } else {
            val inflatedView = parent.inflate(R.layout.item_content_title, false)
            return MyContentTitleHolder(inflatedView, this)
        }
    }

    override fun getItemViewType(position: Int): Int {
        if(contents[position].startsWith("title_")) {
            return 0
        } else {
            return 1
        }
    }

    class MyContentHolder(v: View, var adapter: MyContentRecyclerAdapter) : RecyclerView.ViewHolder(v),
        View.OnClickListener {
        private var view: View = v
        private var content: String? = null

        init {
            v.setOnClickListener {
                onClick(it)
            }
        }

        override fun onClick(p0: View?) {
            if (adapter != null && content != null) {
                adapter.delegate.onContentClick(content!!)
            }
        }

        fun bindContent(content: String) {
            this.content = content

            if(this.content!!.contains(ContentPrefix.img.toString())) {
                view.image.setImageBitmap(BitmapFactory.decodeFile(this.content!!))
                view.videoTag.visibility = View.GONE
            } else if(this.content!!.contains(ContentPrefix.mov.toString())) {
                view.image.setImageBitmap(BitmapFactory.decodeFile(this.content!! + "_" + ContentPrefix.mov_snap))
                view.videoTag.visibility = View.VISIBLE
            }
        }
    }

    class MyContentTitleHolder(v: View, var adapter: MyContentRecyclerAdapter) : RecyclerView.ViewHolder(v)  {
        private var view: View = v
        private var title: String? = null

        fun bindTitle(title: String) {
            this.title = title
            view.contentTitle.text = title.removePrefix("title_")
        }
    }

    interface MyContentRecyclerAdapterListener {
        fun onContentClick(content: String) {
        }
    }
}
