package fr.oworld.stepbystep.ui.home

import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import fr.oworld.mpp.datas.DataManager
import fr.oworld.mpp.datas.inflate
import fr.oworld.stepbystep.R
import fr.oworld.stepbystep.datas.model.Step
import kotlinx.android.synthetic.main.item_step.view.*
import java.text.DecimalFormat
import java.text.NumberFormat

class StepRecyclerAdapter(var delegate: StepRecyclerAdapterListener) :
    RecyclerView.Adapter<StepRecyclerAdapter.StepHolder>() {

    override fun onBindViewHolder(holder: StepRecyclerAdapter.StepHolder, position: Int) {
        val key = DataManager.sharedInstance.stepMap.keys.sorted()[position]
        val step = DataManager.sharedInstance.stepMap[key]
        holder.bindStep(step!!)
    }

    override fun getItemCount(): Int {
        return DataManager.sharedInstance.stepMap.keys.sorted().count()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StepHolder {
        val inflatedView = parent.inflate(R.layout.item_step, false)
        val lp = inflatedView.layoutParams
        lp.width = (parent.measuredWidth / 2.1).toInt()
        lp.height = 430
        inflatedView.setLayoutParams(lp)
        return StepHolder(inflatedView, this)
    }

    class StepHolder(v: View, var adapter: StepRecyclerAdapter) : RecyclerView.ViewHolder(v), View.OnClickListener {
        private var view: View = v
        private var step : Step? = null

        init {
            v.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            if (adapter != null && step != null) {
                adapter.delegate.onStepClicked(step!!)
            }
        }

        fun bindStep(step: Step) {
            val model: HomeFragment.HomeFragmentModel by (adapter.delegate as HomeFragment).viewModels(ownerProducer = {
                (adapter.delegate as HomeFragment).requireParentFragment()
            })

            this.step = step
            view.stepNumber.setText(step!!.stepId.removePrefix("E"))
            view.stepName.setText(step!!.stepName)
            val percentage = step.getPercentageDone()

            val f: NumberFormat
            f = DecimalFormat("0")
            f.maximumFractionDigits = 1

            view.percentage.setText(f.format(percentage) + " %")
            view.percentageGuide.setGuidelinePercent((percentage / 100.0).toFloat())
            view.percentageView.setBackgroundColor((adapter.delegate as Fragment).resources.getColor(step.getPercentageColor()))
            view.percentage.setTextColor((adapter.delegate as Fragment).resources.getColor(step.getPercentageTextColor()))
            if (percentage == 100.0) {
                view.stepCard.setCardBackgroundColor((adapter.delegate as Fragment).resources.getColor(R.color.grey))
                view.checkImage.visibility = View.VISIBLE
                view.stepName.setTextColor((adapter.delegate as Fragment).resources.getColor(R.color.greyText))
            } else if (percentage != 0.0) {
                view.stepCard.setCardBackgroundColor((adapter.delegate as Fragment).resources.getColor(R.color.orange))
                view.checkImage.visibility = View.GONE
                view.stepName.setTextAppearance((adapter.delegate as Fragment).requireContext(), R.style.TxtNormalBoldBase)
                view.stepName.setTextColor((adapter.delegate as Fragment).resources.getColor(R.color.green))
            } else {
                view.percentageGuide.setGuidelinePercent(1f)
                view.stepName.setTextColor((adapter.delegate as Fragment).resources.getColor(R.color.green))
                view.checkImage.visibility = View.GONE
            }

            if(model.currentStep.value == this.step) {
                view.stepCard.strokeColor = (adapter.delegate as Fragment).resources.getColor(R.color.green)
                view.stepCard.strokeWidth = 10
            } else {
                view.stepCard.strokeColor = (adapter.delegate as Fragment).resources.getColor(R.color.transparent)
                view.stepCard.strokeWidth = 0
            }
        }
    }

    interface StepRecyclerAdapterListener {
        fun onStepClicked(step: Step)
    }
}
