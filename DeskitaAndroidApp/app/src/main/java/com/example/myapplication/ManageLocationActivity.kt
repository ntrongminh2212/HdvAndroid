package com.example.myapplication

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AlertDialog
import com.example.myapplication.adapter.LocationAdapter
import com.example.myapplication.entities.UserAddress
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.act_manage_location.*

class ManageLocationActivity : AppCompatActivity() {
    private val CODE_ADD_LOCATION = 1
    val lstAddressType = object : TypeToken<ArrayList<UserAddress>>() {}.type
    var purpose: Int = 0
    var lstUserAddress = ArrayList<UserAddress>()
    lateinit var defaultAdress: UserAddress
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_manage_location)

        purpose = intent.getIntExtra("purpose", 0)
        if (purpose == 2) {
            txtTittle.text = "Chọn địa chỉ nhận hàng"
        }
        setControl()
        setEvent()
    }

    fun setControl() {
        val sharePref = getSharedPreferences("userData", MODE_PRIVATE)
        val defaultAddressJson = sharePref.getString(getString(R.string.defaultAddress), "")
        val userAddressesJson = sharePref.getString(getString(R.string.userAdresses), "")

        if (!defaultAddressJson!!.isBlank() || !userAddressesJson!!.isBlank()) {
            defaultAdress = Gson().fromJson(defaultAddressJson, UserAddress::class.java)
            if (!userAddressesJson!!.isBlank()) {
                lstUserAddress = Gson().fromJson(userAddressesJson, lstAddressType)
            }
            lstUserAddress.add(0, defaultAdress)
            lvLocations.adapter = LocationAdapter(this, lstUserAddress)
        }
    }

    private fun setEvent() {
        lvLocations.setOnItemClickListener(object : AdapterView.OnItemClickListener {
            override fun onItemClick(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val address: UserAddress = parent?.getItemAtPosition(position) as UserAddress
                if (purpose == 2) {
                    selectReceiveLocation(address)
                } else {
                    if (position > 0) {
                        AlertDialog.Builder(this@ManageLocationActivity)
                            .setTitle("Đổi địa chỉ mặc định")
                            .setMessage("Bạn muốn " + address.address + ", " + address.city + " thành địa chỉ mặc định ?")
                            .setPositiveButton(
                                "Xác nhận",
                                DialogInterface.OnClickListener { dialog, which ->
                                    this@ManageLocationActivity.runOnUiThread {
                                        lstUserAddress.remove(address)
                                        val defaultAddressJson = Gson().toJson(address)
                                        val userAddressesJson = Gson().toJson(lstUserAddress)
                                        val sharePref =
                                            getSharedPreferences("userData", MODE_PRIVATE)
                                        with(sharePref.edit()) {
                                            putString(getString(R.string.defaultAddress), defaultAddressJson)
                                                .commit()
                                            putString(getString(R.string.userAdresses), userAddressesJson)
                                                .commit()
                                        }
                                        setResult(RESULT_OK)
                                        finish()
                                    }
                                }).setNegativeButton("Bỏ qua", null)
                            .show()
                    }
                }
            }
        })
    }

    fun selectReceiveLocation(address: UserAddress) {
        val data = Intent()
        data.putExtra("address", address)
        setResult(RESULT_OK, data)
        finish()
    }

    fun startActAddLocation(item: android.view.MenuItem) {
        val intent = Intent(this, AddLocationAct::class.java)
        startActivityForResult(intent, CODE_ADD_LOCATION)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == CODE_ADD_LOCATION) {
            setControl()
        }
    }

    fun goBack(view: android.view.View) {
        onBackPressed()
    }
}










