package com.example.oderme

import com.example.oderme.Fcm.MyResponse
import com.example.oderme.Fcm.Sender
import com.example.oderme.Fcm.SenderToOwner
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface APIService {
    @Headers(
        "Content-Type:application/json",
        "Authorization:key=AAAA5il9NGQ:APA91bF3lF1Z6n5qp-o1tu7Txqzgegy_aHVqgoiOCViCUqT7dPglbWybGiohnxdstVVCyGy7QTYh355SfU2_bOtv3I_87jSV--oydrDVGpUiEvigL_zsw-oEG5DkJnSohwp1Be86fk8R"
    )

    @POST("fcm/send")
    fun sendNotification(@Body body : Sender) : Call<MyResponse>

    @POST("fcm/send")
    fun sendToOwnerNotification(@Body body : SenderToOwner) : Call<MyResponse>
}