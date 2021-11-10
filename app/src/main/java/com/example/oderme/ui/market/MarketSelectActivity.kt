package com.example.oderme.ui.market

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.oderme.Adapter.MarketSelectAdapter
import com.example.oderme.Model.Market
import com.example.oderme.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_market_select.*

class MarketSelectActivity : AppCompatActivity() {

    private val userID = FirebaseAuth.getInstance().currentUser!!.uid
    private lateinit var ref : DatabaseReference
    private var mMarketList : ArrayList<Market> = arrayListOf()
    private var mMarketUIDList : ArrayList<String> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_market_select)

        var toolbar : Toolbar = toolbar_market_select
        setSupportActionBar(toolbar)
        toolbar_market_select_title.text = intent.getStringExtra("type")
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        var recyclerView : RecyclerView = recycler_market_select
        var linearLayoutManager : LinearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.reverseLayout
        recyclerView.layoutManager = linearLayoutManager

        ref = FirebaseDatabase.getInstance().reference.child("Markets")

        ref.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.exists()){
                    for(snapshot in dataSnapshot.children){
                        val market : Market = snapshot.getValue(Market::class.java)!!
                        mMarketList.add(market)
                        mMarketUIDList.add(snapshot.key.toString())
                    }

                    recyclerView.adapter = MarketSelectAdapter(this@MarketSelectActivity,mMarketList,mMarketUIDList)


                }
            }
        })


    }
}
