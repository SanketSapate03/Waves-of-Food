package com.example.wavesoffood.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.wavesoffood.databinding.CartItemBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CartAdapter(
    private val context: Context,
    private val cartItemsName: MutableList<String>,
    private val cartItemPrice: MutableList<String>,
    private val cartItemDescription: MutableList<String>,
    private val cartItemImage: MutableList<String>,
    private val cartItemIngredient: MutableList<String>,
    private val cartItemQuantity: MutableList<Int>,
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    //initialise firebase
    private val auth = FirebaseAuth.getInstance()

    init {
        val database = FirebaseDatabase.getInstance()
        val userId = auth.currentUser?.uid ?: ""
        val cartItemNumber = cartItemsName.size

        itemQuantity = IntArray(cartItemNumber) { 1 }
        cartItemReference = database.reference.child("user").child(userId).child("CartItems")
    }

    companion object {
        private var itemQuantity = intArrayOf()
        private lateinit var cartItemReference: DatabaseReference
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = CartItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return cartItemsName.size
    }

    //get updated quantity
    fun getUpdatedItemsQuantities(): MutableList<Int> {
        val itemQuantity = mutableListOf<Int>()
        itemQuantity.addAll(cartItemQuantity)
        return itemQuantity
    }

    inner class CartViewHolder(private val binding: CartItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.apply {
                val quantity = itemQuantity[position]
                cartFoodName.text = cartItemsName[position]
                cartFoodPrice.text = cartItemPrice[position]

                val uriString = cartItemImage[position]
                val uri = Uri.parse(uriString)
                Glide.with(context).load(uri).into(cartFoodImage)

                cartIFoodQuantity.text = quantity.toString()

                minusBtn.setOnClickListener {
                    decQuantity(position)
                }

                plusBtn.setOnClickListener {
                    incQuantity(position)
                }

                deleteBtn.setOnClickListener {
                    val itemPosition = adapterPosition
                    if (itemPosition != RecyclerView.NO_POSITION) {
                        deleteItem(itemPosition)
                    }

                }


            }
        }

        private fun decQuantity(position: Int) {
            if (itemQuantity[position] > 1) {
                itemQuantity[position]--
                cartItemQuantity[position]= itemQuantity[position]
                binding.cartIFoodQuantity.text = itemQuantity[position].toString()
            }
        }

        private fun incQuantity(position: Int) {
            if (itemQuantity[position] < 10) {
                itemQuantity[position]++
                cartItemQuantity[position]= itemQuantity[position]
                binding.cartIFoodQuantity.text = itemQuantity[position].toString()
            }
        }

        private fun deleteItem(position: Int) {
            val positionRetrieve = position
            getUniqueKeyAtPosition(positionRetrieve) { uniqueKey ->
                if (uniqueKey != null) {
                    removeItem(position, uniqueKey)
                }
            }
            cartItemReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var uniqueKey: String? = null
                    //loop for snapshot children
                    snapshot.children.forEachIndexed { index, dataSnapshot ->
                        if (index == positionRetrieve) {
                            uniqueKey = dataSnapshot.key
                            return@forEachIndexed
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        }

        private fun removeItem(position: Int, uniqueKey: String) {
            if (uniqueKey != null) {
                cartItemReference.child(uniqueKey).removeValue()

                    .addOnSuccessListener {
                        cartItemsName.removeAt(position)
                        cartItemPrice.removeAt(position)
                        cartItemImage.removeAt(position)
                        cartItemDescription.removeAt(position)
                        cartItemQuantity.removeAt(position)
                        cartItemIngredient.removeAt(position)

                        Toast.makeText(context, "Item Deleted Successfully", Toast.LENGTH_SHORT)
                            .show()

                        //update the item quantity
                        itemQuantity = itemQuantity.filterIndexed { index, i -> index != position }
                            .toIntArray()
                        notifyItemRemoved(position)
                        notifyItemRangeChanged(position, cartItemsName.size)
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, "Failed to delete", Toast.LENGTH_SHORT).show()
                    }
            }

        }

        private fun getUniqueKeyAtPosition(positionRetrieve: Int, onComplete: (String?) -> Unit) {
            cartItemReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var uniqueKey: String? = null
                    snapshot.children.forEachIndexed { index, dataSnpshot ->
                        if (index == positionRetrieve) {
                            uniqueKey = dataSnpshot.key
                            return@forEachIndexed
                        }
                    }
                    onComplete(uniqueKey)
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
        }

    }


}


