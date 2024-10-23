package com.restugedepurnama.event.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.restugedepurnama.event.databinding.FragmentFavoriteBinding
import com.restugedepurnama.event.settings.SettingPreferences
import com.restugedepurnama.event.settings.dataStore
import com.restugedepurnama.event.ui.adapter.EventFavoriteAdapter
import com.restugedepurnama.event.ui.viewModel.MainViewModel
import com.restugedepurnama.event.ui.viewModel.ViewModelFactory

class FavoriteFragment : Fragment() {

    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!
    private lateinit var progressBar: ProgressBar
    private lateinit var message: TextView
    private lateinit var pref: SettingPreferences
    private lateinit var listRecyclerView: RecyclerView
    private lateinit var adapter: EventFavoriteAdapter
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
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pref = SettingPreferences.getInstance(requireContext().applicationContext.dataStore)

        progressBar = binding.loading
        message = binding.message

        listRecyclerView = binding.rvFavorite
        setupRecyclerView()

        viewModel.getFavoriteEvent().observe(viewLifecycleOwner) { result ->
            adapter.submitList(result) {
                if (adapter.itemCount == 0) {
                    message.visibility = View.VISIBLE
                } else {
                    message.visibility = View.GONE
                }
            }
        }

    }

    private fun setupRecyclerView() {

        adapter = EventFavoriteAdapter({ event ->
                viewModel.saveEvent(event)
                val action = FavoriteFragmentDirections.actionNavigationFavoriteToDetailFragment(event.id)
                findNavController().navigate(action)
            },
            viewModel
        )

        listRecyclerView.adapter = adapter
        listRecyclerView.layoutManager = LinearLayoutManager(requireContext())

    }
}