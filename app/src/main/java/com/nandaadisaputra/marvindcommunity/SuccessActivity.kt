package com.nandaadisaputra.marvindcommunity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_success.*
import org.jetbrains.anko.*

class SuccessActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var sharedPrefManager: SharedPrefManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_success)

        //panggil kelas SharedPrefManager
        sharedPrefManager = SharedPrefManager(this)

    }
    override fun onClick(v: View?) {
        when (v) {
            sign_out_button -> {

                //kita tambahkan alertdialog
                alert("Apakah anda ingin logout ?") {
                    noButton {
                        toast("Anda tidak jadi Keluar")
                        startActivity(intentFor<LoginActivity>())
                        finish()
                    }
                    yesButton {
                        if (sharedPrefManager.sPSudahLogin) {
                            sharedPrefManager.saveSPBoolean(
                                SharedPrefManager.SP_SUDAH_LOGIN,
                                false
                            )
                            startActivity(
                                Intent(this@SuccessActivity, LoginActivity::class.java)
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                            )
                            super.onBackPressed()
                        } else {
                            toast("Anda Gagal Keluar")
                        }
                    }
                }.show()
            }
        }
    }

}