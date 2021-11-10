package com.example.oderme

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity() {

    private lateinit var mAuth : FirebaseAuth
    private lateinit var ref : DatabaseReference
    private lateinit var userID : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        var toolbar : Toolbar = toolbar_signup
        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        mAuth = FirebaseAuth.getInstance()

        toolbar.setNavigationOnClickListener {
            finish()
        }

        signup_btn.setOnClickListener {
            signup()
        }

    }

    private fun signup() {
        val name = name_signup.text.toString()
        val email = email_signup.text.toString()
        val password = password_signup.text.toString()
        val tel = phone_signup.text.toString()

        when{
            TextUtils.isEmpty(name) -> Toast.makeText(this@SignUpActivity,"이름을 입력해주세요",Toast.LENGTH_SHORT).show()

            TextUtils.isEmpty(email) -> Toast.makeText(this@SignUpActivity,"이메일을 입력해주세요",Toast.LENGTH_SHORT).show()

            TextUtils.isEmpty(password) -> Toast.makeText(this@SignUpActivity,"비밀번호를 입력해주세요",Toast.LENGTH_SHORT).show()

            TextUtils.isEmpty(tel) -> Toast.makeText(this@SignUpActivity,"전화번호를 입력해주세요",Toast.LENGTH_SHORT).show()

            else ->{

                mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener { task->
                    if(task.isSuccessful){
                        userID = mAuth.currentUser!!.uid
                        ref = FirebaseDatabase.getInstance().reference.child("Users").child(userID)

                        val user = HashMap<String,Any>()
                        user["uid"]=userID
                        user["name"]=name
                        user["tel"]=tel

                        ref.updateChildren(user).addOnCompleteListener { task->
                            if(task.isSuccessful){
                                startActivity(Intent(this@SignUpActivity,MainActivity::class.java)
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                            }
                        }

                    }else{
                        Toast.makeText(this@SignUpActivity,"회원가입 실패",Toast.LENGTH_SHORT).show()
                    }
                }

            }
        }



    }
}
