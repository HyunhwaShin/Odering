package com.example.oderme.Adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.oderme.ui.menu.fragment.MarketInfoFragment
import com.example.oderme.ui.menu.fragment.MarketMenuFragment
import com.example.oderme.ui.menu.fragment.MarketReviewFragment

class MenuViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle,marketName :String, marketUID : String) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    private val fragments = listOf<Fragment>(
        MarketMenuFragment(marketName,marketUID),
        MarketInfoFragment(marketName,marketUID),
        MarketReviewFragment(marketName,marketUID)
    )



    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
}