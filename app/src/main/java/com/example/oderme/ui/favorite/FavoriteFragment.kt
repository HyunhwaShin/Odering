package com.example.oderme.ui.favorite

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.oderme.Adapter.NotificationOwnerAdapter
import com.example.oderme.Model.Notification

import com.example.oderme.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_favorite.*
import kotlinx.android.synthetic.main.fragment_favorite.view.*


class FavoriteFragment : Fragment() {

    val userID = FirebaseAuth.getInstance().currentUser
    var notificationList : ArrayList<Notification> = arrayListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_favorite, container, false)

        var recyclerView : RecyclerView = view.recycler_notification_owner
        val linearLayoutManager : LinearLayoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = linearLayoutManager

        val ownerRef = FirebaseDatabase.getInstance().reference.child("Owner").child("r0icslSDFakgVEM5v5aDRDO9FVF7g1")

        ownerRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.exists()){
                    for(snapshot in dataSnapshot.children){
                        val notification = snapshot.getValue(Notification::class.java)!!
                        notificationList.add(notification)
                    }
                    recyclerView.adapter = NotificationOwnerAdapter(context!!,notificationList)
                }else{
                    recyclerView.adapter = NotificationOwnerAdapter(context!!,notificationList)
                }
            }
        })


        return view
    }


}
