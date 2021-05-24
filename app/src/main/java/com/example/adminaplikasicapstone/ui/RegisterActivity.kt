package com.example.adminaplikasicapstone.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.adminaplikasicapstone.R
import com.example.adminaplikasicapstone.utils.authentication.AuthenticationServices
import com.example.adminaplikasicapstone.utils.firestore.FirestoreObject.COL_CITY
import com.example.adminaplikasicapstone.utils.firestore.FirestoreObject.COL_EMAIL
import com.example.adminaplikasicapstone.utils.firestore.FirestoreObject.COL_PASSWORD
import com.example.adminaplikasicapstone.utils.firestore.FirestoreServices

class RegisterActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var email_text:EditText
    private lateinit var password_text:EditText
    private lateinit var city_text:EditText
    private lateinit var btn_regis:Button
    private lateinit var btn_login:Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        initializationIdLayout()
    }

    private fun initializationIdLayout() {
        email_text = findViewById(R.id.registeractivity_email)
        password_text = findViewById(R.id.registeractivity_password)
        city_text = findViewById(R.id.registeractivity_city)
        btn_regis = findViewById(R.id.registeractivity_btnRegis)
        btn_login = findViewById(R.id.registeractivity_btn_login)
        btn_regis.setOnClickListener(this)
        btn_login.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.registeractivity_btnRegis->{
                insertAdminDataToFirestore()
            }
            R.id.registeractivity_btn_login->{
                onBackPressed()
            }
        }
    }

    private fun signUpAdmiDataToAuthenticationService(){
        var authenticationServices = AuthenticationServices()
        var result = authenticationServices.AdminData().signUpAdmin(email_text.text.toString(), password_text.text.toString())
        result.addOnSuccessListener {
            println("BERHASIL")
        }.addOnFailureListener {
            Toast.makeText(this, "${it.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun insertAdminDataToFirestore(){
        var adminData:MutableMap<String, Any> = HashMap()
        adminData.put(COL_EMAIL, email_text.text.toString())
        adminData.put(COL_PASSWORD, password_text.text.toString())
        adminData.put(COL_CITY, city_text.text.toString())

        var firestoreServices = FirestoreServices()
        var result = firestoreServices.AdminData().insertAdminData(adminData)
        result.addOnSuccessListener {
            signUpAdmiDataToAuthenticationService()
        }.addOnFailureListener {
            Toast.makeText(this, "${it.message}", Toast.LENGTH_LONG).show()
        }
    }
}