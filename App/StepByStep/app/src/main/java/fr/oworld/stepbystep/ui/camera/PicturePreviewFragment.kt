package fr.oworld.stepbystep.ui.camera

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import fr.oworld.stepbystep.MainActivity
import fr.oworld.stepbystep.R
import fr.oworld.stepbystep.ui.VMOtherToolbar
import kotlinx.android.synthetic.main.fragment_picture_preview.*
import kotlinx.android.synthetic.main.item_content.view.*

class PicturePreviewFragment : Fragment() {

    class ContentPreviewModel: ViewModel() {
        var contentImage = MutableLiveData<String>()
    }

    protected val model: ContentPreviewModel by viewModels(ownerProducer = {
        requireParentFragment()
    })
    private val toolbarModel: VMOtherToolbar by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_picture_preview, container, false)

        return root
    }

    override fun onStart() {
        super.onStart()
        (requireActivity() as? MainActivity)?.hideNavBar()
        setHasOptionsMenu(true)
        toolbarModel.setSpecs(getString(R.string.video), "", true)

        if(model.contentImage.value == null) {
            requireActivity().onBackPressed()
            return
        }
        image.setImageBitmap(BitmapFactory.decodeFile(this.model.contentImage.value!!))
    }

    override fun onStop() {
        super.onStop()
        (requireActivity() as? MainActivity)?.showNavBar()
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
}