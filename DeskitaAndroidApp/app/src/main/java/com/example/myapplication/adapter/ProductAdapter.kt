package com.example.myapplication.adapter

import android.content.Context
import android.widget.TextView
import com.example.myapplication.R
import com.example.myapplication.entities.Product
import android.widget.BaseAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.squareup.picasso.Picasso

internal class ViewHolder(private val row: View) {
    var imgProduct: ImageView
    var txtProductName: TextView
    var txtPrice: TextView

    init {
        imgProduct = row.findViewById(R.id.imgProduct)
        txtProductName = row.findViewById(R.id.txtProductName)
        txtPrice = row.findViewById(R.id.txtPrice)
    }
}

class ProductAdapter(var context: Context, var lstProducts: ArrayList<Product>) : BaseAdapter() {
    var layoutInflater: LayoutInflater? = null
    var isLoading: Boolean = false

    fun addMoreProducts(lstPlusProduct: List<Product>){
        lstProducts.addAll(lstPlusProduct)
        notifyDataSetChanged()
    }
    override fun getCount(): Int {
        return lstProducts.size
    }

    override fun getItem(position: Int): Product {
        return lstProducts.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val viewHolder: ViewHolder
        val view: View?
        if (convertView == null) {
            layoutInflater = LayoutInflater.from(context)
            view = layoutInflater!!.inflate(R.layout.product_row, null)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }
        viewHolder.txtProductName.text = lstProducts.get(position).name
        viewHolder.txtPrice.text = lstProducts.get(position).price.toString()+"$"
        Picasso.get().load(lstProducts.get(position).images[0].url)
            .into(viewHolder.imgProduct)
        return view as View
    }
}