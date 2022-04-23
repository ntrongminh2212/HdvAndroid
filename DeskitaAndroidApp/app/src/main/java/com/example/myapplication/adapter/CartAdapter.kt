package com.example.myapplication.adapter

import android.content.Context
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.widget.doAfterTextChanged
import com.example.myapplication.MyCartActivity
import com.example.myapplication.R
import com.example.myapplication.entities.CartItem
import com.example.myapplication.entities.Product
import com.squareup.picasso.Picasso
import org.w3c.dom.Text

internal class CartViewHolder(private val row: View) {
    var chbItemSelected: CheckBox
    var imgProduct: ImageView
    var txtProductName: TextView
    var txtPrice: TextView
    var txtQuantities:EditText
    var bttIncreaseQuan:ImageButton
    var bttDecreaseQuan:ImageButton

    init {
        chbItemSelected = row.findViewById(R.id.chbItemSelect)
        imgProduct = row.findViewById(R.id.imgAddProduct)
        txtProductName = row.findViewById(R.id.txtAddProductName)
        txtPrice = row.findViewById(R.id.txtAddProductPrice)
        txtQuantities = row.findViewById(R.id.txtQuantities)
        bttIncreaseQuan = row.findViewById(R.id.bttIncreaseQuan)
        bttDecreaseQuan = row.findViewById(R.id.bttDecreaseQuan)
    }
}

class CartAdapter(var context: Context, var lstCartItems: ArrayList<CartItem>,val txtTotalPay:TextView) : BaseAdapter() {
    var layoutInflater: LayoutInflater? = null
    var TotalPay:Double = 0.0

    override fun getCount(): Int {
        return lstCartItems.size
    }

    override fun getItem(position: Int): CartItem {
        return lstCartItems.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val viewHolder: CartViewHolder
        val view: View?
        if (convertView == null) {
            layoutInflater = LayoutInflater.from(context)
            view = layoutInflater!!.inflate(R.layout.cartitem_row, null)
            viewHolder = CartViewHolder(view)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as CartViewHolder
        }
        viewHolder.txtProductName.text = lstCartItems.get(position).name
        viewHolder.txtPrice.text = lstCartItems.get(position).price.toString()+"$"
        viewHolder.txtQuantities.setText(lstCartItems.get(position).quantity.toString())
        viewHolder.chbItemSelected.isChecked = lstCartItems.get(position).isSelect
        Picasso.get().load(lstCartItems.get(position).image)
            .into(viewHolder.imgProduct)
        Log.d("callGetView","position: "+lstCartItems.get(position).isSelect)
        setViewListener(viewHolder,lstCartItems.get(position))

        return view as View
    }

    private fun setViewListener(viewHolder: CartViewHolder,cartItem: CartItem) {
        viewHolder.chbItemSelected.setOnClickListener({
            val checkBox =viewHolder.chbItemSelected
            cartItem.isSelect = checkBox.isChecked
            if (checkBox.isChecked){
                TotalPay = TotalPay + cartItem.total
            }else
                TotalPay = TotalPay - cartItem.total
            txtTotalPay.setText(TotalPay.toString()+" $")
            notifyDataSetChanged()
        })

        viewHolder.bttDecreaseQuan.setOnClickListener{
            var quan = Integer.parseInt(viewHolder.txtQuantities.text.toString())
            quan--
            if (quan>=0&&quan<=30){
                if (cartItem.isSelect){
                    TotalPay = TotalPay-cartItem.total
                }
                cartItem.quantity = quan
                cartItem.total = cartItem.price*cartItem.quantity
                if (cartItem.isSelect){
                    TotalPay = TotalPay+cartItem.total
                    txtTotalPay.setText(TotalPay.toString()+" $")
                }
                notifyDataSetChanged()
            }
        }

        viewHolder.bttIncreaseQuan.setOnClickListener{
            var quan = Integer.parseInt(viewHolder.txtQuantities.text.toString())
            quan++
            if (quan>=0&&quan<=30){
                if (cartItem.isSelect){
                    TotalPay = TotalPay-cartItem.total
                }
                cartItem.quantity = quan
                cartItem.total = cartItem.price*cartItem.quantity
                if (cartItem.isSelect){
                    TotalPay = TotalPay+cartItem.total
                    txtTotalPay.setText(TotalPay.toString()+" $")
                }
                notifyDataSetChanged()
            }
        }

        viewHolder.txtQuantities.setOnEditorActionListener(object :TextView.OnEditorActionListener{
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                var quan = Integer.parseInt(viewHolder.txtQuantities.text.toString())
                if (quan>=0&&quan<=30){
                    if (cartItem.isSelect){
                        TotalPay = TotalPay-cartItem.total
                    }
                    cartItem.quantity = quan
                    cartItem.total = cartItem.price*cartItem.quantity
                    if (cartItem.isSelect){
                        TotalPay = TotalPay+cartItem.total
                        txtTotalPay.setText(TotalPay.toString()+" $")
                    }
                    notifyDataSetChanged()
                }
                return true
            }
        })
    }

    fun selectAllCartItem(isChecked:Boolean){
        TotalPay = 0.0;
        for (cart in lstCartItems){
            cart.isSelect = isChecked
            TotalPay = TotalPay+cart.total
        }
        txtTotalPay.setText(TotalPay.toString()+" $")
        notifyDataSetChanged()
    }

    fun getSelectedItem(): ArrayList<CartItem>{
        val lstOderItem:ArrayList<CartItem> = arrayListOf()
        for (cart in lstCartItems){
            if (cart.isSelect) {
                lstOderItem.add(cart)
            }
        }
        return lstOderItem
    }
}