package com.example.wavesoffood.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wavesoffood.Model.MenuItemModel
import com.example.wavesoffood.adapter.MenuAdapter
import com.example.wavesoffood.databinding.FragmentSearchBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private lateinit var adapter: MenuAdapter
    private lateinit var database: FirebaseDatabase
    private val originalFoodItem = mutableListOf<MenuItemModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)

        //retrieve menu item from db
        retrieveMenuItem()

        //set up for search view
        setupSearchView()




        return binding.root

    }

    private fun retrieveMenuItem() {
        database = FirebaseDatabase.getInstance()
        //reference to the menu node
        val foodRef: DatabaseReference = database.reference.child("menu")
        foodRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (foodSnapshot in snapshot.children) {
                    val menuItem = foodSnapshot.getValue(MenuItemModel::class.java)
                    menuItem?.let { originalFoodItem.add(it) }
                }
                showAllMenu()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun showAllMenu() {
        val filterMenuItem = ArrayList(originalFoodItem)
        setAdapter(filterMenuItem)
    }

    private fun setAdapter(filterMenuItem: List<MenuItemModel>) {
        adapter = MenuAdapter(filterMenuItem, requireContext())
        binding.menuRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.menuRecyclerView.adapter = adapter
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
        val filteredMenuItem = originalFoodItem.filter { it ->
            it.foodName?.contains(query, ignoreCase = true) == true
        }
        setAdapter(filteredMenuItem)
    }

}


