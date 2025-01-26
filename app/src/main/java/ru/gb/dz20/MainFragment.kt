package ru.gb.dz20

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.crashlytics.FirebaseCrashlytics
import ru.gb.dz20.databinding.FragmentMainBinding

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentMainBinding.inflate(inflater)
        binding.buttonBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonCrash.setOnClickListener {

            FirebaseCrashlytics.getInstance().log("This is log message with additional info")
            throw Exception("My first exception")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}