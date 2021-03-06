package com.demarkelabs.android_kotlin_facebook_login

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.facebook.AccessToken
import com.facebook.GraphRequest
import com.facebook.GraphResponse
import com.parse.ParseException
import com.parse.ParseUser
import com.parse.facebook.ParseFacebookUtils
import org.json.JSONException
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    private val TAG = "LoginActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val login = findViewById<Button>(R.id.login)
        login.setOnClickListener {
            val dlg = ProgressDialog(this)
            dlg.setTitle("Please, wait a moment.")
            dlg.setMessage("Logging in...")
            dlg.show()
            val permissions: Collection<String> = listOf("public_profile", "email")
            ParseFacebookUtils.logInWithReadPermissionsInBackground(this, permissions) { user: ParseUser?, err: ParseException? ->
                dlg.dismiss()
                when {
                    err != null -> {
                        Log.e("FacebookLoginExample", "done: ", err)
                        Toast.makeText(this, err.message, Toast.LENGTH_LONG).show()
                    }
                    user == null -> {
                        Toast.makeText(this, "The user cancelled the Facebook login.", Toast.LENGTH_LONG).show()
                        Log.d("FacebookLoginExample", "Uh oh. The user cancelled the Facebook login.")
                    }
                    user.isNew -> {
                        Toast.makeText(this, "User signed up and logged in through Facebook.", Toast.LENGTH_LONG).show()
                        Log.d("FacebookLoginExample", "User signed up and logged in through Facebook!")
                        getUserDetailFromFB()
                    }
                    else -> {
                        Toast.makeText(this, "User logged in through Facebook.", Toast.LENGTH_LONG).show()
                        Log.d("FacebookLoginExample", "User logged in through Facebook!")
                        showAlert("Oh, you!", "Welcome back!")
                    }
                }
            }
        }
    }

    private fun getUserDetailFromFB() {
        val request =
            GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken()) { `object`: JSONObject, _: GraphResponse? ->
                val user = ParseUser.getCurrentUser()
                try {
                    user.username = `object`.getString("name")
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                try {
                    user.email = `object`.getString("email")
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                user.saveInBackground {
                    if (it == null)
                        showAlert("First Time Login!", "Welcome!")
                    else
                        showAlert("Error", it.message)
                }
            }
        val parameters = Bundle()
        parameters.putString("fields", "name,email")
        request.parameters = parameters
        request.executeAsync()
    }

    private fun showAlert(title: String, message: String?) {
        val builder = AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK") { dialog: DialogInterface, which: Int ->
                dialog.cancel()
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        val ok = builder.create()
        ok.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data)
    }


}