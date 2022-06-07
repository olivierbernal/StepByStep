package fr.oworld.stepbystep.ui.home

import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import fr.oworld.mpp.datas.DataManager
import fr.oworld.mpp.datas.inflate
import fr.oworld.stepbystep.R
import fr.oworld.stepbystep.datas.model.Step
import fr.oworld.stepbystep.datas.model.SubStep
import kotlinx.android.synthetic.main.item_substep.view.*
import java.text.DecimalFormat
import java.text.NumberFormat

class SubStepRecyclerAdapter(var selectedStep: Step, var delegate: SubStepRecyclerAdapterListener) :
    RecyclerView.Adapter<SubStepRecyclerAdapter.SubStepHolder>() {

    override fun onBindViewHolder(holder:SubStepRecyclerAdapter.SubStepHolder, position: Int) {
        val key = selectedStep.subSteps.keys.sorted()[position]
        val subStep = selectedStep.subSteps[key]
        holder.bindSubStep(subStep!!)
    }

    override fun getItemCount(): Int {
        return selectedStep.subSteps.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubStepHolder {
        val inflatedView = parent.inflate(R.layout.item_substep, false)
        return SubStepHolder(inflatedView, this)
    }

    class SubStepHolder(v: View, var adapter: SubStepRecyclerAdapter) : RecyclerView.ViewHolder(v), View.OnClickListener {
        private var view: View = v
        private var subStep : SubStep? = null

        init {
            v.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            if (adapter != null && subStep != null) {
                adapter.delegate.onSubStepClicked(subStep!!)
            }
        }

        fun bindSubStep(subStep: SubStep) {
            this.subStep = subStep
            view.subStepName.setText(subStep.subStepName)
            view.subStepPercentage.setText("0 %")

            val lastKey = adapter.selectedStep.subSteps.keys.sorted()[adapter.selectedStep.subSteps.keys.count() - 1]
            if (subStep.subStepId == lastKey) {
                view.arrow.visibility = View.GONE
            }

            val percentage = subStep.getPercentageDone()
            val f: NumberFormat
            f = DecimalFormat("0")
            f.maximumFractionDigits = 0

            view.subStepPercentage.setText(f.format(percentage) + " %")
            view.subStepPercentageGuide.setGuidelinePercent((percentage / 100f).toFloat())
            view.subStepPercentageView.setBackgroundColor((adapter.delegate as Fragment).resources.getColor(subStep.getPercentageColor()))
            view.subStepPercentage.setTextColor((adapter.delegate as Fragment).resources.getColor(subStep.getPercentageTextColor()))
            if (percentage == 100.0) {
                view.subStepCard.setCardBackgroundColor((adapter.delegate as Fragment).resources.getColor(R.color.grey))
                view.subStepCheckImage.visibility = View.VISIBLE
            } else if (percentage != 0.0) {
                view.subStepCard.setCardBackgroundColor((adapter.delegate as Fragment).resources.getColor(R.color.orange))
                view.subStepCheckImage.visibility = View.GONE
                view.subStepName.setTextAppearance((adapter.delegate as Fragment).requireContext(), R.style.TxtNormalBoldBase)
            } else {
                view.subStepPercentageGuide.setGuidelinePercent(1f)
                view.subStepCheckImage.visibility = View.GONE
            }
        }
    }

    interface SubStepRecyclerAdapterListener {
        fun onSubStepClicked(subStep: SubStep)
    }
}
