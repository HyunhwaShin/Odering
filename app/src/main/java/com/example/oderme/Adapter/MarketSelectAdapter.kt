package com.example.oderme.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.example.oderme.Model.Market
import com.example.oderme.R
import com.example.oderme.ui.menu.MenuSelectActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.market_layout.view.*

class MarketSelectAdapter (private val mContext : Context, private val mMarket : List<Market>, private val mMarketUIDList : List<String>)
    :RecyclerView.Adapter<MarketSelectAdapter.ViewHolder>(){

    inner class ViewHolder (@NonNull itemView : View) : RecyclerView.ViewHolder(itemView){
        var marketImage : ImageView = itemView.market_image
        var marketName : TextView = itemView.market_name
        var marketSubInfo : TextView = itemView.market_sub_info
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.market_layout,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mMarket.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var market = mMarket[position]
        var marketUID : String = mMarketUIDList[position]
        holder.marketName.text = market.getName()
        holder.marketSubInfo.text = market.getSubInfo()
        Picasso.get().load(market.getImage()).placeholder(R.drawable.ic_home_black_24dp).into(holder.marketImage)
        holder.itemView.setOnClickListener {
            mContext.startActivity(Intent(mContext,MenuSelectActivity::class.java)
                .putExtra("marketName",market.getName())
                .putExtra("marketUID",marketUID))

        }

    }
}