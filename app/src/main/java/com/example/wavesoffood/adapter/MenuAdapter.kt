package com.example.wavesoffood.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.wavesoffood.DetailsActivity
import com.example.wavesoffood.Model.MenuItemModel
import com.example.wavesoffood.databinding.MenuItemBinding

class MenuAdapter(
    private val menuItems: List<MenuItemModel>,
    private val requireContext: Context,
) : RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val binding = MenuItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MenuViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return menuItems.size
    }

    inner class MenuViewHolder(private val binding: MenuItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    Log.d("MenuAdapter", "Item clicked at position: $position")
                    openDetailActivity(position)
                } else {
                    Log.d("MenuAdapter", "Invalid position: $position")
                }
            }
        }

        fun bind(position: Int) {
            binding.apply {
                val menuItem = menuItems[position]
                foodNameMenu.text = menuItem.foodName
                foodPriceMenu.text = menuItem.foodPrice
                val uri = Uri.parse(menuItem.foodImage)
                Glide.with(requireContext).load(uri).into(foodImageMenu)
            }
        }

        private fun openDetailActivity(position: Int) {
            val menuItem = menuItems[position]
            Log.d("MenuAdapter", "Opening DetailsActivity for: ${menuItem.foodName}, ${menuItem.foodPrice}, ${menuItem.foodDescription}, ${menuItem.foodIngredients}, ${menuItem.foodImage}")

            val intent = Intent(requireContext, DetailsActivity::class.java).apply {
                putExtra("MenuItemName", menuItem.foodName)
                putExtra("MenuItemPrice", menuItem.foodPrice)
                putExtra("MenuItemDescription", menuItem.foodDescription)
                putExtra("MenuItemIngredient", menuItem.foodIngredients)
                putExtra("MenuItemImageUrl", menuItem.foodImage)
            }

            Log.d("MenuAdapter", "Starting DetailsActivity with intent: $intent")
            requireContext.startActivity(intent)
        }
    }
}
