package com.example.oderme.ui.menu

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.example.oderme.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_selected_menu.*

class SelectedMenuActivity : AppCompatActivity() {

    private lateinit var userID :String
    private lateinit var ref : DatabaseReference
    private lateinit var marketUID :String
    private lateinit var menuImage :String
    private lateinit var menuName:String
    private lateinit var menuPrice:String
    private lateinit var radio :RadioButton
    private var counter : Int =0
    private lateinit var pBtn:TextView
    private lateinit var mBtn:TextView
    private var menuPriceInt =0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selected_menu)

        val toolbar : Toolbar = toolbar_selected_menu
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        marketUID = intent.getStringExtra("marketUID")
        menuImage = intent.getStringExtra("menuImage")
        menuName = intent.getStringExtra("menuName")
        menuPrice = intent.getStringExtra("menuPrice")
        radio = radio_food
        counter = counter_btn.text.substring(0,counter_btn.text.length-1).toInt()
        pBtn = plus_btn
        mBtn = minus_btn
        menuPriceInt = menuPrice.toInt()

        //Toast.makeText(this,menuPriceInt.toString(),Toast.LENGTH_LONG).show()

        radio.isChecked = true

        pBtn.setOnClickListener {
            counter++
            var price = menuPriceInt * counter
            counter_btn.text = "${counter}개"
            intoFavorite_counter_text.text = "${counter}개 담기"
            intoFavorite_price_text.text = "${price}원"

        }

        mBtn.setOnClickListener {
            if(counter<=1){
                return@setOnClickListener
            }
            counter--
            var price = menuPriceInt * counter
            counter_btn.text = "${counter}개"
            intoFavorite_counter_text.text = "${counter}개 담기"
            intoFavorite_price_text.text = "${price}원"

        }


        Picasso.get().load(menuImage).placeholder(R.drawable.ic_home_black_24dp).into(selected_menu_image)
        selected_menu_name.text = menuName
        radio_price.text = menuPrice

        intoFavorite.setOnClickListener {
            createFavorite()
        }

        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun createFavorite() {
        userID = FirebaseAuth.getInstance().currentUser!!.uid
        ref = FirebaseDatabase.getInstance().reference.child("Users").child(userID).child("favorite")

        val hashMap = HashMap<String,Any>()
        hashMap["menuImage"] = menuImage
        hashMap["menuName"] = menuName
        hashMap["menuPrice"] = menuPrice
        hashMap["counter"] = counter
        hashMap["menuPriceTotal"] = (menuPriceInt * counter).toString()


        ref.child(marketUID).push().setValue(hashMap).addOnCompleteListener {task->
            if(task.isSuccessful){
                Toast.makeText(this@SelectedMenuActivity,"장바구니 담기 성공!",Toast.LENGTH_SHORT).show()
                finish()
            }else{
                Toast.makeText(this@SelectedMenuActivity,"장바구니 담기 실패",Toast.LENGTH_SHORT).show()
            }
        }
    }
}
