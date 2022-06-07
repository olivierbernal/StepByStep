package fr.oworld.stepbystep.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import fr.oworld.stepbystep.databinding.FragmentToolbarHomeBinding

class ToolbarHome : Fragment() {
    private var _binding: FragmentToolbarHomeBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentToolbarHomeBinding.inflate(inflater, container, false)
        val root: View = _binding!!.root

        return root
    }
}
