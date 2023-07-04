package com.arjun.headout.ui.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.arjun.headout.R
import com.arjun.headout.data.local.preferences.DEFAULT_PREFERENCE
import com.arjun.headout.data.local.preferences.PreferencesHelper
import com.arjun.headout.data.model.City
import com.arjun.headout.data.model.Experience
import com.arjun.headout.data.model.Subcategory
import com.arjun.headout.databinding.FragmentSearchBinding
import com.arjun.headout.ui.MainViewModel
import com.arjun.headout.ui.search.adapters.ISearchedExperienceListener
import com.arjun.headout.ui.search.adapters.SearchCategoryInterface
import com.arjun.headout.ui.search.adapters.SearchedExperiencesAdapter
import com.arjun.headout.ui.search.adapters.SubCategoriesAdapter
import kotlinx.coroutines.launch
import java.util.Locale

class SearchFragment : Fragment(R.layout.fragment_search), SearchCategoryInterface,
    ISearchedExperienceListener {

    private lateinit var binding: FragmentSearchBinding

    private val mainViewModel: MainViewModel by activityViewModels()

    private var cityId = DEFAULT_PREFERENCE
    private var languageCode = DEFAULT_PREFERENCE
    private var dataSaved = DEFAULT_PREFERENCE

    private var localExperiences: List<Experience>? = null

    private lateinit var subCategoriesAdapter: SubCategoriesAdapter
    private lateinit var searchAdapter: SearchedExperiencesAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSearchBinding.bind(view)

        lifecycleScope.launch {
            cityId = mainViewModel.readData(PreferencesHelper.SELECTED_CITY_ID, DEFAULT_PREFERENCE)
            languageCode =
                mainViewModel.readData(PreferencesHelper.SELECTED_LANGUAGE_CODE, DEFAULT_PREFERENCE)
            dataSaved =
                mainViewModel.readData(PreferencesHelper.LOCAL_DATA_SAVED, DEFAULT_PREFERENCE)

            if (dataSaved == "true") {
                val city = mainViewModel.getCityLocally(cityId = cityId)
                loadData(city = city)
            } else {
                val city = mainViewModel.getCityRemote(cityId = cityId)
                loadData(city = city)
                mainViewModel.fetchAndSaveDataFromRemote(
                    context = requireActivity(),
                    cityId = cityId
                )
            }

            localExperiences = mainViewModel.getExperiencesLocally()

        }

        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // This method is called before the text is changed.
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // This method is called when the text is being changed.
            }

            override fun afterTextChanged(s: Editable?) {
                val searchText = s.toString().trim().lowercase(Locale.ROOT)

                if (searchText.isEmpty()) {
                    binding.searchedExperiencesRecyclerView.visibility = View.GONE
                    binding.emptyListLayout.visibility = View.GONE
                    binding.categoryRecyclerView.visibility = View.VISIBLE
                } else {
                    binding.categoryRecyclerView.visibility = View.GONE
                    binding.searchedExperiencesRecyclerView.visibility = View.GONE
                    binding.emptyListLayout.visibility = View.VISIBLE

                    val expTitles = mutableSetOf<String?>()
                    val filteredList = localExperiences?.filter { experience ->
                        val titleMatch = experience.title?.values?.any {
                            it?.lowercase(Locale.ROOT)?.contains(searchText) == true
                        } == true

                        val isNewTitle = experience.title?.get("en") !in expTitles

                        if (titleMatch && isNewTitle) {
                            expTitles.add(experience.title?.get("en"))
                            true
                        } else {
                            false
                        }
                    }

                    if (filteredList?.isNotEmpty() == true) {
                        setSearchAdapter(filteredList)
                    }
                }
            }
        })

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            findNavController().apply {
                popBackStack(R.id.home_navigation, true)
                navigate(R.id.home_navigation)
            }
        }

    }

    private fun loadData(city: City?) {
        if (city == null || city.name?.get(languageCode).isNullOrEmpty()) {
            binding.categoryRecyclerView.visibility = View.GONE
            binding.emptyListLayout.visibility = View.VISIBLE
        } else {
            binding.categoryRecyclerView.visibility = View.VISIBLE
            binding.emptyListLayout.visibility = View.GONE

            val subcategoryList = city.categories?.flatMap { category ->
                category?.subcategories.orEmpty()
            }!!.mapNotNull { it }

            subCategoriesAdapter = SubCategoriesAdapter(
                subcategoryList,
                languageCode,
                this@SearchFragment
            )
            binding.categoryRecyclerView.adapter = subCategoriesAdapter
            binding.categoryRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        }
    }

    private fun setSearchAdapter(experienceList: List<Experience>) {
        searchAdapter =
            SearchedExperiencesAdapter(experienceList, languageCode, this@SearchFragment)

        binding.searchedExperiencesRecyclerView.adapter = searchAdapter
        binding.searchedExperiencesRecyclerView.layoutManager =
            LinearLayoutManager(requireContext())

        binding.emptyListLayout.visibility = View.GONE
        binding.searchedExperiencesRecyclerView.visibility = View.VISIBLE
    }

    override fun onItemClicked(subcategory: Subcategory) {
        mainViewModel.selectedSubcategory.value = subcategory
        findNavController().navigate(R.id.action_searchFragment_to_experiencesListFragment)
    }

    override fun onExperienceClicked(experience: Experience) {
        mainViewModel.selectedExperience.value = experience
        findNavController().navigate(R.id.action_searchFragment_to_experience_navigation)
    }

}