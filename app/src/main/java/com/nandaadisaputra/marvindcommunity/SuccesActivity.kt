package com.nandaadisaputra.marvindcommunity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_succes.*

class SuccesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_success)

        setupUI()
    }

    private fun setupUI() {
        sign_out_button.setOnClickListener{
            signOut()
        }
    }

    private fun signOut() {
        startActivity(MainActivity.getLaunchIntent(this))
        FirebaseAuth.getInstance().signOut()
    }

    companion object {
        fun getLaunchIntent(from: Context) = Intent(from, SuccesActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
    }
}