package com.example.oderme.Fcm

class OwnerData {
    private var marketName : String = ""
    private var icon  = 0
    private var body : String = ""
    private var title : String = ""
    private var sented : String = ""
    private var marketUID : String = ""

    constructor()
    constructor(marketName: String, icon: Int, body: String, title: String, sented: String, marketUID : String) {
        this.marketName = marketName
        this.icon = icon
        this.body = body
        this.title = title
        this.sented = sented
        this.marketUID = marketUID
    }

    fun getUser() :String{
        return marketName
    }

    fun setUser(user: String){
        this.marketName = marketName
    }

    fun getIcon() :Int{
        return icon
    }

    fun setIcon(icon: Int){
        this.icon = icon
    }

    fun getTitle() :String{
        return title
    }

    fun setTitle(title: String){
        this.title = title
    }

    fun getBody() :String{
        return body
    }

    fun setBody(body: String){
        this.body = body
    }

    fun getSented() :String{
        return sented
    }

    fun setSented(sented: String){
        this.sented = sented
    }

    fun getMarketUID() :String{
        return marketUID
    }

    fun setMarketUID(marketUID: String){
        this.marketUID = marketUID
    }
}