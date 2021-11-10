package com.example.oderme

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private var mAuth : FirebaseAuth = FirebaseAuth.getInstance()
    lateinit var ref : DatabaseReference
    lateinit var userID : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        register_btn_login.setOnClickListener {
            startActivity(Intent(this@LoginActivity,SignUpActivity::class.java))
        }

        login_btn.setOnClickListener {
            login()

        }
    }

    private fun login() {
        val email = email_login.text.toString()
        val passowrd = password_login.text.toString()

        when{
            TextUtils.isEmpty(email)-> Toast.makeText(this@LoginActivity,"이메일을 입력해주세요",Toast.LENGTH_SHORT).show()

            TextUtils.isEmpty(passowrd)-> Toast.makeText(this@LoginActivity, "비밀번호를 입력해주세요",Toast.LENGTH_SHORT).show()

            else->{
                mAuth.signInWithEmailAndPassword(email,passowrd).addOnCompleteListener {task->
                    if(task.isSuccessful){
                        startActivity(Intent(this@LoginActivity,MainActivity::class.java)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK))
                    }else{
                        Toast.makeText(this@LoginActivity, "이메일 또는 비밀번호가 틀렸습니다.",Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

    }

    override fun onStart() {
        super.onStart()

        if(mAuth.currentUser != null){

            startActivity(Intent(this@LoginActivity,MainActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK))
        }
    }
}
