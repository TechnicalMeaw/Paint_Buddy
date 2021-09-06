package com.example.paintbuddy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast

import com.example.paintbuddy.constants.IntentStrings.Companion.COUNTRY
import com.example.paintbuddy.constants.IntentStrings.Companion.PHONE_NUMBER

import kotlinx.android.synthetic.main.activity_login.*
import java.util.*
import kotlin.concurrent.timerTask

class LoginActivity : AppCompatActivity() {

    private var phoneNumber = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

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



    fun skip(view: View) {
        val intent = Intent(this, MainMenuActivity::class.java)
        startActivity(intent)
        finish()
    }

}