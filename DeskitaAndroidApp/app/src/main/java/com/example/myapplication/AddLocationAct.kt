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
import com.example.myapplication.entities.City
import com.example.myapplication.entities.Districts
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
    }

    fun goBack(view: android.view.View) {
        onBackPressed()
    }
}















