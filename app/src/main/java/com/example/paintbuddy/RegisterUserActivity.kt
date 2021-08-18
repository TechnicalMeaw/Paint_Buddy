package com.example.paintbuddy

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import com.bumptech.glide.Glide
import com.example.paintbuddy.local.LocalStorage.Companion.status
import com.example.paintbuddy.constants.DatabaseLocations.Companion.USERINFO_LOCATION
import com.example.paintbuddy.constants.IntentStrings.Companion.CHOOSE_IMAGE
import com.example.paintbuddy.constants.IntentStrings.Companion.COUNTRY
import com.example.paintbuddy.constants.IntentStrings.Companion.PHONE_NUMBER
import com.example.paintbuddy.constants.StorageLocations
import com.example.paintbuddy.firebaseClasses.UserItem
import com.example.paintbuddy.imageOperations.ImageResizer
import com.example.paintbuddy.local.LocalStorage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_register_user.*
import java.io.ByteArrayOutputStream
import java.util.*


class RegisterUserActivity : AppCompatActivity() {

    private var firstName = ""
    private var lastName = ""
    private var email = ""
    private var phoneNumber = ""
    private var countryName = ""
    private var notificationToken = ""
    private var imageBitmap: Bitmap? = null
    private var dpUrl = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_user)

        phoneNumber = intent.getStringExtra(PHONE_NUMBER).toString()
        notificationToken = MyFirebaseMessagingService.token.toString()
        countryName = intent.getStringExtra(COUNTRY).toString()

        LocalStorage.sharedPref = getSharedPreferences("sharedPref", Context.MODE_PRIVATE)

        createAccountButton.setOnClickListener {
            firstName = loginFirstNameEditText.text.toString()
            lastName = loginLastNameEditText.text.toString()
            email = loginEmailEditText.text.toString()

            if (email != "" && !validateEmail(email)){
                Toast.makeText(this, "Invalid Email", Toast.LENGTH_SHORT).show()
                email = ""
            }else if (firstName != "" && lastName != ""){

                if (imageBitmap == null) {
                    registerUserToDatabase(firstName, lastName, phoneNumber, countryName, email, notificationToken)
                }else{
                    uploadImageToFirebaseAndRegisterUser(imageBitmap)
                }
            }else{
                Toast.makeText(this, "Empty Fields", Toast.LENGTH_SHORT).show()
            }

        }

    }

    private fun uploadImageToFirebaseAndRegisterUser(bitmap: Bitmap?){
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("${StorageLocations.USER_DP_LOCATION}/$filename")
        ref.putBytes(bitmapToByteArray(bitmap!!)).addOnSuccessListener {
            ref.downloadUrl.addOnSuccessListener {
                dpUrl = it.toString()
                registerUserToDatabase(firstName, lastName, phoneNumber, countryName, email, notificationToken)
            }
        }
    }

    private fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val bitmap = ImageResizer.generateThumb(bitmap, 25000)
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream)

        return stream.toByteArray()
    }


    private fun registerUserToDatabase(firstName: String, lastName: String, phoneNumber: String, countryName: String, email: String, notificationToken: String = MyFirebaseMessagingService.token.toString()){
        val ref = FirebaseDatabase.getInstance().getReference("$USERINFO_LOCATION/${FirebaseAuth.getInstance().uid.toString()}")
        val user = UserItem(firstName, lastName, phoneNumber, email, dpUrl, countryName, FirebaseAuth.getInstance().uid.toString(), notificationToken, "normal")
        ref.setValue(user).addOnSuccessListener {
            Toast.makeText(this, "Account Created", Toast.LENGTH_SHORT).show()
//            LocalStorage.sharedPref = getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
            status = "registered"
            val intent = Intent(this, MainMenuActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    fun chooseDP(view: View) {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, CHOOSE_IMAGE)
    }


    var imageUri: Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CHOOSE_IMAGE && resultCode == Activity.RESULT_OK && data != null){
            imageUri = data.data
            circleImageView.setImageURI(imageUri)
            circleImageView.invalidate()
            val dr = circleImageView.drawable
            imageBitmap = dr.toBitmap()
            Glide.with(this).load(imageUri).into(circleImageView)
        }
    }

    private fun validateEmail(emailForValidation: String): Boolean{
        return Patterns.EMAIL_ADDRESS.matcher(emailForValidation).matches()
    }

}