package com.example.paintbuddy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.core.widget.addTextChangedListener
import kotlinx.android.synthetic.main.activity_login.*
import java.util.*
import kotlin.concurrent.timerTask

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

//        supportActionBar?.hide()

        nextButton.setOnClickListener {
            val intent = Intent(this, MainMenuActivity::class.java)
            startActivity(intent)
        }

        loginOTPEditTextField.visibility = View.GONE
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