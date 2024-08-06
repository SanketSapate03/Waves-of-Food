package com.example.wavesoffood.fragment

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.wavesoffood.Model.OrderDetailsModel
import com.example.wavesoffood.RecentOrderItem
import com.example.wavesoffood.adapter.BuyAgainAdapter
import com.example.wavesoffood.databinding.FragmentHistoryBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.io.Serializable

class HistoryFragment : Fragment() {
    private lateinit var binding: FragmentHistoryBinding
    private lateinit var buyAgainAdapter: BuyAgainAdapter

    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private lateinit var userId: String
    private var listOfOrderItems: MutableList<OrderDetailsModel> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHistoryBinding.inflate(layoutInflater, container, false)

        //initialize
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        //retrieve and display user order details
        retrieveBuyHistory()

        //recent buy button click
        binding.recentButyItem.setOnClickListener {
            seeRecentBuyItem()
        }

        binding.receivedButton.setOnClickListener {
           updateOrderStatus()
        }

        return binding.root
    }

    private fun updateOrderStatus() {
        val itemPushKey=listOfOrderItems[0].itemPushKey
        val completeOrderRef=database.reference.child("CompletedOrder").child(itemPushKey!!)
        completeOrderRef.child("paymentReceived").setValue(true)
    }

    //to see recent item
    private fun seeRecentBuyItem() {
        listOfOrderItems.firstOrNull()?.let { recentBuy ->
            val intent = Intent(requireContext(), RecentOrderItem::class.java)
            intent.putExtra("RecentBuyOrderItem", listOfOrderItems as Serializable)
            // Add logs to verify the data being passed
            Log.d("HistoryFragment", "Passing data: $listOfOrderItems")
            startActivity(intent)
        }
    }

    //to retrieve buy history
    private fun retrieveBuyHistory() {
        binding.recentButyItem.visibility = View.INVISIBLE
        userId = auth.currentUser?.uid ?: ""

        val buyItemRef = database.reference.child("user").child(userId).child("BuyHistory")
        val sortQuery = buyItemRef.orderByChild("currentTime")
        sortQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (buySnapshot in snapshot.children) {
                    val buyHistoryItem = buySnapshot.getValue(OrderDetailsModel::class.java)
                    buyHistoryItem?.let {
                        listOfOrderItems.add(it)
                    }

                    listOfOrderItems.reverse()
                    if (listOfOrderItems.isNotEmpty()) {
                        setDataInRecentBuyItem()
                        setPreviouslyBuyItemsRecyclerview()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("HistoryFragment", "Database error: $error")
            }
        })
    }

    //display most recent order details
    private fun setDataInRecentBuyItem() {
        binding.recentButyItem.visibility = View.VISIBLE
        val recentOrderItem = listOfOrderItems.firstOrNull()
        recentOrderItem?.let {
            with(binding) {
                buyAgainItemName.text = it.foodNames?.firstOrNull() ?: ""
                buyAgainItemPrice.text = it.foodPrices?.firstOrNull() ?: ""
                val image = it.foodImages?.firstOrNull() ?: ""
                val uri = Uri.parse(image)
                Glide.with(requireContext()).load(uri).into(buyAgainItemImage)
                 val isOrderAccepted= listOfOrderItems[0].orderAccepted
                if(isOrderAccepted == true){
                    orderStatus.background.setTint(Color.GREEN)
                    receivedButton.visibility= View.VISIBLE
                }

                //reverse the list of order items
                listOfOrderItems.reverse()
            }
        }
    }

    //set up recyclerview with previous order details
    private fun setPreviouslyBuyItemsRecyclerview() {
        val buyAgainFoodName = mutableListOf<String>()
        val buyAgainFoodPrice = mutableListOf<String>()
        val buyAgainFoodImage = mutableListOf<String>()

        for (i in 1 until listOfOrderItems.size) {
            listOfOrderItems[i].foodNames?.firstOrNull()?.let { buyAgainFoodName.add(it) }
            listOfOrderItems[i].foodPrices?.firstOrNull()?.let { buyAgainFoodPrice.add(it) }
            listOfOrderItems[i].foodImages?.firstOrNull()?.let { buyAgainFoodImage.add(it) }
        }

        val rv = binding.historyRecyclerView
        rv.layoutManager = LinearLayoutManager(requireContext())
        buyAgainAdapter = BuyAgainAdapter(
            buyAgainFoodName,
            buyAgainFoodPrice,
            buyAgainFoodImage,
            requireContext()
        )
        rv.adapter = buyAgainAdapter
    }
}
