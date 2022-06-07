package fr.oworld.stepbystep.ui.dashboard

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexboxLayoutManager
import fr.oworld.mpp.datas.DataManager
import fr.oworld.mpp.datas.inflate
import fr.oworld.stepbystep.R
import fr.oworld.stepbystep.databinding.FragmentDashboardBinding
import fr.oworld.stepbystep.datas.model.ContentPrefix
import fr.oworld.stepbystep.datas.model.Skill
import fr.oworld.stepbystep.ui.VMOtherToolbar
import fr.oworld.stepbystep.ui.home.HomeFragment
import kotlinx.android.synthetic.main.fragment_dashboard.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.item_skill.view.*
import kotlin.random.Random

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val toolbarModel: VMOtherToolbar by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onStart() {
        super.onStart()

        toolbarModel.setSpecs("", getString(R.string.title_dashboard),
            false)

        weatherThunderCard.setOnClickListener {
            weatherThunderCard.setCardBackgroundColor(requireContext().getColor(fr.oworld.stepbystep.R.color.white))
            weatherRainyCard.setCardBackgroundColor(requireContext().getColor(fr.oworld.stepbystep.R.color.orange))
            weatherCloudCard.setCardBackgroundColor(requireContext().getColor(fr.oworld.stepbystep.R.color.orange))
            weathersunCard.setCardBackgroundColor(requireContext().getColor(fr.oworld.stepbystep.R.color.orange))
        }

        weatherRainyCard.setOnClickListener {
            weatherThunderCard.setCardBackgroundColor(requireContext().getColor(fr.oworld.stepbystep.R.color.orange))
            weatherRainyCard.setCardBackgroundColor(requireContext().getColor(fr.oworld.stepbystep.R.color.white))
            weatherCloudCard.setCardBackgroundColor(requireContext().getColor(fr.oworld.stepbystep.R.color.orange))
            weathersunCard.setCardBackgroundColor(requireContext().getColor(fr.oworld.stepbystep.R.color.orange))
        }

        weatherCloudCard.setOnClickListener {
            weatherThunderCard.setCardBackgroundColor(requireContext().getColor(fr.oworld.stepbystep.R.color.orange))
            weatherRainyCard.setCardBackgroundColor(requireContext().getColor(fr.oworld.stepbystep.R.color.orange))
            weatherCloudCard.setCardBackgroundColor(requireContext().getColor(fr.oworld.stepbystep.R.color.white))
            weathersunCard.setCardBackgroundColor(requireContext().getColor(fr.oworld.stepbystep.R.color.orange))
        }

        weathersunCard.setOnClickListener {
            weatherThunderCard.setCardBackgroundColor(requireContext().getColor(fr.oworld.stepbystep.R.color.orange))
            weatherRainyCard.setCardBackgroundColor(requireContext().getColor(fr.oworld.stepbystep.R.color.orange))
            weatherCloudCard.setCardBackgroundColor(requireContext().getColor(fr.oworld.stepbystep.R.color.orange))
            weathersunCard.setCardBackgroundColor(requireContext().getColor(fr.oworld.stepbystep.R.color.white))
        }
        weathersunCard.callOnClick()
        setupSkillList()

        val listTips = DataManager.sharedInstance.tipsList
        val randomInt = (0 until listTips.count()).random()
        val randomTips = listTips[randomInt]
        tipsDetails.text = randomTips.tips
    }

    fun setupSkillList() {
        skillList.layoutManager = FlexboxLayoutManager(requireContext())

        val stepAdapter = SKillRecyclerAdapter(this,
            DataManager.sharedInstance.skillList)
        skillList.adapter = stepAdapter

        val spacingInPixels = 20
        skillList.addItemDecoration(HomeFragment.SpacesItemDecoration(spacingInPixels))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    class SKillRecyclerAdapter(var delegate: DashboardFragment, var skills: MutableList<Skill>) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            if (holder is SkillHolder) {
                holder.bindSkill(skills[position])
            }
        }

        override fun getItemCount(): Int {
            return skills.count()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val inflatedView = parent.inflate(R.layout.item_skill, false)
            val lp = inflatedView.layoutParams
            lp.width = (parent.measuredWidth / 2.2).toInt()
            return SkillHolder(inflatedView, this)
        }

        class SkillHolder(v: View, var adapter: SKillRecyclerAdapter) : RecyclerView.ViewHolder(v) {
            private var view: View = v
            private var skill: Skill? = null

            fun bindSkill(skill: Skill) {
                this.skill = skill
                val tasksDone = skill.numberTaskDone()
                val tasks = skill.numberTask()
                view.progressTxt.text = tasksDone.toString() + "/" + tasks.toString()
                if(tasks == tasksDone) {
                    view.skillCard.setCardBackgroundColor(adapter.delegate.resources.getColor(R.color.green))
                    view.detailsTxt.setTextColor(adapter.delegate.resources.getColor(R.color.white))
                    if(skill.skillName.startsWith("e", true)) {
                        view.detailsTxt.text = adapter.delegate.resources.getString(R.string.skill_fini_skill_e, skill.skillName)
                    } else {
                        view.detailsTxt.text = adapter.delegate.resources.getString(R.string.skill_fini, skill.skillName)
                    }
                    view.detailsTxt.text = "" + skill.skillName
                    view.successImg.visibility = View.VISIBLE
                } else {
                    view.successImg.visibility = View.GONE
                    view.skillCard.setCardBackgroundColor(adapter.delegate.resources.getColor(R.color.white))
                    view.detailsTxt.setTextColor(adapter.delegate.resources.getColor(R.color.black))
                    if(skill.skillName.startsWith("e", true)) {
                        view.detailsTxt.text = adapter.delegate.resources.getString(R.string.skill_pas_fini_skill_e,
                            (tasks - tasksDone).toString(),
                            skill.skillName)
                    } else {
                        view.detailsTxt.text = adapter.delegate.resources.getString(R.string.skill_pas_fini,
                            (tasks - tasksDone).toString(),
                            skill.skillName)
                    }
                    view.successImg.visibility = View.GONE
                }
            }
        }
    }
}