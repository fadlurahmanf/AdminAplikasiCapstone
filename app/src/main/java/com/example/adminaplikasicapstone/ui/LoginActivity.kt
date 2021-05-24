package com.example.adminaplikasicapstone.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.adminaplikasicapstone.MainActivity
import com.example.adminaplikasicapstone.R
import com.example.adminaplikasicapstone.utils.authentication.AuthenticationServices
import com.example.adminaplikasicapstone.utils.firestore.FirestoreObject.COLLECTION_ADMIN_DATA
import com.example.adminaplikasicapstone.utils.firestore.FirestoreObject.COL_EMAIL
import com.example.adminaplikasicapstone.utils.firestore.FirestoreServices
import kotlinx.coroutines.runBlocking


class LoginActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var email_text:EditText
    private lateinit var password_text:EditText
    private lateinit var btn_Login:Button
    private lateinit var btn_regis:Button

    private var listEmail:ArrayList<String> = ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initializationIdLayout()
    }



    override fun onClick(v: View?) {
        when(v?.id){
            R.id.loginactivity_btnLogin->{
                getAllEmailListAdminData()
            }
            R.id.loginactivity_btnRegis->{
                val intent = Intent(this, RegisterActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun signIn() {
        var authenticationServices = AuthenticationServices()
        var signInQuery = authenticationServices.AdminData().signInAdmin(email_text.text.toString(), password_text.text.toString())
        signInQuery.addOnSuccessListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
//            finish()
        }.addOnFailureListener {
            Toast.makeText(this, "${it.message.toString()}", Toast.LENGTH_LONG).show()
        }
    }

    fun getAllEmailListAdminData() {
        var firestoreServices = FirestoreServices()
        var getQuery = firestoreServices.AdminData().checkIsEmailIsInAdminData(email_text.text.toString())
        getQuery.addOnCompleteListener {
            listEmail.clear()
            for (document in it.result!!){
                if (document.id==email_text.text.toString()){
                    listEmail.add(document.id)
                }
            }
            if (listEmail.size>0){
                signIn()
            }else{
                Toast.makeText(this, "BELOM TERDAFTAR DI FIRESTORE", Toast.LENGTH_LONG).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "${it.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun initializationIdLayout() {
        email_text = findViewById(R.id.loginactivity_email)
        password_text = findViewById(R.id.loginactivity_password)
        btn_Login = findViewById(R.id.loginactivity_btnLogin)
        btn_regis = findViewById(R.id.loginactivity_btnRegis)

        btn_Login.setOnClickListener(this)
        btn_regis.setOnClickListener(this)
    }

}