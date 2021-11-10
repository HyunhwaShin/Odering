package com.example.oderme.Model

class Menu {
    private var foodName : String = ""
    private var foodPrice : String = ""
    private var foodImage : String = ""

    constructor()

    constructor(foodName: String, foodPrice: String, foodImage: String) {
        this.foodName = foodName
        this.foodPrice = foodPrice
        this.foodImage = foodImage
    }

    fun getFoodName() :String {
        return foodName
    }

    fun setFoodName(foodName: String){
        this.foodName = foodName
    }

    fun getFoodPrice() :String {
        return foodPrice
    }

    fun setFoodPrice(foodPrice: String){
        this.foodPrice = foodPrice
    }

    fun getFoodImage() :String {
        return foodImage
    }

    fun setFoodImage(foodImage: String){
        this.foodImage = foodImage
    }


}