package com.example.adminaplikasicapstone.ui.regis

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
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
    private lateinit var phoneNumber_text:EditText
    private lateinit var confPassword_text:EditText
    private lateinit var city_text:EditText
    private lateinit var btn_regis:Button
    private lateinit var btn_login:TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        initializationIdLayout()
    }

    private fun initializationIdLayout() {
        email_text = findViewById(R.id.registeractivity_email)
        password_text = findViewById(R.id.registeractivity_password)
        confPassword_text = findViewById(R.id.registeractivity_confpassword)
        city_text = findViewById(R.id.registeractivity_city)
        phoneNumber_text = findViewById(R.id.registeractivity_phoneNumber)
        btn_regis = findViewById(R.id.registeractivity_btnRegis)
        btn_login = findViewById(R.id.registeractivity_btn_login)
        btn_regis.setOnClickListener(this)
        btn_login.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.registeractivity_btnRegis->{
                checkIsAllTextFieldIsNotEmpty()
                if (checkIsAllTextFieldIsNotEmpty()){
                    if (checkPasswordAndConfPasswordIsMatch()){
                        insertAdminDataToFirestore()
                    }
                }
//                insertAdminDataToFirestore()
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
            Toast.makeText(this, "BERHASIL DAFTAR", Toast.LENGTH_LONG).show()
            email_text.text.clear()
            city_text.text.clear()
            phoneNumber_text.text.clear()
            password_text.text.clear()
            confPassword_text.text.clear()
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

    private fun checkIsAllTextFieldIsNotEmpty():Boolean{
        var isAllTextFieldIsNotEmpty:Boolean = false
        if (email_text.text.isNullOrEmpty()){
            email_text.error = "ISI EMAIL"
            isAllTextFieldIsNotEmpty = false
        }else if (city_text.text.isNullOrEmpty()){
            city_text.error = "ISI KOTA"
            isAllTextFieldIsNotEmpty = false
        }else if (phoneNumber_text.text.isNullOrEmpty()){
            phoneNumber_text.error = "ISI NOMOR TELEPON"
            isAllTextFieldIsNotEmpty = false
        }else if (password_text.text.isNullOrEmpty()){
            password_text.error = "ISI PASSWORD"
            isAllTextFieldIsNotEmpty = false
        }else if (confPassword_text.text.isNullOrEmpty()){
            confPassword_text.error = "ISI KONFIRMASI PASSWORD"
            isAllTextFieldIsNotEmpty = false
        }else if (email_text.text.isNotEmpty() && city_text.text.isNotEmpty()
                &&phoneNumber_text.text.isNotEmpty() && password_text.text.isNotEmpty()
                && confPassword_text.text.isNotEmpty()){
            isAllTextFieldIsNotEmpty = true
        }
        return isAllTextFieldIsNotEmpty
    }

    private fun checkPasswordAndConfPasswordIsMatch():Boolean{
        var isMatch:Boolean = false
        if (password_text.text.toString()==confPassword_text.text.toString()){
            isMatch = true
        }else{
            isMatch = false
            password_text.error = "PASSWORD DAN KONFIRMASI PASSWORD TIDAK SESUAI"
            confPassword_text.error = "PASSWORD DAN KONFIRMASI PASSWORD TIDAK SESUAI"
        }
        return isMatch
    }
}