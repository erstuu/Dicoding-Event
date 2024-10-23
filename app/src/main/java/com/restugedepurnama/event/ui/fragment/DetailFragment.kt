package com.restugedepurnama.event.ui.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.core.text.HtmlCompat
import com.bumptech.glide.Glide
import com.restugedepurnama.event.R
import com.restugedepurnama.event.databinding.FragmentDetailBinding
import com.restugedepurnama.event.ui.viewModel.MainViewModel
import com.restugedepurnama.event.ui.viewModel.ViewModelFactory
import com.restugedepurnama.event.data.Result
import com.restugedepurnama.event.data.local.entity.EventEntity
import com.restugedepurnama.event.settings.SettingPreferences
import com.restugedepurnama.event.settings.dataStore
import android.widget.Toast
import com.restugedepurnama.event.ui.MainActivity

class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var pref: SettingPreferences
    private val viewModel: MainViewModel by viewModels {
        ViewModelFactory.getInstance(
            requireContext(),
            pref
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Handle back button press
        requireActivity().onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().supportFragmentManager.popBackStack()
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupActionBar()
        hideBottomNavigation()

        pref = SettingPreferences.getInstance(requireContext().applicationContext.dataStore)

        val args = DetailFragmentArgs.fromBundle(requireArguments())
        val eventId = args.eventId
        Log.d("DetailFragment", "Event ID: $eventId")

        viewModel.getDetailEvent(eventId).observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.loading.visibility = View.VISIBLE
                }
                is Result.Success -> {
                    binding.loading.visibility = View.GONE
                    showDetailEvent(result.data)
                }
                is Result.Error -> {
                    binding.loading.visibility = View.GONE
                    binding.errorMessage.visibility = View.VISIBLE
                }
            }
        }

    }

    private fun hideBottomNavigation() {
        (activity as? MainActivity)?.hideBottomNavigation()
    }

    @Suppress("DEPRECATION")
    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                requireActivity().supportFragmentManager.popBackStack()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupActionBar() {
        (activity as AppCompatActivity).supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
        @Suppress("DEPRECATION")
        setHasOptionsMenu(true)
    }

    private fun showDetailEvent(detailEvent: EventEntity) {
        val imageUrl = detailEvent.mediaCover
        val title = detailEvent.name
        val ownerName = detailEvent.ownerName
        val date = detailEvent.beginTime
        val quota = detailEvent.quota
        val description = detailEvent.description
        val registrants = detailEvent.registrants

        binding.apply {
            Glide.with(requireActivity())
                .load(imageUrl)
                .into(binding.imgEvent)
            tvTitleEvent.text = title
            tvOwnerName.text = ownerName
            tvBeginTime.text = date
            tvQuota.text = (quota - registrants).toString()
            tvDescription.text = HtmlCompat.fromHtml(description, HtmlCompat.FROM_HTML_MODE_LEGACY)

            btnNavigate.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(detailEvent.link)
                startActivity(intent)
            }

            if (detailEvent.favorite) {
                btnFavorite.background = AppCompatResources.getDrawable(requireContext(), R.drawable.baseline_favorite_24)
            } else {
                btnFavorite.background = AppCompatResources.getDrawable(requireContext(), R.drawable.baseline_favorite_border_24)
            }

            btnFavorite.setOnClickListener {
                detailEvent.favorite = !detailEvent.favorite
                binding.btnFavorite.background = if (detailEvent.favorite) {
                    AppCompatResources.getDrawable(requireContext(), R.drawable.baseline_favorite_24)
                } else {
                    AppCompatResources.getDrawable(requireContext(), R.drawable.baseline_favorite_border_24)
                }

                viewModel.updateFavoriteStatus(detailEvent, detailEvent.favorite)

                val message = if (detailEvent.favorite) "Added to favorites" else "Removed from favorites"
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}