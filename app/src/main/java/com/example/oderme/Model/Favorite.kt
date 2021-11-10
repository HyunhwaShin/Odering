package com.example.oderme.Model

class Favorite{
    private var menuName : String = ""
    private var menuPrice : String = ""
    private var counter : Long = 0
    private var menuPriceTotal : String = ""

    constructor()
    constructor(menuName: String, menuPrice: String, counter: Long, menuPriceTotal:String) {
        this.menuName = menuName
        this.menuPrice = menuPrice
        this.counter = counter
        this.menuPriceTotal = menuPriceTotal
    }

    fun getMenuName () : String{
        return menuName
    }

    fun setMenuName (name:String){
        this.menuName = name
    }

    fun getMenuPrice() : String{
        return menuPrice
    }

    fun setMenuPrice(menuPrice: String) {
        this.menuPrice = menuPrice
    }

    fun getCounter() : Long{
        return counter
    }

    fun setCounter(counter: Long){
        this.counter = counter
    }

    fun getMenuPriceTotal() : String{
        return menuPriceTotal
    }

    fun setMenuPriceTotal(menuPriceTotal: String) {
        this.menuPriceTotal = menuPriceTotal
    }



}