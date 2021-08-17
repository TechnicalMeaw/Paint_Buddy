package com.example.paintbuddy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.paintbuddy.constants.DatabaseLocations
import com.example.paintbuddy.constants.IntentStrings.Companion.COUNTRY
import com.example.paintbuddy.constants.IntentStrings.Companion.PHONE_NUMBER
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_login.*
import java.util.*
import kotlin.concurrent.timerTask

class LoginActivity : AppCompatActivity() {

    var phoneNumber = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        FirebaseApp.initializeApp(/*context=*/this)
        val firebaseAppCheck = FirebaseAppCheck.getInstance()
        firebaseAppCheck.installAppCheckProviderFactory(
            SafetyNetAppCheckProviderFactory.getInstance()
        )

        checkLogin()

        nextButton.setOnClickListener {
            if (loginPhoneEditText.text!!.length == 10){
                phoneNumber = cpp.selectedCountryCodeWithPlus + loginPhoneEditText.text.toString()

                val intent = Intent(this, VerifyCodeActivity::class.java)
                intent.putExtra(PHONE_NUMBER, phoneNumber)
                intent.putExtra(COUNTRY, cpp.selectedCountryName)
                startActivity(intent)
            }else{
                Toast.makeText(this, "Invalid Phone Number", Toast.LENGTH_SHORT).show()
            }
        }

        cpp.setOnCountryChangeListener {
            loginPhoneFieldLayout.prefixText = cpp.selectedCountryCodeWithPlus
        }
        stopAnimation()
    }

    private fun stopAnimation(){
        Timer().schedule(timerTask{
            try {
                pencilAnimation.pauseAnimation()
            }catch (e: Exception){
                println(e)
            }finally {
                runOnUiThread { pencilAnimation.pauseAnimation() }
            }

        }, 3000)
    }


    private fun checkLogin(){
        if(FirebaseAuth.getInstance().uid != null){
            checkRegistered()
        }
    }


    private fun checkRegistered(){
        val ref = FirebaseDatabase.getInstance().getReference("${DatabaseLocations.USERINFO_LOCATION}/${FirebaseAuth.getInstance().uid}")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.hasChild("First Name")) {
                    val intent = Intent(this@LoginActivity, MainMenuActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                else if (snapshot.exists()) {
                    val intent = Intent(this@LoginActivity, RegisterUserActivity::class.java)
                    startActivity(intent)
                    finish()
                }
//                else {
//                    val intent = Intent(this@LoginActivity, RegisterUserActivity::class.java)
//                    startActivity(intent)
//                    finish()
//                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    fun skip(view: View) {
        val intent = Intent(this, MainMenuActivity::class.java)
        startActivity(intent)
        finish()
    }

}