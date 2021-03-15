package com.demarkelabs.android_kotlin_facebook_login

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.facebook.login.LoginManager
import com.parse.ParseUser
import com.parse.facebook.ParseFacebookUtils

class MainActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val link_button = findViewById<Button>(R.id.link_button)
        val logout_button = findViewById<Button>(R.id.logout_button)
        val textView = findViewById<TextView>(R.id.textView)
        val unlink_button = findViewById<Button>(R.id.unlink_button)


        textView.text = getString(R.string.welcome) + "\n"
        val intent = intent
        if (intent != null) {
            if (intent.getStringExtra("info") != null)
                textView.text = "Welcome to My App\n" + getIntent().getStringExtra("info")
        }
        logout_button.setOnClickListener { v ->
            val dlg = ProgressDialog(this)
            dlg.setTitle("Please, wait a moment.")
            dlg.setMessage("Logging out...")
            dlg.show()
            LoginManager.getInstance().logOut()
            ParseUser.logOutInBackground { e ->
                if (e == null)
                    showAlert("So, you're going...", "Ok...Bye-bye then", true)
                else
                    showAlert("Error...", e.message, false)
            }
        }

        link_button.setOnClickListener {
            val permissions = listOf("public_profile", "email")
            if (!ParseFacebookUtils.isLinked(ParseUser.getCurrentUser())) {
                ParseFacebookUtils.linkWithReadPermissionsInBackground(
                    ParseUser.getCurrentUser(),
                    this,
                    permissions
                ) {
                    if (ParseFacebookUtils.isLinked(ParseUser.getCurrentUser())) {
                        Toast.makeText(
                            this,
                            "Woohoo, user logged in with Facebook.",
                            Toast.LENGTH_LONG
                        ).show()
                        Log.d("FacebookLoginExample", "Woohoo, user logged in with Facebook!")
                    }
                }
            } else {
                Toast.makeText(
                    this,
                    "You have already linked your account with Facebook.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }


        unlink_button.setOnClickListener {
            ParseFacebookUtils.unlinkInBackground(ParseUser.getCurrentUser()) {
                if (it == null) {
                    Toast.makeText(this,"The user is no longer associated with their Facebook account.",Toast.LENGTH_LONG).show()
                    Log.d("MyApp", "The user is no longer associated with their Facebook account.")
                } else {
                    Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun showAlert(title: String, message: String?, isOk: Boolean) {
        val builder = AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK") { dialog: DialogInterface, _: Int ->
                dialog.cancel()
                if (isOk) {
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }
            }
        val ok = builder.create()
        ok.show()
    }


}