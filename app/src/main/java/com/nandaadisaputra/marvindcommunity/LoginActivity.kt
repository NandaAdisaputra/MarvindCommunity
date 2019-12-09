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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.layout_login.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class LoginActivity : AppCompatActivity(), View.OnClickListener {
    private val rcSignIn: Int = 1
    lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var mGoogleSignInOptions: GoogleSignInOptions
    private lateinit var firebaseAuth: FirebaseAuth
    private var sharedPrefManager: SharedPrefManager? = null
    private var db: SQLiteDatabase? = null
    private var openHelper: SQLiteOpenHelper? = null
    private var cursor: Cursor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        firebaseAuth = FirebaseAuth.getInstance()
        //panggil kelas SharedPrefManager
        sharedPrefManager = SharedPrefManager(this)
        configureGoogleSignIn()
        setupUI()
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
                    .addFlags(
                        Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                Intent.FLAG_ACTIVITY_NEW_TASK
                    )
            )

            super.onBackPressed()
        }

        //panggil kelas DatabaseHelper
        openHelper = DatabaseHelper(this)
        db = openHelper?.readableDatabase

        //Handler ketika button login diklik dgn
        // lambda kita kasih aksi ketika button login di klik
        btn_login.onClick {
            val email = edt_email.text.toString().trim()
            val password = edt_password.text.toString().trim()

            //save to sharedpreferences
            sharedPrefManager?.saveSPString(SharedPrefManager.COL_2, email)
            sharedPrefManager?.saveSPString(SharedPrefManager.COL_3, password)

            if (email.isEmpty() || password.isEmpty()) {
                if (validation()) {
                    return@onClick
                }
            } else {
                cursor = db?.rawQuery(
                    "SELECT *FROM " + DatabaseHelper.TABLE_NAME + " WHERE " +
                            DatabaseHelper.COL_2 + "=? AND " + DatabaseHelper.COL_3 + "=?",
                    arrayOf(email, password)
                )
                if (cursor != null) {
                    if (cursor!!.count > 0) {

                        //Intent to SuccessActivity
                        startActivity<SuccessActivity>()
                        sharedPrefManager?.saveSPBoolean(
                            SharedPrefManager.SP_SUDAH_LOGIN,
                            true
                        )
                        toast("Login Success")
                    } else {
                        //menampilkan toast
                        toast("Username and password do not match")
                    }

                }

            }
        }

    }

    //Method ini digunakan untuk memvalidasi form login
    private fun validation(): Boolean {
        when {
            //cek email tidak boleh kosong
            edt_email.text.toString().isBlank() -> {
                edt_email.requestFocus()
                edt_email.error = "Email cannot be empty"
                return false
            }
            //cek email tidak boleh kosong
            edt_password.text.toString().isBlank() -> {
                edt_password.requestFocus()
                edt_password.error = "The password must not be blank"
                return false
            }
            else -> return true
        }
    }
    ////

    //handler OnClick
    override fun onClick(v: View?) {
        when (v) {
            btn_register_login -> {
                //intent ke RegisterActivity
                startActivity<RegisterActivity>()
            }
            btn_reset ->{
                //intent ke ResetActivity
                startActivity<ResetActivity>()
            }
        }
    }

    private fun configureGoogleSignIn() {
        mGoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, mGoogleSignInOptions)
    }

    private fun setupUI() {
        google_button.onClick {
            signIn()
        }
    }

    private fun signIn() {
        val signInIntent: Intent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, rcSignIn)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == rcSignIn) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {
                toast("Google Sign In failed :(")
            }
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                startActivity(SuccessActivity.getLaunchIntent(this))
            } else {
                toast("Google sign in failed :(")
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            startActivity(SuccessActivity.getLaunchIntent(this))
        }
    }
}
