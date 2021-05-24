package com.example.adminaplikasicapstone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.ImageView
import com.example.adminaplikasicapstone.ui.LoginActivity
import com.example.adminaplikasicapstone.utils.authentication.AuthenticationServices

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        Handler().postDelayed({
            checkIsAdminIsSignIn()
        }, 1000)
    }

    private fun checkIsAdminIsSignIn(){
        var authenticationServices = AuthenticationServices()
        var result = authenticationServices.AdminData().checkIsAdminIsSignIn()
        if (result?.email.toString()!="null"){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }else{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}