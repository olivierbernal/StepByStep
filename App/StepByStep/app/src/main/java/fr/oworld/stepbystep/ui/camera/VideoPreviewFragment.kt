package fr.oworld.stepbystep.ui.camera

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.MediaController
import android.widget.Toast
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.otaliastudios.cameraview.VideoResult
import com.otaliastudios.cameraview.size.AspectRatio
import fr.oworld.stepbystep.MainActivity
import fr.oworld.stepbystep.R
import fr.oworld.stepbystep.ui.VMOtherToolbar
import fr.oworld.stepbystep.ui.camera.PicturePreviewFragment
import kotlinx.android.synthetic.main.fragment_video_preview.*
import java.io.File

class VideoPreviewFragment : Fragment() {
    protected val model: PicturePreviewFragment.ContentPreviewModel by viewModels(ownerProducer = {
        requireParentFragment()
    })
    private val toolbarModel: VMOtherToolbar by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_video_preview, container, false)

        return root
    }

    lateinit var controller : MediaController

    override fun onStart() {
        super.onStart()
        (requireActivity() as? MainActivity)?.hideNavBar()
        setHasOptionsMenu(true)
        toolbarModel.setSpecs(getString(R.string.video), "", true)

        if(model.contentImage.value == null) {
            requireActivity().onBackPressed()
            return
        }

        video.setOnClickListener { playVideo() }

        controller = MediaController(requireContext())
        controller.setAnchorView(video)
        controller.setMediaPlayer(video)
        video.setMediaController(controller)
        video.setVideoURI(Uri.fromFile(File(model.contentImage.value!!)))
        video.setOnPreparedListener { mp ->
            val lp = video.layoutParams
            val videoWidth = mp.videoWidth.toFloat()
            val videoHeight = mp.videoHeight.toFloat()
            val viewWidth = video.width.toFloat()
            lp.height = (viewWidth * (videoHeight / videoWidth)).toInt()
            video.layoutParams = lp
            playVideo()
        }
    }

    override fun onStop() {
        super.onStop()
        (requireActivity() as? MainActivity)?.showNavBar()
    }


    fun playVideo() {
        if (!video.isPlaying) {
            video.start()
            controller.show(0);
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

}