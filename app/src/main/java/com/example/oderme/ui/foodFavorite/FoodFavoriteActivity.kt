package com.example.oderme.ui.foodFavorite

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import android.widget.Toolbar
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.oderme.APIService
import com.example.oderme.Adapter.FoodFavoriteAdapter
import com.example.oderme.Fcm.*
import com.example.oderme.Model.Favorite
import com.example.oderme.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_food_favorite.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class FoodFavoriteActivity : AppCompatActivity() {

    private var favoriteList : ArrayList<Favorite> = arrayListOf()
    private var firebaseUser = FirebaseAuth.getInstance().currentUser
    var totalPrice = 0
    val cureent = Calendar.getInstance().time
    val cureentDateFormatter = SimpleDateFormat("yyyy-MM-dd",Locale.KOREA)
    val cureentDate = cureentDateFormatter.format(cureent)
    val cureentFullDateFormatter = SimpleDateFormat("yyyy년 MM월 dd일 HH시 mm분 ss초", Locale.KOREA)
    val cureentFullDate = cureentFullDateFormatter.format(cureent)
    var userName = ""
    var apiService : APIService? = null
    var market_master = ""


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_favorite)

        val toolbar : androidx.appcompat.widget.Toolbar = toolbar_food_favorite
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        var marketUID : String? = null
        marketUID= intent.getStringExtra("marketUID")

        val marketName = intent.getStringExtra("marketName")
        val recyclerView : RecyclerView = recycler_food_favorite
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.reverseLayout = false
        linearLayoutManager.isSmoothScrollbarEnabled = true
        recyclerView.layoutManager = linearLayoutManager
        food_favorite_title_name.text = marketName
        val sharedPreferences = getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        val isVisit = sharedPreferences.getString("visit","false")
        userName = getSharedPreferences("PREFS", Context.MODE_PRIVATE).getString("name","none").toString()
        val ref = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid).child("favorite")
            .child(marketUID!!)
        apiService = Client.Client.getClient("https://fcm.googleapis.com/")!!.create(APIService::class.java)


        val masterRef = FirebaseDatabase.getInstance().reference.child("Markets").child(marketUID)
            .child("uid")
        masterRef.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                market_master = dataSnapshot.value.toString()

            }
        })

        ref.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.exists()){
                    for(snapshot in dataSnapshot.children){

                        val favorite :Favorite = snapshot.getValue(Favorite::class.java)!!
                        favoriteList.add(favorite)
                        totalPrice += favorite.getMenuPriceTotal().toInt()

                    }

                    recyclerView.adapter = FoodFavoriteAdapter(this@FoodFavoriteActivity,favoriteList)
                    food_favorite_buy_text.text = "${totalPrice}원 주문하기"

                }
            }

        })

        toolbar.setNavigationOnClickListener {
            finish()
        }

        food_favorite_buy_btn.setOnClickListener {
            if(favoriteList.size>0){


                if(isVisit == "true"){

                    var ref = FirebaseDatabase.getInstance().reference.child("Markets").child(marketUID).child("buy")
                        .child(cureentDate)

                    var map : HashMap<String,Any> = HashMap<String,Any>()
                    map["buyDay"] = cureentFullDate
                    map["userName"] = userName
                    map["userID"] = firebaseUser!!.uid.toString()
                    map["buy"] = favoriteList
                    map["totalPrice"] = totalPrice

                    ref.push().setValue(map)

                    var owerMap : HashMap<String,Any> = HashMap()
                    owerMap["title"] = "${userName}님이 주문을 하였습니다."
                    owerMap["subInfo"] = "좌석 : 1번 테이블 \n 인원수 : 2명 \n"
                    owerMap["userName"] = userName
                    owerMap["userUID"] = firebaseUser!!.uid
                    owerMap["marketUID"] = marketUID
                    owerMap["type"] = "주문"

                    FirebaseDatabase.getInstance().reference.child("Owner").child(marketUID).push().setValue(owerMap)

                    FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid).child("favorite").child(marketUID).removeValue()
                    Toast.makeText(this@FoodFavoriteActivity,"주문 완료!",Toast.LENGTH_SHORT).show()

                    //sendToOwnerNotification(market_master,"주문 도착~",marketName)

                    finish()

                }else{

                    startActivity(Intent(this,FoodFavoriteOptionActivity::class.java)
                        .putExtra("buyDay",cureentFullDate)
                        .putExtra("userName",userName)
                        .putExtra("userID",firebaseUser!!.uid.toString())
                        .putExtra("totalPrice",totalPrice)
                        .putExtra("marketUID",marketUID)
                        .putExtra("marketName",marketName)
                        .putExtra("marketMaster",market_master)
                    )

                }
            }else{
                Toast.makeText(this@FoodFavoriteActivity,"장바구니에 음식을 담아주세요!",Toast.LENGTH_SHORT).show()
            }

        }



    }

    private fun sendToOwnerNotification(receiverId: String, message: String,marketName : String) {
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
                        "true"
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
                                        Toast.makeText(this@FoodFavoriteActivity,"Failed, Nothing happen",Toast.LENGTH_SHORT).show()

                                    }
                                }
                            }

                        })

                }
            }
        })

    }
}
