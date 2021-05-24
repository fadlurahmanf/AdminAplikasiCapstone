package com.example.adminaplikasicapstone.utils.authentication

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class AuthenticationServices {
    var authenticationServices = FirebaseAuth.getInstance()
    inner class AdminData{
        fun signUpAdmin(email:String, password:String): Task<AuthResult> {
            return authenticationServices.createUserWithEmailAndPassword(email, password)
        }
        fun signInAdmin(email: String, password: String): Task<AuthResult> {
            return authenticationServices.signInWithEmailAndPassword(email, password)
        }
        fun checkIsAdminIsSignIn(): FirebaseUser? {
            return authenticationServices.currentUser
        }
        fun signOut(){
            return authenticationServices.signOut()
        }
    }
}