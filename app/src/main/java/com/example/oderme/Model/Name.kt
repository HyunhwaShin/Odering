package com.example.oderme.Model

class Name {
    private var name : String =""
    constructor()
    constructor(name:String){
        this.name = name
    }

    fun getName () : String{
        return name
    }

    fun setName (name: String) {
        this.name = name
    }
}