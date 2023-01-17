package com.example.pora_projekat

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.Navigation
import com.example.pora_projekat.databinding.FragmentImageSimulationBinding
import com.example.pora_projekat.databinding.FragmentSoundSimulationBinding
import com.example.pora_projekat.services.AudioService
import com.example.pora_projekat.services.AudioServiceSimulation

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SoundSimulationFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SoundSimulationFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding:FragmentSoundSimulationBinding

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
        //return inflater.inflate(R.layout.fragment_sound_simulation, container, false)
        binding = FragmentSoundSimulationBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnLocation.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_soundSimulationFragment_to_mapFragment)
        }
        setFragmentResultListener("requestKey") { key, bundle ->
            latitude = bundle.getString("latitude")
            longitude = bundle.getString("longitude")
            binding.textViewGetLocation.text = "$latitude $longitude"
        }

        binding.btnOff.setOnClickListener {
            requireActivity().stopService(Intent(requireActivity(), AudioServiceSimulation::class.java))
            requireActivity().stopService(Intent(requireActivity(), AudioService::class.java))
            Toast.makeText(requireActivity(), "Services stopped!", Toast.LENGTH_SHORT).show()
        }

        binding.btnSimulation.setOnClickListener {
            Intent(requireActivity(), AudioServiceSimulation::class.java).also { intent ->
                intent.putExtra(AudioService.EXTRA_FROM, 0f)
                intent.putExtra(AudioService.EXTRA_TO, 24f)
                intent.putExtra(AudioService.EXTRA_INTERVAL, 30)
                intent.putExtra(AudioService.EXTRA_DURATION, 5)
                intent.putExtra(AudioService.EXTRA_LATITUDE, 0f)
                intent.putExtra(AudioService.EXTRA_LONGITUDE, 0f)
                requireActivity().startService(intent)
            }
            Toast.makeText(requireActivity(), "Simulation started!", Toast.LENGTH_SHORT).show()
        }

        binding.btnOn.setOnClickListener {
            Intent(requireActivity(), AudioService::class.java).also { intent ->
                intent.putExtra(AudioService.EXTRA_FROM, 0f)
                intent.putExtra(AudioService.EXTRA_TO, 24f)
                intent.putExtra(AudioService.EXTRA_INTERVAL, 30)
                intent.putExtra(AudioService.EXTRA_DURATION, 5)
                intent.putExtra(AudioService.EXTRA_LATITUDE, 0f)
                intent.putExtra(AudioService.EXTRA_LONGITUDE, 0f)
                requireActivity().startService(intent)
            }
            Toast.makeText(requireActivity(), "Service started!", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SoundSimulationFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SoundSimulationFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}