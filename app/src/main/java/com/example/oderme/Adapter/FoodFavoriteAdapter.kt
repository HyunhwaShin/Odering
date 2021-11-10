package com.example.oderme.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.example.oderme.Model.Favorite
import com.example.oderme.R
import kotlinx.android.synthetic.main.food_favorite_layout.view.*

class FoodFavoriteAdapter(private val mContext : Context , private val favoriteList : List<Favorite>)
    :RecyclerView.Adapter<FoodFavoriteAdapter.ViewHolder>(){

    inner class ViewHolder (@NonNull itemView : View) : RecyclerView.ViewHolder(itemView){
        val name = itemView.food_favorite_name
        val price = itemView.food_favorite_price
        val count = itemView.food_favorite_count

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.food_favorite_layout,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return favoriteList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val favorite : Favorite = favoriteList[position]

        holder.name.text = favorite.getMenuName()
        holder.price.text = favorite.getMenuPriceTotal().toString()
        holder.count.text = favorite.getCounter().toString()


    }

}