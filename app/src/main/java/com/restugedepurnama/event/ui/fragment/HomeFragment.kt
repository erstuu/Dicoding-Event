package com.restugedepurnama.event.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.restugedepurnama.event.databinding.FragmentHomeBinding
import com.restugedepurnama.event.ui.adapter.EventFinishedAdapter
import com.restugedepurnama.event.ui.viewModel.MainViewModel
import com.restugedepurnama.event.ui.viewModel.ViewModelFactory
import com.restugedepurnama.event.data.Result
import com.restugedepurnama.event.settings.SettingPreferences
import com.restugedepurnama.event.settings.dataStore
import com.restugedepurnama.event.ui.adapter.EventUpcomingAdapter

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var rvEventUpcoming: RecyclerView
    private lateinit var rvEventActive: RecyclerView
    private lateinit var eventFinishedAdapter: EventFinishedAdapter
    private lateinit var eventUpcomingAdapter: EventUpcomingAdapter
    private lateinit var pref: SettingPreferences
    private val viewModel: MainViewModel by viewModels {
        ViewModelFactory.getInstance(
            requireContext(),
            pref
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pref = SettingPreferences.getInstance(requireContext().applicationContext.dataStore)

        rvEventUpcoming = binding.rvEventUpcoming
        rvEventUpcoming.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        eventUpcomingAdapter = EventUpcomingAdapter { event ->
            val action = HomeFragmentDirections.actionNavigationHomeToDetailFragment(event.id)
            view.findNavController().navigate(action)
        }
        rvEventUpcoming.adapter = eventUpcomingAdapter

        rvEventActive = binding.rvEventActive
        rvEventActive.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        eventFinishedAdapter = EventFinishedAdapter { event ->
            val action = HomeFragmentDirections.actionNavigationHomeToDetailFragment(event.id)
            view.findNavController().navigate(action)
        }
        rvEventActive.adapter = eventFinishedAdapter

        viewModel.finishedEvent().observe(viewLifecycleOwner) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        binding.loading.visibility = View.VISIBLE
                    }
                    is Result.Success -> {
                        binding.loading.visibility = View.GONE
                        eventFinishedAdapter.submitList(result.data.take(5))
                    }
                    is Result.Error -> {
                        binding.loading.visibility = View.GONE
                        Toast.makeText(context, "Error: ${result.error}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

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
                    }
                }
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}