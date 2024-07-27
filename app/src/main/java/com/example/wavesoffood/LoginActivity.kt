package com.example.wavesoffood

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.wavesoffood.Model.UserModel
import com.example.wavesoffood.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private  var name:String?=null
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var googleSignInClient: GoogleSignInClient


    private val binding: ActivityLoginBinding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("842517839325-s4pt3t3pdri0ubv787nslu6f1441t999.apps.googleusercontent.com").requestEmail().build()
        auth = Firebase.auth
        database = Firebase.database.reference
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)


        //set on click listner on login button
        binding.loginBtn.setOnClickListener {

            email = binding.editTextTextEmailAddress.text.toString().trim()
            password = binding.editTextTextPassword.text.toString().trim()

            if ((email.isBlank() || password.isBlank())) {
                Toast.makeText(this, "Please Fill all details", Toast.LENGTH_SHORT).show()
            } else {
                createUserAccount(email,password)
                Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
            }


        }
        binding.dontHaveBtn.setOnClickListener {
            val intent = Intent(this, SigninActivity::class.java)
            startActivity(intent)
        }

        //click listner on google button
        binding.googleLoginBtn.setOnClickListener {
            val signIntent = googleSignInClient.signInIntent
            launcher.launch(signIntent)
        }

    }

    //launcher for sign in
    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                if (task.isSuccessful) {
                    val account = task.result
                    val credentials = GoogleAuthProvider.getCredential(account.idToken, null)
                    auth.signInWithCredential(credentials).addOnCompleteListener { authTask ->
                        if (authTask.isSuccessful) {
                            //open new activity
                            Toast.makeText(
                                this, "Login Successful With Google 👍", Toast.LENGTH_SHORT
                            ).show()
                            updateUi(authTask.result?.user)
                            finish()
                        } else {
                            Toast.makeText(this, "Google SignIn Failed 😒", Toast.LENGTH_SHORT)
                                .show()
                            Log.d("Accounts", "error due:", authTask.exception)
                        }
                    }
                } else {
                    Toast.makeText(this, "Google SignIn Failed 😒", Toast.LENGTH_SHORT).show()
                    Log.d("Account", "error due:", task.exception)
                }
            }
        }


    //sign in or login if not then create account
    private fun createUserAccount(email: String, password: String) {

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user: FirebaseUser? = auth.currentUser
                Toast.makeText(this, "Login Successful 👍", Toast.LENGTH_SHORT).show()
                updateUi(user)
            } else {
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { t1 ->
                    if (t1.isSuccessful) {
                        val user: FirebaseUser? = auth.currentUser
                        Toast.makeText(
                            this, "Create User and Login Successful 👍", Toast.LENGTH_SHORT
                        ).show()
                        saveUserData()
                        updateUi(user)
                    } else {
                        Toast.makeText(this, "Login Failed 😒", Toast.LENGTH_SHORT).show()
                        Log.d("CreateUserAccount", "Authentication Failed:", t1.exception)
                    }
                }
            }
        }
    }

    //for save user data
    private fun saveUserData() {
        //get data from edit text
        email = binding.editTextTextEmailAddress.text.toString().trim()
        password = binding.editTextTextPassword.text.toString().trim()

        //save info
        val user = UserModel(name, email, password)
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        userId?.let {
            database.child("user").child(it).setValue(user)
        }
    }

    private fun updateUi(user: FirebaseUser?) {
        val intent = Intent(this, ChooseLocationActivity::class.java)
        startActivity(intent)
        finish()
    }
}