package com.example.oderme

import android.Manifest
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.Layout
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.oderme.Fcm.*
import com.example.oderme.Model.Reservation
import com.example.oderme.ui.favorite.FavoriteFragment
import com.example.oderme.ui.home.HomeFragment
import com.example.oderme.ui.profile.ProfileFragment
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import com.minew.beacon.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class MainActivity : AppCompatActivity() {

    private var mMinewBeaconManager: MinewBeaconManager? = null
    private var isScanning = false
    var comp = UserRssi()
    private var state = 0
    private val userID = FirebaseAuth.getInstance().currentUser
    var notify = false
    var apiService : APIService? = null
    var marketName = ""
    var marketUID = ""
    var checkBecone = false
    var name : String =""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        apiService = Client.Client.getClient("https://fcm.googleapis.com/")!!.create(APIService::class.java)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        val ref = FirebaseDatabase.getInstance().reference.child("Users").child(userID!!.uid).child("name")

        navView.setOnNavigationItemSelectedListener(onNavigationOnItemSelectedListener)

        checkLocationPermition()
        initManager()
        checkBluetooth()

        updateToken(FirebaseInstanceId.getInstance().token)
        moveToFragment(HomeFragment())

        if(!checkBecone){
            val pref = getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
            pref.putString("visit","false")
            pref.apply()
        }


        ref.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                name = dataSnapshot.value.toString()
                val sharedPreferences = getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
                sharedPreferences.putString("name",name)
                sharedPreferences.apply()
                Toast.makeText(this@MainActivity,"${name}님 환영합니다",Toast.LENGTH_SHORT).show()
            }
        })



        mMinewBeaconManager!!.setDeviceManagerDelegateListener(object : MinewBeaconManagerListener {
            /**
             * if the manager find some new beacon, it will call back this method.
             *
             * @param minewBeacons  new beacons the manager scanned
             */
            override fun onAppearBeacons(minewBeacons: List<MinewBeacon>) {
                for (minewBeacon in minewBeacons) {
                    if(!notify){
                        notify= true
                        checkBecone = true

                        var deviceUID = minewBeacon.getBeaconValue(BeaconValueIndex.MinewBeaconValueIndex_UUID).getStringValue();
                        Log.d("kwak",deviceUID.toString())
                        Toast.makeText(getApplicationContext(), deviceUID + "  find", Toast.LENGTH_SHORT).show();


                        val ref = FirebaseDatabase.getInstance().reference.child("Markets")
                        ref.addListenerForSingleValueEvent(object : ValueEventListener{
                            override fun onCancelled(p0: DatabaseError) {

                            }

                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                if(dataSnapshot.exists()){
                                    for(snapshot in dataSnapshot.children){
                                        val marketBecon = snapshot.child("beconUID").value.toString()
                                        if(marketBecon == deviceUID){
                                            marketName = snapshot.child("name").value.toString()
                                            marketUID = snapshot.key.toString()
                                            var message = "${marketName} 매장에 방문하셨습니다"
                                            val pref = getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
                                            pref.putString("marketUID",marketUID)
                                            pref.putString("marketName",marketName)
                                            pref.putString("visit","true")
                                            pref.apply()

                                            val visitStateRef = FirebaseDatabase.getInstance().reference.child("Users").child(userID!!.uid)
                                                .child("state").child("isReservation")

                                            visitStateRef.addListenerForSingleValueEvent(object : ValueEventListener{
                                                override fun onCancelled(p0: DatabaseError) {

                                                }

                                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                                    if(dataSnapshot.exists()){

                                                        val reservationRef = FirebaseDatabase.getInstance().reference.child("Users").child(userID.uid)
                                                            .child("reservation").child(marketUID)

                                                        reservationRef.addValueEventListener(object : ValueEventListener{
                                                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                                                if(dataSnapshot.exists()){
                                                                    var option :String = ""
                                                                    for(snapshot in dataSnapshot.children){
                                                                        option = snapshot.child("option").value.toString()

                                                                        when(option){

                                                                            "도착하자마자 먹기" -> show_when_arrival_eat()

                                                                            "도착후 조리" -> show_post_arrival_cooking()

                                                                            "포장" -> show_take_out()

                                                                            "드라이브 쓰루" -> show_drive_throw()

                                                                        }
                                                                    }


                                                                }
                                                            }

                                                            override fun onCancelled(p0: DatabaseError) {

                                                            }
                                                        })



                                                    }
                                                }
                                            })

                                            if(userID !=null){
                                                sendNotification(userID.uid,message,marketName)
                                            }

                                        }
                                    }
                                }
                            }
                        })

                    }


                }
            }

            /**
             * if a beacon didn't update data in 10 seconds, we think this beacon is out of rang, the manager will call back this method.
             *
             * @param minewBeacons beacons out of range
             */
            override fun onDisappearBeacons(minewBeacons: List<MinewBeacon>) {
                for (minewBeacon in minewBeacons) {
                    var deviceName = minewBeacon.getBeaconValue(BeaconValueIndex.MinewBeaconValueIndex_Name).getStringValue();
                    Toast.makeText(getApplicationContext(), deviceName + "  out range", Toast.LENGTH_SHORT).show();
                    notify=false
                    checkBecone = false
                    val pref = getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
                    pref.putString("clickNoti","false")
                    pref.putString("visit","false")
                    pref.apply()
                }

            }

            /**
             * the manager calls back this method every 1 seconds, you can get all scanned beacons.
             *
             * @param minewBeacons all scanned beacons
             */
            override fun onRangeBeacons(minewBeacons: List<MinewBeacon>) {
                runOnUiThread {
                    Collections.sort(minewBeacons, comp)
                    Log.e("tag", state.toString() + "")
                    if (state == 1 || state == 2) {
                    } else {
                        //mAdapter!!.setItems(minewBeacons)
                    }
                }
            }

            /**
             * the manager calls back this method when BluetoothStateChanged.
             *
             * @param state BluetoothState
             */
            override fun onUpdateState(state: BluetoothState) {
                when (state) {
                    BluetoothState.BluetoothStatePowerOn -> Toast.makeText(applicationContext, "BluetoothStatePowerOn", Toast.LENGTH_SHORT).show()
                    BluetoothState.BluetoothStatePowerOff -> Toast.makeText(applicationContext, "BluetoothStatePowerOff", Toast.LENGTH_SHORT).show()
                }
            }
        })

        try {
            mMinewBeaconManager!!.startScan()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun show_drive_throw() {
        val builder = android.app.AlertDialog.Builder(this@MainActivity)
        builder.setTitle("${marketName}")
        builder.setMessage("주문번호 1번 음식 준비 완료되었습니다.")
        builder.setPositiveButton("예",DialogInterface.OnClickListener { dialog, which ->
            val ref = FirebaseDatabase.getInstance().reference.child("Users").child(userID!!.uid)
                .child("reservation").child(marketUID)

            ref.addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if(dataSnapshot.exists()){

                        for(snapshot in dataSnapshot.children){
                            val cureent = Calendar.getInstance().time
                            val cureentDateFormatter = SimpleDateFormat("yyyy-MM-dd",Locale.KOREA)
                            val cureentDate = cureentDateFormatter.format(cureent)
                            val reservation : Reservation = snapshot.getValue(Reservation::class.java)!!
                            FirebaseDatabase.getInstance().reference.child("Markets").child(marketUID)
                                .child("buy").child(cureentDate).push().setValue(reservation)

                            FirebaseDatabase.getInstance().reference.child("Users").child(userID!!.uid)
                                .child("reservation").child(marketUID).removeValue()

                            FirebaseDatabase.getInstance().reference.child("Users").child(userID!!.uid)
                                .child("state").removeValue()

                            Toast.makeText(this@MainActivity,"완료!",Toast.LENGTH_SHORT).show()

                        }



                    }
                }

                override fun onCancelled(p0: DatabaseError) {

                }
            })
        })

        builder.show()
    }

    private fun show_take_out() {
        val builder = android.app.AlertDialog.Builder(this@MainActivity)
        builder.setTitle("${marketName}")
        builder.setMessage("주문번호 1번 음식 포장 완료되었습니다.")
        builder.setPositiveButton("예",DialogInterface.OnClickListener { dialog, which ->
            val ref = FirebaseDatabase.getInstance().reference.child("Users").child(userID!!.uid)
                .child("reservation").child(marketUID)

            ref.addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if(dataSnapshot.exists()){

                        for(snapshot in dataSnapshot.children){
                            val cureent = Calendar.getInstance().time
                            val cureentDateFormatter = SimpleDateFormat("yyyy-MM-dd",Locale.KOREA)
                            val cureentDate = cureentDateFormatter.format(cureent)
                            val reservation : Reservation = snapshot.getValue(Reservation::class.java)!!
                            FirebaseDatabase.getInstance().reference.child("Markets").child(marketUID)
                                .child("buy").child(cureentDate).push().setValue(reservation)

                            FirebaseDatabase.getInstance().reference.child("Users").child(userID!!.uid)
                                .child("reservation").child(marketUID).removeValue()

                            FirebaseDatabase.getInstance().reference.child("Users").child(userID!!.uid)
                                .child("state").removeValue()

                            Toast.makeText(this@MainActivity,"완료!",Toast.LENGTH_SHORT).show()

                        }



                    }
                }

                override fun onCancelled(p0: DatabaseError) {

                }
            })
        })

        builder.show()
    }

    private fun show_post_arrival_cooking() {
        val builder = android.app.AlertDialog.Builder(this@MainActivity)
        builder.setTitle("${marketName}")
        builder.setMessage("예약한 음식을 조리 시작합니다.")
        builder.setPositiveButton("예",DialogInterface.OnClickListener { dialog, which ->
            val ref = FirebaseDatabase.getInstance().reference.child("Users").child(userID!!.uid)
                .child("reservation").child(marketUID)

            ref.addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if(dataSnapshot.exists()){

                        for(snapshot in dataSnapshot.children){
                            val cureent = Calendar.getInstance().time
                            val cureentDateFormatter = SimpleDateFormat("yyyy-MM-dd",Locale.KOREA)
                            val cureentDate = cureentDateFormatter.format(cureent)
                            val reservation : Reservation = snapshot.getValue(Reservation::class.java)!!
                            FirebaseDatabase.getInstance().reference.child("Markets").child(marketUID)
                                .child("buy").child(cureentDate).push().setValue(reservation)

                            FirebaseDatabase.getInstance().reference.child("Users").child(userID!!.uid)
                                .child("reservation").child(marketUID).removeValue()

                            FirebaseDatabase.getInstance().reference.child("Users").child(userID!!.uid)
                                .child("state").removeValue()

                            Toast.makeText(this@MainActivity,"주문 완료!",Toast.LENGTH_SHORT).show()

                        }



                    }
                }

                override fun onCancelled(p0: DatabaseError) {

                }
            })
        })

        builder.show()

    }

    private fun show_when_arrival_eat() {
        val builder = android.app.AlertDialog.Builder(this@MainActivity)
        builder.setTitle("${marketName}")
        builder.setMessage("1번 테이블에 음식을 갖다드리겠습니다.")
        builder.setPositiveButton("예",DialogInterface.OnClickListener { dialog, which ->
            val ref = FirebaseDatabase.getInstance().reference.child("Users").child(userID!!.uid)
                .child("reservation").child(marketUID)

            ref.addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if(dataSnapshot.exists()){

                        for(snapshot in dataSnapshot.children){
                            val cureent = Calendar.getInstance().time
                            val cureentDateFormatter = SimpleDateFormat("yyyy-MM-dd",Locale.KOREA)
                            val cureentDate = cureentDateFormatter.format(cureent)
                            val reservation : Reservation = snapshot.getValue(Reservation::class.java)!!
                            FirebaseDatabase.getInstance().reference.child("Markets").child(marketUID)
                                .child("buy").child(cureentDate).push().setValue(reservation)

                            FirebaseDatabase.getInstance().reference.child("Users").child(userID!!.uid)
                                .child("reservation").child(marketUID).removeValue()

                            FirebaseDatabase.getInstance().reference.child("Users").child(userID!!.uid)
                                .child("state").removeValue()

                            Toast.makeText(this@MainActivity,"완료!",Toast.LENGTH_SHORT).show()

                        }



                    }
                }

                override fun onCancelled(p0: DatabaseError) {

                }
            })
        })

        builder.show()
    }

    private fun sendNotification(receiverId: String, message: String, marketName : String ) {
        val ref : DatabaseReference = FirebaseDatabase.getInstance().reference.child("Tokens")
        val query = ref.orderByKey().equalTo(receiverId)

        query.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for(snapshot in dataSnapshot.children){
                    val token: Token? = snapshot.getValue(Token::class.java)
                    val data = Data(
                        marketName,
                        R.mipmap.ic_launcher,
                        message,
                        "New Message",
                        receiverId,
                        "true"
                    )

                    val sender = Sender(data!!,token!!.getToken())

                    apiService!!.sendNotification(sender)
                        .enqueue(object : Callback<MyResponse>{
                            override fun onFailure(call: Call<MyResponse>, t: Throwable) {
                                TODO("Not yet implemented")
                            }

                            override fun onResponse(
                                call: Call<MyResponse>,
                                response: Response<MyResponse>
                            ) {
                                if(response.code() == 200){
                                    if(response.body()!!.success !== 1){
                                        Toast.makeText(this@MainActivity,"Failed, Nothing happen",Toast.LENGTH_SHORT).show()

                                    }
                                }
                            }

                        })

                }
            }
        })

    }



    private fun updateToken(token: String?) {
        val ref = FirebaseDatabase.getInstance().reference.child("Tokens")
        val token1 = Token(token!!)
        ref.child(userID!!.uid).setValue(token1)
    }


    private val onNavigationOnItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item->
        when(item.itemId){
            R.id.nav_home->{
                moveToFragment(HomeFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_favorite->{
                moveToFragment(FavoriteFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_profile->{
                moveToFragment(ProfileFragment())
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    private fun initManager() {
        mMinewBeaconManager = MinewBeaconManager.getInstance(this)
    }

    private fun moveToFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.nav_host_fragment,fragment).commit()
    }

    private fun checkLocationPermition() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            if (permissionCheck == PackageManager.PERMISSION_DENIED) {

                // 권한 없음
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_ACCESS_FINE_LOCATION)
            } else {

                // ACCESS_FINE_LOCATION 에 대한 권한이 이미 있음.
            }
        } else {
        }
    }

    private fun checkBluetooth() {
        val bluetoothState = mMinewBeaconManager!!.checkBluetoothState()
        when (bluetoothState) {
            BluetoothState.BluetoothStateNotSupported -> {
                Toast.makeText(this, "Not Support BLE", Toast.LENGTH_SHORT).show()
                finish()
            }
            BluetoothState.BluetoothStatePowerOff -> showBLEDialog()
            BluetoothState.BluetoothStatePowerOn -> {
            }
        }
    }

    private fun showBLEDialog() {
        val enableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        startActivityForResult(enableIntent, REQUEST_ENABLE_BT)
    }

    override fun onDestroy() {
        super.onDestroy()
        //stop scan
        if (isScanning) {
            mMinewBeaconManager!!.stopScan()
        }
        val pref = getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
        pref.putString("marketUID",marketUID)
        pref.putString("marketName",marketName)
        pref.putString("clickNoti","false")
        pref.apply()

    }

    override fun onPause() {
        super.onPause()
        val pref = getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
        pref.putString("marketUID",marketUID)
        pref.putString("marketName",marketName)
        pref.apply()
    }

    override fun onResume() {
        super.onResume()
        val pref = getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
        pref.putString("marketUID",marketUID)
        pref.putString("marketName",marketName)
        pref.apply()
    }

    companion object {
        private const val REQUEST_ACCESS_FINE_LOCATION = 1000
        private const val REQUEST_ENABLE_BT = 2
    }


}
