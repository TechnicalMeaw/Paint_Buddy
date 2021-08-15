package com.example.paintbuddy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.example.paintbuddy.IntentStrings.Companion.PHONE_NUMBER
import kotlinx.android.synthetic.main.activity_login.*
import java.util.*
import kotlin.concurrent.timerTask

class LoginActivity : AppCompatActivity() {

    var phoneNumber = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

//        supportActionBar?.hide()

        nextButton.setOnClickListener {
            if (loginPhoneEditText.text!!.length == 10){
                phoneNumber = cpp.selectedCountryCodeWithPlus + loginPhoneEditText.text.toString()
                val intent = Intent(this, VerifyCodeActivity::class.java)
                intent.putExtra(PHONE_NUMBER, phoneNumber)
                startActivity(intent)
            }else{
                Toast.makeText(this, "Invalid Phone Number", Toast.LENGTH_SHORT).show()
            }
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


}