package com.example.wavesoffood.fragment

import android.os.Bundle
import android.os.UserHandle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.wavesoffood.Model.UserModel
import com.example.wavesoffood.R
import com.example.wavesoffood.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProfileFragment : Fragment() {
    private lateinit var binding:FragmentProfileBinding

   private val auth=FirebaseAuth.getInstance()
    private val database=FirebaseDatabase.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentProfileBinding.inflate(inflater,container,false)

        setUserData()

        binding.saveInformation.setOnClickListener {
            val name =binding.name.text.toString()
            val email =binding.email.text.toString()
            val address =binding.address.text.toString()
            val phone =binding.phone.text.toString()

            updateUserData(name,email,address,phone)

        }

        return binding.root

    }

    private fun updateUserData(name: String, email: String, address: String, phone: String) {
        val userID=auth.currentUser?.uid
        if(userID!=null){
            val userRef=database.getReference("user").child(userID)
            val userData = hashMapOf(
                "name" to name,
                "address" to address,
                "email" to email,
                "phone" to phone
            )
            userRef.setValue(userData).addOnSuccessListener {
                Toast.makeText(requireContext(), "Profile Updated Successfully", Toast.LENGTH_SHORT).show()
            }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Profile update failed", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun setUserData() {
        val userId=auth.currentUser?.uid
        if(userId!=null){
            val userRef=database.getReference("user").child(userId)
            userRef.addListenerForSingleValueEvent(object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        val userProfile=snapshot.getValue(UserModel::class.java)
                        if(userProfile!=null){

                            binding.name.setText(userProfile.name)
                            binding.address.setText(userProfile.address)
                            binding.email.setText(userProfile.email)
                            binding.phone.setText(userProfile.phone)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        }

    }


}