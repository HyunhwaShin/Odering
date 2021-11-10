package com.example.oderme.Model

class User {
    private var uid : String = ""
    private var name : String = ""
    private var tel : String = ""

    constructor()
    constructor(uid: String, name: String, tel: String) {
        this.uid = uid
        this.name = name
        this.tel = tel
    }


    fun getName() : String{
        return name
    }

    fun setName(name : String ){
        this.name = name
    }

    fun getUID() : String{
        return uid
    }

    fun setUID(uid: String){
        this.uid = uid
    }

    fun getTel() : String{
        return tel
    }

    fun setTel(tel: String){
        this.tel = tel
    }

}