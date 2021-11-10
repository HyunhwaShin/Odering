package com.example.oderme.Model

class Reservation {
    private var userID :String = ""
    private var userName : String = ""
    private var totalPrice : Long = 0
    private var buyDay : String =""
    private var buy : ArrayList<Favorite> = arrayListOf()
    private var option : String = ""

    constructor()
    constructor(
        userID: String,
        userName: String,
        totalPrice: Long,
        buyDay: String,
        buy: ArrayList<Favorite>,
        option : String
    ) {
        this.userID = userID
        this.userName = userName
        this.totalPrice = totalPrice
        this.buyDay = buyDay
        this.buy = buy
        this.option = option
    }

    fun getUserID() : String{
        return userID
    }

    fun setUserID(userID: String){
        this.userID = userID
    }

    fun getUserName() : String{
        return userName
    }

    fun setUserName(userName: String){
        this.userName = userName
    }

    fun getTotalPrice() : Long{
        return totalPrice
    }

    fun setTotalPrice(totalPrice: Long){
        this.totalPrice = totalPrice
    }

    fun getBuyDay() : String {
        return buyDay
    }

    fun setBuyDat(buyDay: String) {
        this.buyDay = buyDay
    }

    fun getBuy():ArrayList<Favorite>{
        return buy
    }

    fun setBuy(buy: ArrayList<Favorite>){
        this.buy = buy
    }

    fun getOption():String{
        return option
    }

    fun setOption(option: String){
        this.option = option
    }

}