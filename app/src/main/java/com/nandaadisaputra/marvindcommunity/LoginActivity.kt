package com.nandaadisaputra.marvindcommunity

import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.View
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.layout_login.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private var sharedPrefManager: SharedPrefManager? = null
    private var db: SQLiteDatabase? = null
    private var openHelper: SQLiteOpenHelper? = null
    private var cursor: Cursor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //panggil kelas SharedPrefManager
        sharedPrefManager = SharedPrefManager(this)

        //tambahkan animasi
        val img: RelativeLayout = findViewById<View>(R.id.anim) as RelativeLayout
        img.setBackgroundResource(R.drawable.bg_gradient0)
        val frameAnimation = img.background as AnimationDrawable
        frameAnimation.setEnterFadeDuration(2000)
        frameAnimation.setExitFadeDuration(4000)
        frameAnimation.start()

        //cek sudah login atau belum
        if (sharedPrefManager?.sPSudahLogin!!) {
            startActivity(
                Intent(this@LoginActivity, SuccessActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            )
            super.onBackPressed()
        }
        //panggil kelas DatabaseHelper
        openHelper = DatabaseHelper(this)
        db = openHelper?.readableDatabase

        //lambda kita kasih aksi ketika button login di klik
        btn_login.onClick {
            val email = edt_email.text.toString().trim()
            val password = edt_password.text.toString().trim()
            sharedPrefManager?.saveSPString(SharedPrefManager.COL_2, email)
            sharedPrefManager?.saveSPString(SharedPrefManager.COL_3, password)
            if (email.isEmpty() || password.isEmpty()) {
                if (validation()) {
                    return@onClick
                }
            } else {
                cursor = db?.rawQuery(
                    "SELECT *FROM " + DatabaseHelper.TABLE_NAME + " WHERE " + DatabaseHelper.COL_2 + "=? AND " + DatabaseHelper.COL_3 + "=?",
                    arrayOf(email, password)
                )
                if (cursor != null) {
                    if (cursor!!.count > 0) {
                        startActivity<SuccessActivity>()
                        sharedPrefManager?.saveSPBoolean(SharedPrefManager.SP_SUDAH_LOGIN, true)
                        toast("Login Success")
                    } else {
                        toast("Username and password do not match")
                    }

                }

            }
        }

    }

    private fun validation(): Boolean {
        when {
            //Check email is empty or not
            edt_email.text.toString().isBlank() -> {
                edt_email.requestFocus()
                edt_email.error = "Email cannot be empty"
                return false
            }
            //Check the password is empty or not
            edt_password.text.toString().isBlank() -> {
                edt_password.requestFocus()
                edt_password.error = "The password must not be blank"
                return false
            }
            else -> return true
        }
    }

    override fun onClick(v: View?) {
        when (v) {
            btn_register_login -> {
                startActivity<RegisterActivity>()
            }
        }
    }
}
