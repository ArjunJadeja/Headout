package com.arjun.headout.ui.home

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arjun.headout.BuildConfig
import com.arjun.headout.R
import com.arjun.headout.data.local.preferences.DEFAULT_PREFERENCE
import com.arjun.headout.data.local.preferences.PreferencesHelper
import com.arjun.headout.data.model.City
import com.arjun.headout.data.model.Experience
import com.arjun.headout.data.model.Subcategory
import com.arjun.headout.databinding.FragmentHomeBinding
import com.arjun.headout.ui.MainViewModel
import com.arjun.headout.ui.home.adapters.CategoriesAdapter
import com.arjun.headout.ui.home.adapters.CategoryInterface
import com.zoho.livechat.android.ZohoLiveChat
import com.zoho.salesiqembed.ZohoSalesIQ
import kotlinx.coroutines.launch

class HomeFragment : Fragment(R.layout.fragment_home), CategoryInterface {

    private lateinit var binding: FragmentHomeBinding

    private val mainViewModel: MainViewModel by activityViewModels()

    private var cityId = DEFAULT_PREFERENCE
    private var languageCode = DEFAULT_PREFERENCE
    private var dataSaved = DEFAULT_PREFERENCE
    private var videoSaved = DEFAULT_PREFERENCE

    private lateinit var adapter: CategoriesAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)

        lifecycleScope.launch {
            cityId = mainViewModel.readData(PreferencesHelper.SELECTED_CITY_ID, DEFAULT_PREFERENCE)
            languageCode =
                mainViewModel.readData(PreferencesHelper.SELECTED_LANGUAGE_CODE, DEFAULT_PREFERENCE)
            dataSaved =
                mainViewModel.readData(PreferencesHelper.LOCAL_DATA_SAVED, DEFAULT_PREFERENCE)
            videoSaved =
                mainViewModel.readData(PreferencesHelper.LOCAL_VIDEO_SAVED, DEFAULT_PREFERENCE)

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

            ZohoSalesIQ.init(
                requireActivity().application,
                BuildConfig.ZOHO_APP_KEY,
                BuildConfig.ZOHO_ACCESS_KEY
            )

            ZohoSalesIQ.registerVisitor(
                mainViewModel.readData(
                    PreferencesHelper.USER_ID, DEFAULT_PREFERENCE
                )
            )
            ZohoLiveChat.registerVisitor(
                mainViewModel.readData(
                    PreferencesHelper.USER_ID, DEFAULT_PREFERENCE
                )
            )

        }
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            requireActivity().finishAffinity()
        }
    }

    private fun loadData(city: City?) {
        if (city == null || city.name?.get(languageCode).isNullOrEmpty()) {
            binding.nestedRecyclerview.visibility = View.GONE
            binding.emptyListLayout.visibility = View.VISIBLE
        } else {
            binding.nestedRecyclerview.visibility = View.VISIBLE
            binding.emptyListLayout.visibility = View.GONE

            binding.titleTextView.text = city.name?.get(languageCode) ?: getString(R.string.headout)

            val subcategoryList = city.categories?.flatMap { category ->
                category?.subcategories.orEmpty()
            }!!.mapNotNull { it }

            setAdapter(
                url = city.bannerUrl.toString(),
                subcategoryList = subcategoryList
            )
        }
    }

    private fun setAdapter(url: String, subcategoryList: List<Subcategory>) {
        adapter = CategoriesAdapter(
            context = requireContext(),
            url = url,
            itemList = subcategoryList,
            languageCode = languageCode,
            dataSaved = dataSaved,
            videoSaved = videoSaved,
            lifecycleScope = viewLifecycleOwner.lifecycleScope,
            mainViewModel = mainViewModel,
            listener = this@HomeFragment
        )
        binding.apply {
            nestedRecyclerview.adapter = adapter
            nestedRecyclerview.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

            nestedRecyclerview.addOnScrollListener(object :
                RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                    val headerViewHolder = recyclerView.findViewHolderForAdapterPosition(0)

                    if (headerViewHolder is CategoriesAdapter.HeaderViewHolder) {
                        if (firstVisibleItemPosition == 0) {
                            if (!headerViewHolder.videoView.isPlaying) {
                                headerViewHolder.videoView.start()
                            }
                        } else {
                            if (headerViewHolder.videoView.isPlaying) {
                                headerViewHolder.videoView.pause()
                            }
                        }
                    }
                }
            })
        }
    }

    private fun makeToast(toast: String) {
        Toast.makeText(requireContext(), toast, Toast.LENGTH_SHORT).show()
    }

    override fun onSeeAllClicked(subcategory: Subcategory) {
        mainViewModel.selectedSubcategory.value = subcategory
        findNavController().navigate(R.id.action_homeFragment_to_experiencesListFragment)
    }

    override fun onExperienceSelected(experience: Experience) {
        mainViewModel.selectedExperience.value = experience
        findNavController().navigate(R.id.action_homeFragment_to_experience_navigation)
    }

}