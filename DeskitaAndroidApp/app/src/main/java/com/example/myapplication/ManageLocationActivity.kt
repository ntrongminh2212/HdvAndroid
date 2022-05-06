package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.myapplication.adapter.LocationAdapter
import com.example.myapplication.entities.UserAddress
import com.google.gson.Gson
import kotlinx.android.synthetic.main.act_manage_location.*

class ManageLocationActivity : AppCompatActivity() {
    private val CODE_ADD_LOCATION = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_manage_location)
        setControl()
    }

    fun setControl(){
        val sharePref = getSharedPreferences("userData", MODE_PRIVATE)
        val defaultAddressJson = sharePref.getString(getString(R.string.defaultAddress),"")
        val userAddressesJson = sharePref.getString(getString(R.string.userAdresses),"")

        if (!defaultAddressJson!!.isBlank() && !userAddressesJson!!.isBlank()){
            var defaultAdress = Gson().fromJson(defaultAddressJson,UserAddress::class.java)
            var lstUserAddress = Gson().fromJson(defaultAddressJson,ArrayList<UserAddress>()::class.java)
            lstUserAddress.add(0,defaultAdress)

            lvLocations.adapter = LocationAdapter(this,lstUserAddress)
        }
    }

    fun startActAddLocation(item: android.view.MenuItem) {
        val intent = Intent(this,AddLocationAct::class.java)
        startActivityForResult(intent,CODE_ADD_LOCATION)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode== RESULT_OK && requestCode==CODE_ADD_LOCATION){
            setControl()
        }
    }
    fun goBack(view: android.view.View) {
        onBackPressed()
    }
}










