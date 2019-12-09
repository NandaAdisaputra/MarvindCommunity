package com.nandaadisaputra.marvindcommunity

import android.content.ContentValues
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_register.*
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

private var sharedPrefManager: SharedPrefManager? = null
private var openHelper: SQLiteOpenHelper? = null
private var db: SQLiteDatabase? = null

class RegisterActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        //panggil kelas SharedPrefManager
        sharedPrefManager = SharedPrefManager(this)
        openHelper = DatabaseHelper(this)

        //cek sudah register atau belum
        if (sharedPrefManager?.sPSudahLogin!!) {
            //intent ke SuccessActivity
            startActivity(
                    Intent(this@RegisterActivity, SuccessActivity::class.java)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                    Intent.FLAG_ACTIVITY_NEW_TASK)
            )
            super.onBackPressed()
        }
        //handler ketika button login diklik
        btn_register.onClick {
            db = openHelper?.writableDatabase
            val email = edt_email_register.text.toString().trim()
            val password = edt_password_register.text.toString().trim()

            //save to sharedpreferences
            sharedPrefManager?.saveSPString(SharedPrefManager.COL_2, email)
            sharedPrefManager?.saveSPString(SharedPrefManager.COL_3, password)
            if (email.isEmpty() || password.isEmpty()) {
            } else {
                dataInsert(email, password)
                toast("Registration successful")
                startActivity(intentFor<LoginActivity>()) //Anko commons
            }
            if (validation()) {
                return@onClick
            }
        }
    }

    private fun dataInsert(email: String?, password: String?) {
        val contentValues = ContentValues()
        contentValues.put(DatabaseHelper.COL_2, email)
        contentValues.put(DatabaseHelper.COL_3, password)
        db?.insert(DatabaseHelper.TABLE_NAME, null, contentValues)

    }

    //Method ini digunakan untuk memvalidasi form register
    private fun validation(): Boolean {
        when {
            //Check email is empty or not
            edt_email_register.text.toString().isBlank() -> {
                edt_email_register.requestFocus()
                edt_email_register.error = "Your Email may not be empty"
                return false
            }
            //Check the password is empty or not
            edt_password_register.text.toString().isBlank() -> {
                edt_password_register.requestFocus()
                edt_password_register.error = "Your password must not be blank"
                return false
            }
            else -> return true
        }
    }
    //handler OnClick
    override fun onClick(v: View?) {
        when (v) {
            btn_login_register -> {
                //intent ke LoginActivity
                startActivity<LoginActivity>()
            }

        }
    }
}