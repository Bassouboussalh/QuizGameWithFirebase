package com.boussalh.quizapp

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.boussalh.quizapp.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : AppCompatActivity() {
    private lateinit var loginBinding: ActivityLoginBinding
    var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loginBinding = ActivityLoginBinding.inflate(layoutInflater)
        val view = loginBinding.root
        setContentView(view)

        val textOfGoogleButton = loginBinding.buttonGoogleSignin.getChildAt(0) as TextView
        textOfGoogleButton.text = "Continue with Google"
        textOfGoogleButton.setTextColor(Color.BLACK)
        textOfGoogleButton.textSize = 18F

        //register
        registerActivityForGoogleSignIn()

        loginBinding.buttonSignin.setOnClickListener {
            val userEmail = loginBinding.editTextLoginEmail.text.toString()
            val userPassword = loginBinding.editTextLoginPassword.text.toString()
            signinUser(userEmail, userPassword)
        }

        loginBinding.buttonGoogleSignin.setOnClickListener {
            signInGoogle()
        }
        loginBinding.TextViewSignup.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }
        loginBinding.textViewForgotPassword.setOnClickListener {
            val intent = Intent(this@LoginActivity, ForgetPasswordActivity::class.java)
            startActivity(intent)
        }
    }

    fun signinUser(userEmail: String, userPassword: String) {
        auth.signInWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(applicationContext, "Welcome to Quiz Game", Toast.LENGTH_SHORT)
                    .show()
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(
                    applicationContext,
                    task.exception?.localizedMessage,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()

        val user = auth.currentUser
        if (user != null) {
            Toast.makeText(applicationContext, "Welcome to Quiz Game", Toast.LENGTH_SHORT).show()
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    fun signInGoogle() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("857370989318-tlpcqmu5k7cipiep1qirl6m47q52vmb8.apps.googleusercontent.com")
            .requestEmail().build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        signIn()
    }

    private fun signIn() {
        val signIntent: Intent = googleSignInClient.signInIntent
        activityResultLauncher.launch(signIntent)
    }

    private fun registerActivityForGoogleSignIn() {
        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult(),
                ActivityResultCallback { result ->
                    val resultCode = result.resultCode
                    val data = result.data

                    if (resultCode == RESULT_OK && data != null) {
                        val task: Task<GoogleSignInAccount> =
                            GoogleSignIn.getSignedInAccountFromIntent(data)
                        firebaseSignInWithGoogle(task)
                    }
                })
    }

    private fun firebaseSignInWithGoogle(task: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount = task.getResult(ApiException::class.java)
            Toast.makeText(applicationContext, "Welcome to Quiz Game", Toast.LENGTH_SHORT).show()
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
            firebaseGoogleAccount(account)
        } catch (e: ApiException) {
            Toast.makeText(applicationContext, e.localizedMessage, Toast.LENGTH_SHORT).show()
        }
    }

    fun firebaseGoogleAccount(account: GoogleSignInAccount) {
        val authCredential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(authCredential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                //val user = auth.currentUser
            } else {

            }
        }
    }
}