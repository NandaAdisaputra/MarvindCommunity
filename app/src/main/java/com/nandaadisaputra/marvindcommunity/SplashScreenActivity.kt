package com.nandaadisaputra.marvindcommunity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity

class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        //Tambahkan jeda intent ke RegisterActivity
        Handler().postDelayed({
            finish()
            startActivity(
                Intent(this@SplashScreenActivity,
                RegisterActivity::class.java)
            )
        }, 3000)

    }
}
