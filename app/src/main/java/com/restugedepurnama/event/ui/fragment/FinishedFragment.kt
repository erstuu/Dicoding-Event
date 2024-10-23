package com.restugedepurnama.event.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.restugedepurnama.event.databinding.FragmentFinishedBinding
import com.restugedepurnama.event.ui.viewModel.MainViewModel
import com.restugedepurnama.event.ui.viewModel.ViewModelFactory
import com.restugedepurnama.event.data.Result
import com.restugedepurnama.event.settings.SettingPreferences
import com.restugedepurnama.event.settings.dataStore
import com.restugedepurnama.event.ui.adapter.EventFinishedAdapter
import com.restugedepurnama.event.ui.adapter.SearchAdapter

class FinishedFragment : Fragment() {

    private var _binding: FragmentFinishedBinding? = null
    private val binding get() = _binding!!
    private lateinit var searchEventAdapter: SearchAdapter
    private lateinit var eventFinishedAdapter: EventFinishedAdapter
    private lateinit var rvSearchView: RecyclerView
    private lateinit var rvFinishedEvent: RecyclerView
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
        _binding = FragmentFinishedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pref = SettingPreferences.getInstance(requireContext().applicationContext.dataStore)

        rvSearchView = binding.rvSearch
        rvSearchView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        searchEventAdapter = SearchAdapter { event ->
            val action = FinishedFragmentDirections.actionFinishedFragmentToDetailFragment(event.id)
            view.findNavController().navigate(action)
        }
        rvSearchView.adapter = searchEventAdapter

        rvFinishedEvent = binding.rvFinishedEvent
        rvFinishedEvent.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        eventFinishedAdapter = EventFinishedAdapter { event ->
            val action = FinishedFragmentDirections.actionFinishedFragmentToDetailFragment(event.id)
            view.findNavController().navigate(action)
        }
        rvFinishedEvent.adapter = eventFinishedAdapter

        showFinishedEvents()

        with (binding) {
            searchView.setupWithSearchBar(binding.searchBar)
            searchView.editText.setOnEditorActionListener { _, _, _ ->
                val query = searchView.text.toString()
                viewModel.searchEvent(0, query).observe(viewLifecycleOwner) { result ->
                    when (result) {
                        is Result.Loading -> {
                            binding.loading.visibility = View.VISIBLE
                        }
                        is Result.Success -> {
                            binding.loading.visibility = View.GONE
                            val data = result.data
                            if (data.isEmpty()) {
                                binding.rvSearch.visibility = View.GONE
                                binding.errorMessage.visibility = View.VISIBLE
                            } else {
                                binding.rvSearch.visibility = View.VISIBLE
                                binding.errorMessage.visibility = View.GONE
                                searchEventAdapter.submitList(data)
                            }
                        }
                        is Result.Error -> {
                            binding.loading.visibility = View.GONE
                            binding.errorMessageSearch.visibility = View.VISIBLE
                        }
                    }
                }
                false
            }
            val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)

        }

    }

    private fun showFinishedEvents() {
        viewModel.finishedEvent().observe(viewLifecycleOwner) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        binding.loading.visibility = View.VISIBLE
                    }
                    is Result.Success -> {
                        binding.loading.visibility = View.GONE
                        eventFinishedAdapter.submitList(result.data)
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

