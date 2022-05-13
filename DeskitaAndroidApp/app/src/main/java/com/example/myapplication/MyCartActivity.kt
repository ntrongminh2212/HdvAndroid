package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.myapplication.adapter.CartAdapter
import com.example.myapplication.entities.Cart
import com.example.myapplication.entities.CartItem
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.act_my_cart.*
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import java.io.IOException

class MyCartActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_my_cart)
        callGetMyCart()
        chbSelectAll.setOnCheckedChangeListener { buttonView, isChecked ->
            val adapter = lstCartItems.adapter as CartAdapter
            adapter.selectAllCartItem(isChecked)
        }
    }

    private fun callGetMyCart() {
        val sharePref = getSharedPreferences("userData", MODE_PRIVATE)
        val userToken = sharePref.getString(getString(R.string.token),null)

        val url = getString(R.string.getMyCart).toHttpUrlOrNull()!!.newBuilder()
            .addQueryParameter("userToken",userToken).build()
        val request = Request.Builder().get().url(url).build()
        val okHttpClient = OkHttpClient()
        okHttpClient.newCall(request).enqueue(object :Callback{
            override fun onFailure(call: Call, e: IOException) {
                this@MyCartActivity.runOnUiThread {
                    Toast.makeText(this@MyCartActivity, "Lỗi kết nối internet", Toast.LENGTH_SHORT)
                        .show()
                }
                return
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                val gson = GsonBuilder().create()
                val cart = gson.fromJson(body,Cart::class.java)
                this@MyCartActivity.runOnUiThread({
                    lstCartItems.adapter = CartAdapter(this@MyCartActivity,cart.myCart as ArrayList<CartItem>,txtTotalPay)
                })
            }
        })
    }

    fun goBack(view: android.view.View) {
        onBackPressed()
    }

    fun orderAct(view: android.view.View) {
        val adapter = lstCartItems.adapter as CartAdapter
        val lstOrderItem = adapter.getSelectedItem()
        if (lstOrderItem.size>0){
            val intent = Intent(this,CreateOrderActivity::class.java)
            intent.putExtra("newOrders",lstOrderItem)
            startActivity(intent)
            finish()
        }else{
            Toast.makeText(this, "Hãy chọn đồ bạn muốn đặt hàng trong giỏ", Toast.LENGTH_SHORT).show()
        }
    }
}