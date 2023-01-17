package com.example.pora_projekat

import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.Navigation
import com.example.pora_projekat.databinding.FragmentSoundInputBinding
import com.example.pora_projekat.services.APIUtil
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SoundInputFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SoundInputFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding:com.example.pora_projekat.databinding.FragmentSoundInputBinding
    private var MICROPHONE_PERMISSION_CODE = 200
    private var mediaRecorder:MediaRecorder = MediaRecorder()
    var currentTime: Date = Calendar.getInstance().getTime()
    var df: SimpleDateFormat = SimpleDateFormat("dd-MMM-yyyy hh:mm:ss", Locale.getDefault())

    private var latitude: String? = "0"
    private var longitude: String? = "0"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_sound_input, container, false)
        binding = FragmentSoundInputBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(isMicrophonePresent()) {
            getMicrophonePermission()
        }
        binding.btnRecord.setOnClickListener {
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            mediaRecorder.setOutputFile(getRecordingFilePath())
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            mediaRecorder.prepare()
            mediaRecorder.start()

            Toast.makeText(context,"Recording started",Toast.LENGTH_SHORT).show()
        }
        binding.btnStopRecording.setOnClickListener {
            mediaRecorder.stop()
            mediaRecorder.release()
            Toast.makeText(context,"Recording stopped",Toast.LENGTH_SHORT).show()
        }
        binding.textViewRecordedFile.text = getRecordingFilePath()

        binding.buttonLocation.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_soundInputFragment_to_mapFragment)
        }

        var formattedDate: String = df.format(currentTime)
        binding.txtTime2.text = formattedDate
        setFragmentResultListener("requestKey") { key, bundle ->
            latitude = bundle.getString("latitude")
            longitude = bundle.getString("longitude")
            binding.txtViewLocation2.text = "$latitude $longitude"
        }

        binding.btnSave2.setOnClickListener {
            APIUtil.uploadFile(getRecordingFilePath()!!, APIUtil.BASE_URL, latitude!!, longitude!!, APIUtil.MIME_MP3)
            Toast.makeText(requireActivity(), "Audio file uploading..", Toast.LENGTH_SHORT).show()
            Log.d("SoundInputFragment", "Audio file uploading..")
        }

    }
    fun isMicrophonePresent():Boolean {
        return requireContext().packageManager.hasSystemFeature(PackageManager.FEATURE_MICROPHONE)
    }
    fun getMicrophonePermission() {
        if(ContextCompat.checkSelfPermission(requireContext(),android.Manifest.permission.RECORD_AUDIO) ==
                PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(requireActivity(),
                Array<String>(100) {android.Manifest.permission.RECORD_AUDIO}, MICROPHONE_PERMISSION_CODE)
        }
    }
    fun getRecordingFilePath():String {
        var contextWrapper = ContextWrapper(requireContext())
        var musicDirectory: File? = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_MUSIC)
        var file = File(musicDirectory,"testRecordingFile" + ".mp3")
        return file.path
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SoundInputFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SoundInputFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}