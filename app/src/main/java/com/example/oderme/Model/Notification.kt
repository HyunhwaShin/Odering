package com.example.oderme.Model

class Notification {
    private var title : String= ""
    private var subInfo :String = ""
    private var userName : String = ""
    private var userUID : String = ""
    private var marketUID : String = ""
    private var type :String = ""

    constructor()
    constructor(
        title: String,
        subInfo: String,
        userName: String,
        userUID: String,
        marketUID: String,
        type : String
    ) {
        this.title = title
        this.subInfo = subInfo
        this.userName = userName
        this.userUID = userUID
        this.marketUID = marketUID
        this.type = type
    }

    fun getTitle() : String{
        return title
    }

    fun setTitle(title: String){
        this.title = title
    }

    fun getSubInfo() : String{
        return subInfo
    }

    fun setSubInfo(subInfo: String){
        this.subInfo = subInfo
    }

    fun getUserName() : String {
        return userName
    }

    fun setUserName(userName: String){
        this.userName = userName
    }

    fun getUserUID() : String {
        return userUID
    }

    fun setUserUID(userUID: String){
        this.userUID = userUID
    }

    fun getMarketUID() : String {
        return marketUID
    }

    fun setMarketUID(marketUID: String){
        this.marketUID = marketUID
    }

    fun getType() : String{
        return type
    }

    fun setType(type: String){
        this.type = type
    }
}