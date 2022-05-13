package com.example.myapplication

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.get
import com.example.myapplication.entities.City
import com.example.myapplication.entities.Districts
import com.example.myapplication.entities.UserAddress
import com.example.myapplication.entities.Wards
import com.google.gson.Gson
import kotlinx.android.synthetic.main.act_add_location.*
import okhttp3.*
import java.io.IOException
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


class SpnWardAdapter(context: Context, resource: Int, objects: List<Wards>) : ArrayAdapter<Wards>(context, resource, objects) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        convertView = LayoutInflater.from(parent.context).inflate(R.layout.row_province, null)
        val tenSp = convertView!!.findViewById<TextView>(R.id.txtLocationName)
        tenSp.setText(getItem(position)?.name)
        return convertView
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        convertView =
            LayoutInflater.from(parent.context).inflate(R.layout.row_province_dropdown, null)
        val tenSp = convertView!!.findViewById<TextView>(R.id.txtLocationName)
        tenSp.setText(getItem(position)?.name)
        return convertView
    }
}

class SpnDistrictAdapter(context: Context, resource: Int, objects: List<Districts>) : ArrayAdapter<Districts>(context, resource, objects) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        convertView = LayoutInflater.from(parent.context).inflate(R.layout.row_province, null)
        val tenSp = convertView!!.findViewById<TextView>(R.id.txtLocationName)
        tenSp.setText(getItem(position)?.name)
        return convertView
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        convertView =
            LayoutInflater.from(parent.context).inflate(R.layout.row_province_dropdown, null)
        val tenSp = convertView!!.findViewById<TextView>(R.id.txtLocationName)
        tenSp.setText(getItem(position)?.name)
        return convertView
    }
}

class SpnCityAdapter(context: Context, resource: Int, objects: List<City>) : ArrayAdapter<City>(context, resource, objects) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        convertView = LayoutInflater.from(parent.context).inflate(R.layout.row_province, null)
        val tenSp = convertView!!.findViewById<TextView>(R.id.txtLocationName)
        tenSp.setText(getItem(position)?.name)
        return convertView
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        convertView =
            LayoutInflater.from(parent.context).inflate(R.layout.row_province_dropdown, null)
        val tenSp = convertView!!.findViewById<TextView>(R.id.txtLocationName)
        tenSp.setText(getItem(position)?.name)
        return convertView
    }
}

class AddLocationAct : AppCompatActivity() {

    val lstDistrict: ArrayList<Districts> = ArrayList()
    val lstWards: ArrayList<Wards> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_add_location)
        lstWards.add(Wards("<Chọn quận/huyện>",0))
        lstDistrict.add(Districts("<Chọn thành phố>",0,lstWards))
        setControl()
    }

    private fun setControl() {
        spnDistricts.adapter = SpnDistrictAdapter(this@AddLocationAct,R.layout.row_province,lstDistrict)
        spnWards.adapter = SpnWardAdapter(this@AddLocationAct,R.layout.row_province,lstWards)

        val url = "https://provinces.open-api.vn/api/?depth=3"
        val request = Request.Builder().url(url).build()
        val client = OkHttpClient()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                this@AddLocationAct.runOnUiThread {
                    Toast.makeText(
                        this@AddLocationAct,
                        "Lỗi kết nối internet",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val jsonString = response.body?.string()
                val listCityType = object : TypeToken<ArrayList<City>>() {}.type

                var lstCities = Gson().fromJson<ArrayList<City>>(jsonString,listCityType)
                this@AddLocationAct.runOnUiThread({
                    spnCities.adapter = SpnCityAdapter(this@AddLocationAct,R.layout.row_province,lstCities)
                    setEvent()
                })
            }
        })
    }

    private fun setEvent() {
        spnCities.setOnItemSelectedListener(object :AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
               val city: City = parent?.selectedItem as City
                this@AddLocationAct.runOnUiThread{
                    spnDistricts.adapter = SpnDistrictAdapter(this@AddLocationAct,R.layout.row_province,city.districts)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                return;
            }
        })

        spnDistricts.setOnItemSelectedListener(object :AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val districts: Districts = parent?.selectedItem as Districts
                this@AddLocationAct.runOnUiThread{
                    spnWards.adapter = SpnWardAdapter(this@AddLocationAct,R.layout.row_province,districts.wards)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                return;
            }
        })

        bttAddLocation.setOnClickListener {
            val ward : Wards = spnWards.selectedItem as Wards
            val districts: Districts = spnDistricts.selectedItem as Districts
            val city: City = spnCities.selectedItem as City
            var address: String = editHouseAddress.text.toString()

            if (address.isBlank()){
                AlertDialog.Builder(this)
                    .setTitle("Chưa điền đủ thông tin")
                    .setMessage("Địa chỉ nhà không được để trống !")
                    .setPositiveButton("Đã hiểu",null)
                    .show()
            }else {
                address = address
                val userAddress = UserAddress(address,ward.name+", "+districts.name+", "+city.name,"Vietnam","","")
                val sharePref = getSharedPreferences("userData", MODE_PRIVATE)
                var lstAddressJson : String = sharePref.getString(getString(R.string.userAdresses),"")!!
                var defaultAddressJson : String = sharePref.getString(getString(R.string.defaultAddress),"")!!
                var lstUserAddress = ArrayList<UserAddress>()

                if (defaultAddressJson.isBlank()){
                    val defaultAddress = Gson().toJson(userAddress)
                    sharePref.edit()
                        .putString(getString(R.string.defaultAddress),defaultAddress)
                        .commit()
                }else{
                    if (!lstAddressJson.isBlank()){
                        val lstAddressType = object : TypeToken<ArrayList<UserAddress>>() {}.type
                        lstUserAddress = Gson().fromJson(lstAddressJson,lstAddressType)
                    }
                    lstUserAddress.add(userAddress)
                    lstAddressJson = Gson().toJson(lstUserAddress)
                    sharePref.edit()
                        .putString(getString(R.string.userAdresses),lstAddressJson)
                        .commit()
                }
                setResult(RESULT_OK)
                finish()
            }
        }
    }

    fun goBack(view: android.view.View) {
        onBackPressed()
    }
}















