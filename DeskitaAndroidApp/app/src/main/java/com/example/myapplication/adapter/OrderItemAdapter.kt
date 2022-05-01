package com.example.myapplication.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.example.myapplication.ProductDetailActivity
import com.example.myapplication.R
import com.example.myapplication.ReviewProductActivity
import com.example.myapplication.entities.CartItem
import com.example.myapplication.entities.OrderStatus
import com.example.myapplication.entities.ProductContainer
import com.google.gson.GsonBuilder
import com.squareup.picasso.Picasso
import okhttp3.*
import java.io.IOException

class OrderItemViewHolder(rowView:View){
    var imgOrderProduct: ImageView
    var txtProductName: TextView
    var txtProductPrice: TextView
    var txtQuantities: TextView
    var txtItemsTotal: TextView
    var bttRateProduct: Button
    init {
        imgOrderProduct = rowView.findViewById(R.id.imgOrderProduct)
        txtProductName = rowView.findViewById(R.id.txtProductName)
        txtProductPrice = rowView.findViewById(R.id.txtProductPrice)
        txtQuantities = rowView.findViewById(R.id.txtItemAmount)
        txtItemsTotal = rowView.findViewById(R.id.txtItemsTotal)
        bttRateProduct = rowView.findViewById(R.id.bttRateProduct)
    }
}
class OrderItemAdapter(var context: Context, val lstCartItems: ArrayList<CartItem>, val orderStatus: OrderStatus) : BaseAdapter(){

    var layoutInflater: LayoutInflater? = null
    override fun getCount(): Int {
        return  lstCartItems.size
    }

    override fun getItem(position: Int): CartItem {
        return lstCartItems.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view: View?
        var viewHolder: OrderItemViewHolder
        if (convertView == null) {
            layoutInflater = LayoutInflater.from(context)
            view = layoutInflater!!.inflate(R.layout.orderitem_row, null)
            viewHolder = OrderItemViewHolder(view)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as OrderItemViewHolder
        }
        viewHolder.txtProductName.text = lstCartItems.get(position).name
        viewHolder.txtProductPrice.text = lstCartItems.get(position).price.toString()+"$"
        viewHolder.txtQuantities.setText("x"+lstCartItems.get(position).quantity.toString())
        Picasso.get().load(lstCartItems.get(position).image)
            .into(viewHolder.imgOrderProduct)
        viewHolder.txtItemsTotal.text = lstCartItems.get(position).total.toString()+"$"
        if (orderStatus == OrderStatus.Complete){
            viewHolder.bttRateProduct.visibility = View.VISIBLE
            setEventReviewProduct(viewHolder.bttRateProduct, lstCartItems.get(position).product);
        }
        return view as View
    }

    private fun setEventReviewProduct(bttRateProduct: Button, productId:String) {
        bttRateProduct.setOnClickListener {
            val intent = Intent(context,ReviewProductActivity::class.java)
            intent.putExtra("productId",productId)
            context.startActivity(intent)
        }
    }

}