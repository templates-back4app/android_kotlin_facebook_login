package com.demarkelabs.android_kotlin_facebook_login

import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.facebook.login.LoginManager
import com.parse.ParseUser

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val logout_button = findViewById<Button>(R.id.logout_button)
        logout_button.setOnClickListener { v->
            val dlg = ProgressDialog(this)
            dlg.setTitle("Please, wait a moment.")
            dlg.setMessage("Logging out...")
            dlg.show()
            LoginManager.getInstance().logOut()
            ParseUser.logOutInBackground { e->
                if (e == null)
                    showAlert("So, you're going...", "Ok...Bye-bye then", true)
                else
                    showAlert("Error...", e.message, false)
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