package com.example.oderme.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.example.oderme.Model.Notification
import com.example.oderme.R
import kotlinx.android.synthetic.main.notification_owner_layout.view.*

class NotificationOwnerAdapter(private val mContext : Context, private val mNotificationList : ArrayList<Notification>)
    :RecyclerView.Adapter<NotificationOwnerAdapter.ViewHolder>(){

    inner class ViewHolder (@NonNull itemView : View) : RecyclerView.ViewHolder(itemView){
        var notification_owner_title = itemView.notification_owner_title
        var notification_owner_subinfo = itemView.notification_owner_subinfo
        var notification_owner_btn = itemView.notification_owner_btn

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.notification_owner_layout,parent,false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mNotificationList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val notification = mNotificationList[position]
        holder.notification_owner_title.text = notification.getTitle()
        holder.notification_owner_subinfo.text = notification.getSubInfo()
    }
}