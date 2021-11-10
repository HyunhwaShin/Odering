package com.example.oderme.ui.menu.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.oderme.Adapter.MenuSelectAdapter
import com.example.oderme.Model.Market
import com.example.oderme.Model.Menu

import com.example.oderme.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_market_menu.view.*


class MarketMenuFragment(private val marketName :String,private val marketUID : String) : Fragment() {

    private val userID = FirebaseAuth.getInstance().currentUser!!.uid
    private lateinit var ref : DatabaseReference
    private var mMenuList : ArrayList<Menu> = arrayListOf()
    private var mMarketMasterList : ArrayList<String> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_market_menu, container, false)


        view.fragment_market_menu_title.text = marketName
        val recyclerView : RecyclerView = view.fragment_market_menu_recycler
        val linearLayoutManager : LinearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.reverseLayout = false
        recyclerView.layoutManager=linearLayoutManager

        if(marketUID == "none"){

            ref = FirebaseDatabase.getInstance().reference.child("Markets")

            ref.addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if(dataSnapshot.exists()){
                        var marketUIDList = arrayListOf<String>()
                        for(snapshot in dataSnapshot.children){
                            val marketUID = snapshot.key.toString()
                            val marketMaster = snapshot.child("uid").value.toString()
                            mMarketMasterList.add(marketMaster)
                            marketUIDList.add(marketUID)
                            if(snapshot.hasChild("menu")){
                                val menuRef = snapshot.child("menu")
                                for(menuSnapshot in menuRef.children){
                                    val menu : Menu = menuSnapshot.getValue(Menu::class.java)!!
                                    mMenuList.add(menu)


                                }
                            }
                        }

                        recyclerView.adapter = MenuSelectAdapter(requireContext(),mMenuList,marketUIDList,mMarketMasterList)

                    }


                }
            })

        }else{

            ref = FirebaseDatabase.getInstance().reference.child("Markets").child(marketUID)

            ref.addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if(dataSnapshot.exists()){
                        var marketUIDList = arrayListOf<String>()
                        val marketMaster = dataSnapshot.child("uid").value.toString()
                        mMarketMasterList.add(marketMaster)

                        marketUIDList.add(marketUID)
                        if(dataSnapshot.hasChild("menu")){
                            val menuRef = dataSnapshot.child("menu")
                            for(menuSnapshot in menuRef.children){
                                val menu : Menu = menuSnapshot.getValue(Menu::class.java)!!
                                mMenuList.add(menu)
                            }
                        }

                        recyclerView.adapter = MenuSelectAdapter(requireContext(),mMenuList,marketUIDList,mMarketMasterList)

                    }


                }
            })

        }



        return view
    }


}
