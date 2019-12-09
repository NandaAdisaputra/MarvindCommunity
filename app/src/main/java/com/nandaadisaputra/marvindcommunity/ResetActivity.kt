package com.nandaadisaputra.marvindcommunity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.toast

class ResetActivity : AppCompatActivity() {
    private val TAG = "ResetPassword"
    private var etEmail: EditText? = null
    private var btnSubmit: Button? = null
    private var mAuth: FirebaseAuth? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset)
        initialise()
    }
    private fun initialise() {
        etEmail = findViewById<View>(R.id.edt_reset_email) as EditText
        btnSubmit = findViewById<View>(R.id.btn_reset_password) as Button
        mAuth = FirebaseAuth.getInstance()
        btnSubmit?.onClick { sendPasswordResetEmail() }
    }
    private fun sendPasswordResetEmail() {
        val email = etEmail?.text.toString()
        if (!TextUtils.isEmpty(email)) {
            mAuth!!
                .sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val message = "Email sent."
                        Log.d(TAG, message)
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                        updateUI()
                    } else {
                        Log.w(TAG, task.exception?.message)
                        toast("No user found with this email.")
                    }
                }
        } else {
            toast("Enter Email")
        }
    }
    private fun updateUI() {
        val intent = Intent(this@ResetActivity, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }
}