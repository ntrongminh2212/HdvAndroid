package com.example.myapplication.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.myapplication.R
import com.example.myapplication.entities.UserAddress
import com.google.gson.Gson

class LocationViewHolder(view: View){
    var txtTittle:TextView
    var bttDeleteLocation:TextView
    var txtHouseAdress: TextView
    var txtTown: TextView

    init {
        txtTittle = view.findViewById(R.id.txtTittle)
        bttDeleteLocation = view.findViewById(R.id.bttDeleteLocation)
        txtHouseAdress = view.findViewById(R.id.txtHouseAdrress)
        txtTown = view.findViewById(R.id.txtTown)
    }
}

class LocationAdapter(val context: Context, var lstUserAdresses : ArrayList<UserAddress>) :BaseAdapter(){
    override fun getCount(): Int {
        return lstUserAdresses.size
    }

    override fun getItem(p: Int): UserAddress{
        return lstUserAdresses.get(p)
    }

    override fun getItemId(p: Int): Long {
        return p.toLong()
    }

    override fun getView(p: Int, convertView: View?, parent: ViewGroup?): View {
        var viewLocationRow :View
        var viewHolder:LocationViewHolder
        if (convertView==null){
            viewLocationRow = LayoutInflater.from(context).inflate(R.layout.location_row,null)
            viewHolder = LocationViewHolder(viewLocationRow)
            viewLocationRow.tag = viewHolder
        }else{
            viewLocationRow = convertView
            viewHolder = viewLocationRow.tag as LocationViewHolder
        }

        if (p==0) {
            viewHolder.txtTittle.setText("Địa chỉ mặc định:")
            val icLocation: Drawable = context.getDrawable(R.drawable.ic_location_on_24)!!
            viewHolder.txtTittle.setCompoundDrawables(icLocation,null,null,null)
        }else{
            viewHolder.txtTittle.setText("Địa chỉ "+(p+1).toString()+":")
            viewHolder.txtTittle.setCompoundDrawables(null,null,null,null)
        }
        viewHolder.txtHouseAdress.text = lstUserAdresses.get(p).address
        viewHolder.txtTown.text = lstUserAdresses.get(p).city
        viewHolder.bttDeleteLocation.setOnClickListener {
            deleteLocation(lstUserAdresses.get(p),p)
        }
        return viewLocationRow
    }

    private fun deleteLocation(userAddress: UserAddress,p: Int) {
        AlertDialog.Builder(context)
            .setTitle("Xóa địa chỉ")
            .setMessage("Địa chỉ này sẽ mất vĩnh viễn, bạn có chắc muốn xóa không?")
            .setPositiveButton("Xoá ngay",{ dialogInterface: DialogInterface, i: Int ->
                lstUserAdresses.remove(userAddress)
                if(p==0){
                    val defaultUserAddress = lstUserAdresses.get(0)
                    val defaultAddressJson = Gson().toJson(defaultUserAddress)
                    val sharedPref = context.getSharedPreferences("userData", Context.MODE_PRIVATE)
                    sharedPref.edit()
                        .putString(context.getString(R.string.defaultAddress), defaultAddressJson)
                        .commit()
                }else {
                    val lstOtherAddresses =  ArrayList<UserAddress>(lstUserAdresses.subList(1,lstUserAdresses.size))
                    val otherAddressesJson = Gson().toJson(lstOtherAddresses)
                    val sharedPref = context.getSharedPreferences("userData", Context.MODE_PRIVATE)
                    sharedPref.edit()
                        .putString(context.getString(R.string.userAdresses), otherAddressesJson)
                        .commit()
                }
                notifyDataSetChanged()
            })
            .setNegativeButton("Bỏ",null)
            .show()
    }
}













