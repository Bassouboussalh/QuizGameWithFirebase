package com.boussalh.quizapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.boussalh.quizapp.databinding.ActivityForgetPasswordBinding
import com.google.firebase.auth.FirebaseAuth

class ForgetPasswordActivity : AppCompatActivity() {
    private lateinit var forgetPasswordBinding: ActivityForgetPasswordBinding
    val auth : FirebaseAuth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        forgetPasswordBinding = ActivityForgetPasswordBinding.inflate(layoutInflater)
        val view = forgetPasswordBinding.root
        setContentView(view)

        forgetPasswordBinding.buttonReset.setOnClickListener {
            val userEmail = forgetPasswordBinding.editTextForgotEmail.text.toString()
            auth.sendPasswordResetEmail(userEmail).addOnCompleteListener { task ->
                if (task.isSuccessful){
                    Toast.makeText(applicationContext,"We send a password reset to your email adress",Toast.LENGTH_SHORT).show()
                    finish()
                }else{
                    Toast.makeText(applicationContext,task.exception?.localizedMessage,Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}