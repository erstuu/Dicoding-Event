package com.restugedepurnama.event.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.restugedepurnama.event.data.Result
import com.restugedepurnama.event.ui.adapter.EventUpcomingPageAdapter
import com.restugedepurnama.event.databinding.FragmentUpcomingBinding
import com.restugedepurnama.event.settings.SettingPreferences
import com.restugedepurnama.event.settings.dataStore
import com.restugedepurnama.event.ui.viewModel.MainViewModel
import com.restugedepurnama.event.ui.viewModel.ViewModelFactory

class UpcomingFragment : Fragment() {

    private var _binding: FragmentUpcomingBinding? = null
    private val binding get() = _binding!!
    private lateinit var eventUpcomingAdapter: EventUpcomingPageAdapter
    private lateinit var pref: SettingPreferences
    private val viewModel: MainViewModel by viewModels {
        ViewModelFactory.getInstance(
            requireContext(),
            pref
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentUpcomingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pref = SettingPreferences.getInstance(requireContext().applicationContext.dataStore)

        binding.rvUpcomingPage.layoutManager = LinearLayoutManager(context)
        eventUpcomingAdapter = EventUpcomingPageAdapter { event ->
            val action = UpcomingFragmentDirections.actionNavigationUpcomingToDetailFragment(event.id)
            findNavController().navigate(action)
        }
        binding.rvUpcomingPage.adapter = eventUpcomingAdapter

        viewModel.activeEvent().observe(viewLifecycleOwner) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        binding.loading.visibility = View.VISIBLE
                    }
                    is Result.Success -> {
                        binding.loading.visibility = View.GONE
                        eventUpcomingAdapter.submitList(result.data.take(5))
                    }
                    is Result.Error -> {
                        binding.loading.visibility = View.GONE
                        binding.errorMessage.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}