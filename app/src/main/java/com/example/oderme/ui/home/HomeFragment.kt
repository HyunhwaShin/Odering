package com.example.oderme.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.oderme.R
import com.example.oderme.ui.market.MarketSelectActivity
import kotlinx.android.synthetic.main.fragment_home.view.*

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        val view =  inflater.inflate(R.layout.fragment_home, container, false)

        view.food_korea.setOnClickListener {
            startActivity(Intent(context,MarketSelectActivity::class.java)
                .putExtra("type","한식"))
        }

        view.food_fast.setOnClickListener {
            startActivity(Intent(context,MarketSelectActivity::class.java)
                .putExtra("type","패스트푸드"))
        }

        return view
    }
}
