package com.example.oderme.ui.foodFavorite

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.RadioButton
import android.widget.Toast
import android.widget.Toolbar
import com.example.oderme.APIService
import com.example.oderme.Adapter.FoodFavoriteAdapter
import com.example.oderme.Fcm.*
import com.example.oderme.MainActivity
import com.example.oderme.Model.Favorite
import com.example.oderme.R
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_food_favorite.*
import kotlinx.android.synthetic.main.activity_food_favorite_option.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FoodFavoriteOptionActivity : AppCompatActivity() {

    var nowClickRadio :RadioButton? = null
    var preClickRadio : RadioButton? = null
    private var favoriteList : ArrayList<Favorite> = arrayListOf()
    var apiService : APIService? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_favorite_option)

        val toolbar : androidx.appcompat.widget.Toolbar = toolbar_favorite_option
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        apiService = Client.Client.getClient("https://fcm.googleapis.com/")!!.create(APIService::class.java)

        var radio_post_arrival_cooking = radio_post_arrival_cooking
        var radio_when_arrival_eat = radio_when_arrival_eat
        var radio_take_out = radio_take_out
        var radio_drive_throw = radio_drive_throw
        var market_master = intent.getStringExtra("marketMaster").toString()

        radio_post_arrival_cooking.setOnClickListener {
            if(preClickRadio == null){
                preClickRadio = radio_post_arrival_cooking
                nowClickRadio = radio_post_arrival_cooking
                nowClickRadio!!.isChecked = true
            }else{
                preClickRadio = nowClickRadio
                nowClickRadio = radio_post_arrival_cooking
                preClickRadio!!.isChecked = false
                nowClickRadio!!.isChecked = true
            }
        }

        radio_when_arrival_eat.setOnClickListener {
            if(preClickRadio == null){
                preClickRadio = radio_when_arrival_eat
                nowClickRadio = radio_when_arrival_eat
                nowClickRadio!!.isChecked = true
            }else{
                preClickRadio = nowClickRadio
                nowClickRadio = radio_when_arrival_eat
                preClickRadio!!.isChecked = false
                nowClickRadio!!.isChecked = true
            }
        }

        radio_take_out.setOnClickListener {
            if(preClickRadio == null){
                preClickRadio = radio_take_out
                nowClickRadio = radio_take_out
                nowClickRadio!!.isChecked = true
            }else{
                preClickRadio = nowClickRadio
                nowClickRadio = radio_take_out
                preClickRadio!!.isChecked = false
                nowClickRadio!!.isChecked = true
            }
        }

        radio_drive_throw.setOnClickListener {
            if(preClickRadio == null){
                preClickRadio = radio_drive_throw
                nowClickRadio = radio_drive_throw
                nowClickRadio!!.isChecked = true
            }else{
                preClickRadio = nowClickRadio
                nowClickRadio = radio_drive_throw
                preClickRadio!!.isChecked = false
                nowClickRadio!!.isChecked = true
            }
        }

        val buyDay : String = intent.getStringExtra("buyDay")
        val userName : String = intent.getStringExtra("userName")
        val userID : String = intent.getStringExtra("userID")
        val totalPrice : Long = intent.getLongExtra("totalPrice", 0)
        val marketUID : String = intent.getStringExtra("marketUID")
        val marketName : String = intent.getStringExtra("marketName")

        val ref = FirebaseDatabase.getInstance().reference.child("Users").child(userID).child("favorite")
            .child(marketUID!!)

        ref.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.exists()){
                    for(snapshot in dataSnapshot.children){

                        val favorite :Favorite = snapshot.getValue(Favorite::class.java)!!
                        favoriteList.add(favorite)
                    }


                }
            }

        })

        reservation.setOnClickListener {
            if(nowClickRadio != null){

                val builder = AlertDialog.Builder(this);
                builder.setTitle("${marketName}")
                if(nowClickRadio == radio_post_arrival_cooking){
                    builder.setMessage("예약을 하시겠습니까? \n")
                }else{
                    builder.setMessage("예약을 하시겠습니까? \n" +
                            "예상소요시간 10분 \n" +
                            "대기팀 0팀")
                }
                builder.setPositiveButton("예약", DialogInterface.OnClickListener { dialog, which ->
                    val reservationRef = FirebaseDatabase.getInstance().reference.child("Users").child(userID)
                        .child("reservation").child(marketUID)

                    reservationRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {

                        }

                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            if(dataSnapshot.exists()){
                                Toast.makeText(this@FoodFavoriteOptionActivity,"예약을 이미 하셨습니다.", Toast.LENGTH_SHORT).show()
                                finish()
                            }else{
                                var map : HashMap<String,Any> = HashMap<String,Any>()
                                map["buyDay"] = buyDay
                                map["userName"] = userName
                                map["userID"] = userID
                                map["buy"] = favoriteList
                                map["totalPrice"] = totalPrice

                                var option :String =""
                                when(nowClickRadio){

                                    radio_post_arrival_cooking -> option="도착후 조리"

                                    radio_when_arrival_eat -> option = "도착하자마자 먹기"

                                    radio_take_out -> option = "포장"

                                    radio_drive_throw -> option = "드라이브 쓰루"

                                }

                                map["option"] = option

                                var owerMap : HashMap<String,Any> = HashMap()
                                owerMap["title"] = "${userName}님이 '${option}' 로 예약하였습니다."
                                if(option == "도착하자마자 먹기" || option == "도착후 조리"){
                                    map["subInfo"] = "좌석 : 1번 테이블 \n 인원수 : 2명 \n 도착까지 예상 소요시간 10분 \n "
                                } else{
                                    map["subInfo"] = "도착까지 예상 소요시간 10분 \n "
                                }
                                owerMap["userName"] = userName
                                owerMap["userUID"] = userID
                                owerMap["marketUID"] = marketUID
                                owerMap["type"] = "예약"

                                //sendToOwnerNotification(market_master,"예약 주문 도착~",marketName,marketUID)

                                FirebaseDatabase.getInstance().reference.child("Owner").child(marketUID).push().setValue(owerMap)


                                reservationRef.push().setValue(map)
                                FirebaseDatabase.getInstance().reference.child("Markets").child(marketUID).child("reservation")
                                    .child(userID).setValue(map)

                                FirebaseDatabase.getInstance().reference.child("Users").child(userID).child("state")
                                    .child("isReservation").setValue("true")

                                Toast.makeText(this@FoodFavoriteOptionActivity,"예약 완료!", Toast.LENGTH_SHORT).show()

                                FirebaseDatabase.getInstance().reference.child("Users").child(userID)
                                    .child("favorite").child(marketUID).removeValue()

                                startActivity(Intent(this@FoodFavoriteOptionActivity,MainActivity::class.java)
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))


                            }
                        }
                    })


                })
                builder.setNegativeButton("아니요", DialogInterface.OnClickListener { dialog, which ->

                })
                builder.show()

            }else{
                Toast.makeText(this@FoodFavoriteOptionActivity,"옵션을 선택해주세요.", Toast.LENGTH_SHORT).show()
            }

        }


    }


    private fun sendToOwnerNotification(receiverId: String, message: String,marketName : String,marketUID : String) {
        val ref : DatabaseReference = FirebaseDatabase.getInstance().reference.child("Tokens")
        val query = ref.orderByKey().equalTo(receiverId)

        query.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for(snapshot in dataSnapshot.children){
                    val token: Token? = snapshot.getValue(Token::class.java)
                    val data = OwnerData(
                        marketName,
                        R.mipmap.ic_launcher,
                        message,
                        "Order Me",
                        receiverId,
                        marketUID
                    )

                    val sender = SenderToOwner(data!!,token!!.getToken())

                    apiService!!.sendToOwnerNotification(sender)
                        .enqueue(object : Callback<MyResponse> {
                            override fun onFailure(call: Call<MyResponse>, t: Throwable) {
                                TODO("Not yet implemented")
                            }

                            override fun onResponse(
                                call: Call<MyResponse>,
                                response: Response<MyResponse>
                            ) {
                                if(response.code() == 200){
                                    if(response.body()!!.success !== 1){
                                        Toast.makeText(this@FoodFavoriteOptionActivity,"Failed, Nothing happen",Toast.LENGTH_SHORT).show()

                                    }
                                }
                            }

                        })

                }
            }
        })

    }

}
