package com.example.oderme.ui.menu

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import android.widget.Toolbar
import androidx.appcompat.app.AlertDialog
import androidx.viewpager2.widget.ViewPager2
import com.example.oderme.Adapter.MenuViewPagerAdapter
import com.example.oderme.Model.Reservation
import com.example.oderme.R
import com.example.oderme.ui.foodFavorite.FoodFavoriteActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_menu_select.*
import java.text.SimpleDateFormat
import java.util.*

class MenuSelectActivity : AppCompatActivity() {

    private val userID = FirebaseAuth.getInstance().currentUser
    private var marketName : String = ""
    private var marketUID : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_select)

        val toolbar : androidx.appcompat.widget.Toolbar = toolbar_menu_select
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        val pref = getSharedPreferences("PREFS",Context.MODE_PRIVATE)

        marketName = pref.getString("marketName","")!!
        marketUID = pref.getString("marketUID","")!!

        if(marketName != "" && marketUID != ""){
            marketName = intent.getStringExtra("marketName")
            marketUID = pref.getString("marketUID","none")!!
        }else{
            marketName = intent.getStringExtra("marketName")
            marketUID = intent.getStringExtra("marketUID")
        }

        val bundle: Bundle? = intent.extras
        val notificationIsClick = bundle?.getString("isClick")

        if(bundle != null && notificationIsClick == "true"){
            val visitStateRef = FirebaseDatabase.getInstance().reference.child("Users").child(userID!!.uid)
                .child("state").child("isReservation")

            visitStateRef.addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if(dataSnapshot.exists()){
                        val reservationRef = FirebaseDatabase.getInstance().reference.child("Users").child(userID.uid)
                            .child("reservation").child(marketUID)

                        reservationRef.addValueEventListener(object : ValueEventListener{
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                if(dataSnapshot.exists()){
                                    for(snapshot in dataSnapshot.children){
                                        val option = snapshot.child("option").value

                                        when(option){

                                            "?????????????????? ??????" -> show_when_arrival_eat()

                                            "????????? ??????" -> show_post_arrival_cooking()

                                            "??????" -> show_take_out()

                                            "???????????? ??????" -> show_drive_throw()

                                        }
                                    }

                                }
                            }

                            override fun onCancelled(p0: DatabaseError) {

                            }
                        })



                    }
                }
            })
        }





        val tabLayout : TabLayout = menu_select_tab
        var viewPager : ViewPager2  = viewpager_menu_select
        var floatingActionButton : FloatingActionButton = floating_favorite_btn

        floatingActionButton.setOnClickListener {
            startActivity(Intent(this@MenuSelectActivity, FoodFavoriteActivity::class.java)
                .putExtra("marketName",marketName)
                .putExtra("marketUID",marketUID)
            )
        }

        viewPager.adapter = MenuViewPagerAdapter(supportFragmentManager,lifecycle,marketName,marketUID)
        menu_select_market_name.text = marketName

        val titles = arrayOf(
            "??????",
            "??????",
            "??????"
        )

        toolbar.setNavigationOnClickListener {
            finish()
        }

        TabLayoutMediator(tabLayout,viewPager){ tab, position ->
            tab.text = titles[position]
        }.attach()
    }

    private fun show_drive_throw() {
        val builder = android.app.AlertDialog.Builder(this@MenuSelectActivity)
        builder.setTitle("${marketName}")
        builder.setMessage("???????????? 1??? ?????? ?????? ?????????????????????.")
        builder.setPositiveButton("???",DialogInterface.OnClickListener { dialog, which ->
            val ref = FirebaseDatabase.getInstance().reference.child("Users").child(userID!!.uid)
                .child("reservation").child(marketUID)

            ref.addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if(dataSnapshot.exists()){

                        for(snapshot in dataSnapshot.children){
                            val cureent = Calendar.getInstance().time
                            val cureentDateFormatter = SimpleDateFormat("yyyy-MM-dd",Locale.KOREA)
                            val cureentDate = cureentDateFormatter.format(cureent)
                            val reservation : Reservation = snapshot.getValue(Reservation::class.java)!!
                            FirebaseDatabase.getInstance().reference.child("Markets").child(marketUID)
                                .child("buy").child(cureentDate).push().setValue(reservation)

                            FirebaseDatabase.getInstance().reference.child("Users").child(userID!!.uid)
                                .child("reservation").child(marketUID).removeValue()

                            FirebaseDatabase.getInstance().reference.child("Users").child(userID!!.uid)
                                .child("state").removeValue()

                            Toast.makeText(this@MenuSelectActivity,"??????!",Toast.LENGTH_SHORT).show()

                        }



                    }
                }

                override fun onCancelled(p0: DatabaseError) {

                }
            })
        })

        builder.show()
    }

    private fun show_take_out() {
        val builder = android.app.AlertDialog.Builder(this@MenuSelectActivity)
        builder.setTitle("${marketName}")
        builder.setMessage("???????????? 1??? ?????? ?????? ?????????????????????.")
        builder.setPositiveButton("???",DialogInterface.OnClickListener { dialog, which ->
            val ref = FirebaseDatabase.getInstance().reference.child("Users").child(userID!!.uid)
                .child("reservation").child(marketUID)

            ref.addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if(dataSnapshot.exists()){

                        for(snapshot in dataSnapshot.children){
                            val cureent = Calendar.getInstance().time
                            val cureentDateFormatter = SimpleDateFormat("yyyy-MM-dd",Locale.KOREA)
                            val cureentDate = cureentDateFormatter.format(cureent)
                            val reservation : Reservation = snapshot.getValue(Reservation::class.java)!!
                            FirebaseDatabase.getInstance().reference.child("Markets").child(marketUID)
                                .child("buy").child(cureentDate).push().setValue(reservation)

                            FirebaseDatabase.getInstance().reference.child("Users").child(userID!!.uid)
                                .child("reservation").child(marketUID).removeValue()

                            FirebaseDatabase.getInstance().reference.child("Users").child(userID!!.uid)
                                .child("state").removeValue()

                            Toast.makeText(this@MenuSelectActivity,"??????!",Toast.LENGTH_SHORT).show()

                        }



                    }
                }

                override fun onCancelled(p0: DatabaseError) {

                }
            })
        })

        builder.show()
    }

    private fun show_post_arrival_cooking() {
        val builder = android.app.AlertDialog.Builder(this@MenuSelectActivity)
        builder.setTitle("${marketName}")
        builder.setMessage("????????? ????????? ?????? ???????????????.")
        builder.setPositiveButton("???",DialogInterface.OnClickListener { dialog, which ->
            val ref = FirebaseDatabase.getInstance().reference.child("Users").child(userID!!.uid)
                .child("reservation").child(marketUID)

            ref.addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if(dataSnapshot.exists()){

                        for(snapshot in dataSnapshot.children){
                            val cureent = Calendar.getInstance().time
                            val cureentDateFormatter = SimpleDateFormat("yyyy-MM-dd",Locale.KOREA)
                            val cureentDate = cureentDateFormatter.format(cureent)
                            val reservation : Reservation = snapshot.getValue(Reservation::class.java)!!
                            FirebaseDatabase.getInstance().reference.child("Markets").child(marketUID)
                                .child("buy").child(cureentDate).push().setValue(reservation)

                            FirebaseDatabase.getInstance().reference.child("Users").child(userID!!.uid)
                                .child("reservation").child(marketUID).removeValue()

                            FirebaseDatabase.getInstance().reference.child("Users").child(userID!!.uid)
                                .child("state").removeValue()

                            Toast.makeText(this@MenuSelectActivity,"?????? ??????!",Toast.LENGTH_SHORT).show()

                        }



                    }
                }

                override fun onCancelled(p0: DatabaseError) {

                }
            })
        })

        builder.show()

    }

    private fun show_when_arrival_eat() {
        val builder = android.app.AlertDialog.Builder(this@MenuSelectActivity)
        builder.setTitle("${marketName}")
        builder.setMessage("1??? ???????????? ????????? ????????????????????????.")
        builder.setPositiveButton("???",DialogInterface.OnClickListener { dialog, which ->
            val ref = FirebaseDatabase.getInstance().reference.child("Users").child(userID!!.uid)
                .child("reservation").child(marketUID)

            ref.addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if(dataSnapshot.exists()){

                        for(snapshot in dataSnapshot.children){
                            val cureent = Calendar.getInstance().time
                            val cureentDateFormatter = SimpleDateFormat("yyyy-MM-dd",Locale.KOREA)
                            val cureentDate = cureentDateFormatter.format(cureent)
                            val reservation : Reservation = snapshot.getValue(Reservation::class.java)!!
                            FirebaseDatabase.getInstance().reference.child("Markets").child(marketUID)
                                .child("buy").child(cureentDate).push().setValue(reservation)

                            FirebaseDatabase.getInstance().reference.child("Users").child(userID!!.uid)
                                .child("reservation").child(marketUID).removeValue()

                            FirebaseDatabase.getInstance().reference.child("Users").child(userID!!.uid)
                                .child("state").removeValue()

                            Toast.makeText(this@MenuSelectActivity,"??????!",Toast.LENGTH_SHORT).show()

                        }



                    }
                }

                override fun onCancelled(p0: DatabaseError) {

                }
            })
        })

        builder.show()
    }
}
