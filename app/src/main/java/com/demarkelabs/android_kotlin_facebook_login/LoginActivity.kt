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
                if (err != null) {
                    dlg.dismiss()
                    ParseUser.logOut()
                    Log.e(TAG, "done: ", err)
                }
                when {
                    user == null -> {
                        dlg.dismiss()
                        ParseUser.logOut()
                        Toast.makeText(this,"The user cancelled the Facebook login.",Toast.LENGTH_LONG).show()
                        Log.d("FacebookLoginExample", "Uh oh. The user cancelled the Facebook login.")
                    }
                    user.isNew -> {
                        dlg.dismiss()
                        Toast.makeText(this,"User signed up and logged in through Facebook.",Toast.LENGTH_LONG).show()
                        Log.d("FacebookLoginExample", "User signed up and logged in through Facebook!")
                        getUserDetailFromFB()
                    }
                    else -> {
                        dlg.dismiss()
                        Toast.makeText(this, "User logged in through Facebook.", Toast.LENGTH_LONG).show()
                        Log.d("FacebookLoginExample", "User logged in through Facebook!")
                        showAlert("Oh, you!", "Welcome back!",ParseUser.getCurrentUser())
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
                        showAlert("First Time Login!", "Welcome!",user)
                    else
                        showAlert("Error", it.message,null)
                }
            }
        val parameters = Bundle()
        parameters.putString("fields", "name,email")
        request.parameters = parameters
        request.executeAsync()
    }

    private fun showAlert(title: String, message: String?, user: ParseUser?) {
        val builder = AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK") { dialog: DialogInterface, which: Int ->
                dialog.cancel()
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                if (user != null) {
                    intent.putExtra("info", "${user.username} \n\n\n Email: ${user.email}".trimIndent())
                }
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