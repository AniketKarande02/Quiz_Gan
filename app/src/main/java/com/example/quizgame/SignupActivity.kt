package com.example.quizgame

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Email
import android.view.View
import android.widget.Toast
import com.example.quizgame.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth

class SignupActivity : AppCompatActivity() {

    lateinit var  signupBinding: ActivitySignupBinding
    val auth : FirebaseAuth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        signupBinding = ActivitySignupBinding.inflate(layoutInflater)
        val view = signupBinding.root

        setContentView(view)

        signupBinding.buttonSignup.setOnClickListener {

            val email = signupBinding.editTextSignupEmail.text.toString()
            val password = signupBinding.editTextLoginSignupPassword.text.toString()


            signInWithFirebase(email,password)

        }
    }
    fun signInWithFirebase(email: String,password:String){

        signupBinding.progressBarsignup.visibility = View.VISIBLE

        signupBinding.buttonSignup.isClickable = false

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task->

            if(task.isSuccessful){
                Toast.makeText(applicationContext,"Your account has been created",Toast.LENGTH_SHORT).show()
                finish()

                signupBinding.progressBarsignup.visibility = View.INVISIBLE
                signupBinding.buttonSignup.isClickable = true

            }else{
                Toast.makeText(applicationContext,task.exception?.localizedMessage,Toast.LENGTH_SHORT).show()
            }

        }
    }
}