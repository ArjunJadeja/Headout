package com.arjun.headout.ui.profile.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.arjun.headout.R
import com.arjun.headout.data.local.preferences.DEFAULT_PREFERENCE
import com.arjun.headout.data.local.preferences.PreferencesHelper
import com.arjun.headout.data.model.Experience
import com.arjun.headout.databinding.FragmentSavedExperiencesBinding
import com.arjun.headout.ui.MainViewModel
import com.arjun.headout.ui.profile.ProfileViewModel
import com.arjun.headout.ui.profile.adapters.ISavedExperience
import com.arjun.headout.ui.profile.adapters.SavedExperiencesAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SavedExperiencesFragment : Fragment(R.layout.fragment_saved_experiences),
    ISavedExperience {

    private lateinit var binding: FragmentSavedExperiencesBinding
    private val mainViewModel: MainViewModel by activityViewModels()
    private val profileViewModel: ProfileViewModel by activityViewModels()

    private var languageCode = DEFAULT_PREFERENCE
    private lateinit var adapter: SavedExperiencesAdapter

    private var savedExperiences: MutableList<Experience> = mutableListOf()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSavedExperiencesBinding.bind(view)

        lifecycleScope.launch {
            languageCode =
                mainViewModel.readData(PreferencesHelper.SELECTED_LANGUAGE_CODE, DEFAULT_PREFERENCE)

            val savedExperienceIds = profileViewModel.getSavedExperiencesIds()
            savedExperiences = savedExperienceIds.let {
                mainViewModel.getExperiencesByIdsRemote(it).toMutableList()
            }
            setAdapter(savedExperiences.reversed())

            withContext(Dispatchers.Main) {
                setAdapter(savedExperiences.reversed())
            }
        }
    }

    private fun makeToast(toast: String) {
        Toast.makeText(requireContext(), toast, Toast.LENGTH_SHORT).show()
    }

    private fun setAdapter(savedExperiences: List<Experience>) {

        if (savedExperiences.isNotEmpty()) {
            adapter = SavedExperiencesAdapter(
                savedExperiences,
                languageCode,
                this@SavedExperiencesFragment
            )
            binding.savedExperiencesRecyclerView.adapter = adapter
            binding.savedExperiencesRecyclerView.layoutManager =
                LinearLayoutManager(requireContext())

            binding.savedExperiencesRecyclerView.visibility = View.VISIBLE
            binding.emptyListLayout.visibility = View.GONE
        } else {
            binding.savedExperiencesRecyclerView.visibility = View.GONE
            binding.emptyListLayout.visibility = View.VISIBLE
        }
    }

    override fun onExperienceSelected(experience: Experience) {
        mainViewModel.selectedExperience.value = experience
        findNavController().navigate(R.id.action_myProfileFragment_to_experience_navigation)
    }

    override fun removeExperience(experienceId: String) {
        profileViewModel.removeExperience(experienceId)
        savedExperiences.removeAll { it.id == experienceId }
        setAdapter(savedExperiences)
        makeToast(toast = getString(R.string.removed))
    }

}