package com.example.oderme.Model

class Market {
    private var beconUID : String = ""
    private var name :String = ""
    private var image :String = ""
    private var subInfo : String = ""
    private var uid : String =""


    constructor()

    constructor(beconUID : String, name: String, image: String, subInfo: String, uid:String) {
        this.beconUID = beconUID
        this.name = name
        this.image = image
        this.subInfo = subInfo
        this.uid = uid
    }

    fun getBeconUID () :String{
        return beconUID
    }

    fun setBeconUID(beconUID: String){
        this.beconUID = beconUID
    }

    fun getName() : String{
        return name
    }

    fun setName(name:String){
        this.name = name
    }

    fun getImage() : String{
        return image
    }

    fun setImage(image:String){
        this.image = image
    }

    fun getSubInfo() : String{
        return subInfo
    }

    fun setSubInfo(subInfo:String){
        this.subInfo = subInfo
    }

    fun getUid() : String{
        return uid
    }

    fun setUid(uid:String){
        this.uid = uid
    }



}