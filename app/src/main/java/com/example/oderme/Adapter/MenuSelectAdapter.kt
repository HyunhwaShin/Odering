package com.example.oderme.Adapter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.example.oderme.Model.Menu
import com.example.oderme.R
import com.example.oderme.ui.menu.SelectedMenuActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.market_menu_layout.view.*

class MenuSelectAdapter(private val mContext : Context, private val mMenu : List<Menu>,private val marketUIDList : ArrayList<String>,private val marketMasterList : ArrayList<String>)
    :RecyclerView.Adapter<MenuSelectAdapter.ViewHolder>(){

    inner class ViewHolder(@NonNull itemView : View) : RecyclerView.ViewHolder(itemView){
        val menuName = itemView.menu_name
        val menuPrice = itemView.menu_price
        val menuImage = itemView.menu_food_image
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.market_menu_layout,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mMenu.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val menu = mMenu[position]
        val marketUID = marketUIDList[position]
        val marketMaster = marketMasterList[position]
        holder.menuName.text = menu.getFoodName()
        holder.menuPrice.text = menu.getFoodPrice()
        Picasso.get().load(menu.getFoodImage()).placeholder(R.drawable.ic_home_black_24dp).into(holder.menuImage)
        val bundle = Bundle()
        holder.itemView.setOnClickListener {
            mContext.startActivity(Intent(mContext,SelectedMenuActivity::class.java)
                .putExtra("menuName",menu.getFoodName())
                .putExtra("menuImage",menu.getFoodImage())
                .putExtra("menuPrice",menu.getFoodPrice())
                .putExtra("marketUID",marketUID)
                .putExtra("marketMaster",marketMaster))

        }

    }

}