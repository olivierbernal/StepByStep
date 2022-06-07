package fr.oworld.stepbystep.ui.camera

import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.CameraView
import com.otaliastudios.cameraview.PictureResult
import com.otaliastudios.cameraview.VideoResult
import com.otaliastudios.cameraview.controls.Facing
import com.otaliastudios.cameraview.controls.Mode
import es.dmoral.toasty.Toasty
import fr.oworld.stepbystep.R
import fr.oworld.stepbystep.datas.model.ContentPrefix
import fr.oworld.stepbystep.datas.model.Task
import fr.oworld.stepbystep.ui.VMOtherToolbar
import kotlinx.android.synthetic.main.fragment_camera.*
import kotlinx.android.synthetic.main.item_content.view.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.RuntimeException
import java.util.*


class CameraModel: ViewModel() {
    var isImage = MutableLiveData<Boolean>()
    var task = MutableLiveData<Task>()
}

class CameraFragment: Fragment() {

    lateinit var camera: CameraView
    var currentFileName: String = ""
    private val toolbarModel: VMOtherToolbar by activityViewModels()

    protected val model: CameraModel by viewModels(ownerProducer = {
        requireParentFragment()
    })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_camera, container, false)

        camera = root.findViewById<CameraView>(R.id.camera);
        camera.setLifecycleOwner(getViewLifecycleOwner());

        return root
    }

    override fun onStop() {
        super.onStop()
        val bottomNav : BottomNavigationView = requireActivity().findViewById(R.id.nav_view)
        bottomNav.visibility = View.VISIBLE

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            android.R.id.home -> {
                activity?.onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onStart() {
        super.onStart()
        setHasOptionsMenu(true)

        camera.post {
            val bottomNav : BottomNavigationView = requireActivity().findViewById(R.id.nav_view)
            bottomNav.visibility = View.GONE
        }

        if (model.isImage.value!!) {
            camera.mode = Mode.PICTURE

            capturePicture.setOnClickListener {
                camera.takePicture()
            }
            toolbarModel.setSpecs(getString(R.string.photo), "", true)
        } else {
            camera.mode = Mode.VIDEO
            toolbarModel.setSpecs(getString(R.string.video), "", true)

            capturePicture.setOnClickListener {
                val path = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOCUMENTS
                )
                var filename = model.task.value!!.sectionId + "_" + model.task.value!!.taskId
                val currentTimestamp = System.currentTimeMillis()
                filename = ContentPrefix.mov.toString() + "_" + filename + "_" + currentTimestamp.toString()

                val file = File(path, "/$filename")
                currentFileName = filename

                camera.takeVideo(file, 10000)
            }
        }

        camera.addCameraListener(object : CameraListener() {
            override fun onPictureTaken(result: PictureResult) {
                result.toBitmap(2000,2000) { bmp ->
                    if (bmp == null){
                        return@toBitmap
                    }
                    try {
                        val file : File

                        val path = Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_DOCUMENTS
                        )
                        var filename =
                            model.task.value!!.sectionId + "_" + model.task.value!!.taskId
                        val currentTimestamp = System.currentTimeMillis()
                        filename =
                            ContentPrefix.img.toString() + "_" + filename + "_" + currentTimestamp.toString()
                        file = File(path, "/$filename")

                        FileOutputStream(file).use { out ->
                            bmp!!.compress(
                                Bitmap.CompressFormat.PNG,
                                100,
                                out) // bmp is your Bitmap instance
                        }
                        model.task.value!!.addContent(file.toString())
                        Toasty.success(requireContext(), getString(R.string.photo_ok)).show()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                    requireActivity().onBackPressed()
                }
            }

            override fun onVideoTaken(result: VideoResult) {
                super.onVideoTaken(result)
                val fileMovie = result.file
                try {
                    model.task.value!!.addContent(fileMovie.toString())
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                val retriever = MediaMetadataRetriever()
                try {
                    retriever.setDataSource(fileMovie.toString())

                    val image = retriever.getFrameAtTime(
                            10000,
                            MediaMetadataRetriever.OPTION_CLOSEST
                        )
                    val filename = result.file.toString() + "_" + ContentPrefix.mov_snap
                    val file = File(filename)

                    FileOutputStream(file).use { out ->
                        image!!.compress(
                            Bitmap.CompressFormat.PNG,
                            100,
                            out) // bmp is your Bitmap instance
                    }
                } catch (ex: IllegalArgumentException) {
                    ex.printStackTrace()
                } catch (ex: RuntimeException) {
                    ex.printStackTrace()
                }

                requireActivity().onBackPressed()
            }

            override fun onVideoRecordingStart() {
                super.onVideoRecordingStart()
                Toasty.info(requireContext(), getString(R.string.debut_video)).show()
            }

            override fun onVideoRecordingEnd() {
                super.onVideoRecordingEnd()
                Toasty.info(requireContext(), getString(R.string.fin_video)).show()
            }
        })

        toggleCamera.setOnClickListener {
            if (camera.isTakingPicture || camera.isTakingVideo) return@setOnClickListener
            when (camera.toggleFacing()) {
                Facing.BACK -> Toast.makeText(requireContext(), "Switched to back camera!", Toast.LENGTH_LONG).show()
                Facing.FRONT -> Toast.makeText(requireContext(), "Switched to front camera!", Toast.LENGTH_LONG).show()
            }
        }
    }
}
