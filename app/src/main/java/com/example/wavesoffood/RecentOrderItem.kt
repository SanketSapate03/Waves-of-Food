package com.example.wavesoffood

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wavesoffood.Model.OrderDetailsModel
import com.example.wavesoffood.adapter.RecentBuyAdapter
import com.example.wavesoffood.databinding.ActivityRecentOrderItemBinding

class RecentOrderItem : AppCompatActivity() {
    private val binding: ActivityRecentOrderItemBinding by lazy {
        ActivityRecentOrderItemBinding.inflate(layoutInflater)
    }

    private lateinit var allFoodNames: ArrayList<String>
    private lateinit var allFoodPrices: ArrayList<String>
    private lateinit var allFoodImages: ArrayList<String>
    private lateinit var allFoodQuantities: ArrayList<Int>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.recentBuyBackBtn.setOnClickListener {
            finish()
        }

        val recentOrderItems = intent.getSerializableExtra("RecentBuyOrderItem") as? ArrayList<OrderDetailsModel>
        recentOrderItems?.let { orderDetails ->
            Log.d("RecentOrderItem", "Received data: $orderDetails")
            if (orderDetails.isNotEmpty()) {
                val recentOrderItem = orderDetails[orderDetails.size-1]
                allFoodNames = recentOrderItem.foodNames as ArrayList<String>
                allFoodPrices = recentOrderItem.foodPrices as ArrayList<String>
                allFoodImages = recentOrderItem.foodImages as ArrayList<String>
                allFoodQuantities = recentOrderItem.foodQuantities as ArrayList<Int>
            }
        } ?: run {
            Log.e("RecentOrderItem", "No data received")
        }

        setAdapter()
    }

    private fun setAdapter() {
        val rv = binding.recyclerView
        rv.layoutManager = LinearLayoutManager(this)
        val adapter = RecentBuyAdapter(this, allFoodNames, allFoodPrices, allFoodImages, allFoodQuantities)
        rv.adapter = adapter

        Log.d("RecentOrderItem", "Adapter set with: $allFoodNames, $allFoodPrices, $allFoodImages, $allFoodQuantities")
    }
}
