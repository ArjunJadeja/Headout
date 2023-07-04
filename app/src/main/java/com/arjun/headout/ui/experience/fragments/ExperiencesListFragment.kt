package com.arjun.headout.ui.experience.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.arjun.headout.R
import com.arjun.headout.data.local.preferences.DEFAULT_PREFERENCE
import com.arjun.headout.data.local.preferences.PreferencesHelper
import com.arjun.headout.data.model.Experience
import com.arjun.headout.data.model.Subcategory
import com.arjun.headout.databinding.FragmentExperiencesListBinding
import com.arjun.headout.ui.MainViewModel
import com.arjun.headout.ui.experience.adapters.ExperiencesListAdapter
import com.arjun.headout.ui.experience.adapters.IExperienceSelected
import kotlinx.coroutines.launch

class ExperiencesListFragment : Fragment(R.layout.fragment_experiences_list), IExperienceSelected {

    private lateinit var binding: FragmentExperiencesListBinding

    private val mainViewModel: MainViewModel by activityViewModels()

    private lateinit var adapter: ExperiencesListAdapter
    private var subcategory: Subcategory? = null

    private var languageCode = DEFAULT_PREFERENCE
    private var dataSaved = DEFAULT_PREFERENCE

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentExperiencesListBinding.bind(view)

        mainViewModel.selectedSubcategory.observe(viewLifecycleOwner) { subcategory ->
            if (subcategory == null) {
                binding.categoryListRecyclerView.visibility = View.GONE
                binding.emptyListLayout.visibility = View.VISIBLE
            } else {
                binding.categoryListRecyclerView.visibility = View.VISIBLE
                binding.emptyListLayout.visibility = View.GONE

                this.subcategory = subcategory
                setAdapter()
            }
        }

    }

    private fun setAdapter() {
        lifecycleScope.launch {

            languageCode =
                mainViewModel.readData(PreferencesHelper.SELECTED_LANGUAGE_CODE, DEFAULT_PREFERENCE)
            dataSaved =
                mainViewModel.readData(PreferencesHelper.LOCAL_DATA_SAVED, DEFAULT_PREFERENCE)

            binding.titleTextView.text =
                subcategory?.name?.get(languageCode) ?: getString(R.string.headout)

            val experienceIds = subcategory?.experiences?.mapNotNull { it } ?: emptyList()

            val experiences = if (dataSaved == "true") {
                mainViewModel.getExperiencesByIdsLocally(experienceIds = experienceIds)
            } else {
                mainViewModel.getExperiencesByIdsRemote(experienceIds = experienceIds)
            }

            adapter =
                ExperiencesListAdapter(experiences, languageCode, this@ExperiencesListFragment)
            binding.categoryListRecyclerView.adapter = adapter

            val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            binding.categoryListRecyclerView.layoutManager = layoutManager
        }
    }

    override fun onExperienceSelected(experience: Experience) {
        mainViewModel.selectedExperience.value = experience
        findNavController().navigate(R.id.action_experiencesListFragment_to_experience_navigation)
    }

}