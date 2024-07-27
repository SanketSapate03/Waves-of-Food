package com.example.wavesoffood.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wavesoffood.R
import com.example.wavesoffood.adapter.MenuAdapter
import com.example.wavesoffood.databinding.FragmentSearchBinding

class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private lateinit var adapter: MenuAdapter

    private val originalFoodName = mutableListOf("Burger", "Samosa", "Kachori", "Poha","Burger", "Samosa", "Kachori", "Poha")
    private val originalFoodPrice = mutableListOf("$5", "$7", "$10", "$10","$5", "$7", "$10", "$10")
    private val originalFoodImage = mutableListOf(
        R.drawable.spicy_fresh_crab1,
        R.drawable.spicy_fresh_crab2,
        R.drawable.spicy_fresh_crab3,
        R.drawable.green_noddle,
        R.drawable.spicy_fresh_crab1,
        R.drawable.spicy_fresh_crab2,
        R.drawable.spicy_fresh_crab3,
        R.drawable.green_noddle,
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    private val filterMenuFoodName = mutableListOf<String>()
    private val filterMenuFoodPrice = mutableListOf<String>()
    private val filterMenuFoodImage = mutableListOf<Int>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)

       /* adapter = MenuAdapter(
            filterMenuFoodName, filterMenuFoodPrice, filterMenuFoodImage,requireContext()
        )*/
        binding.menuRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.menuRecyclerView.adapter = adapter

        //set up for search view
        setupSearchView()

        //show all menu items
        showAllMenu()


        return binding.root

    }

    private fun showAllMenu() {
        filterMenuFoodName.clear()
        filterMenuFoodPrice.clear()
        filterMenuFoodImage.clear()

        filterMenuFoodName.addAll(originalFoodName)
        filterMenuFoodPrice.addAll(originalFoodPrice)
        filterMenuFoodImage.addAll(originalFoodImage)

        adapter.notifyDataSetChanged()
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                filterMenuItems(query)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                filterMenuItems(newText)
                return true
            }

        })
    }

    private fun filterMenuItems(query: String) {
        filterMenuFoodName.clear()
        filterMenuFoodPrice.clear()
        filterMenuFoodImage.clear()

        originalFoodName.forEachIndexed { index, foodName ->
            if (foodName.contains(query, ignoreCase = true)) {
                filterMenuFoodName.add(foodName)
                filterMenuFoodPrice.add(originalFoodPrice[index])
                filterMenuFoodImage.add(originalFoodImage[index])
            }
        }
        adapter.notifyDataSetChanged()
    }

    companion object {

    }
}
