package com.example.myapplication

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.example.myapplication.adapter.OrderItemAdapter
import com.example.myapplication.entities.CartItem
import com.example.myapplication.entities.OrderStatus
import com.example.myapplication.entities.User
import com.example.myapplication.entities.UserAddress
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.act_create_order.*
import kotlinx.android.synthetic.main.act_my_cart.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class CreateOrderActivity : AppCompatActivity() {
    var lstCartItems: ArrayList<CartItem> = arrayListOf()
    var itemsPrice: Int = 0
    private val SELECT_LOCATION_CODE = 1
    lateinit var user: User
    lateinit var receiveAddress: UserAddress

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_create_order)

        lstCartItems = intent.getSerializableExtra("newOrders") as ArrayList<CartItem>
        lstOrderItems.adapter = OrderItemAdapter(this, lstCartItems, OrderStatus.Processing)
        justifyListViewHeightBasedOnChildren(lstOrderItems)

        val sharePref = getSharedPreferences("userData", MODE_PRIVATE)
        val userMeJson = sharePref.getString(getString(R.string.userMeJson), "")
        val defaultAddressJson = sharePref.getString(getString(R.string.defaultAddress), "")
        user = Gson().fromJson(userMeJson, User::class.java)
        if (defaultAddressJson != null && !defaultAddressJson.isBlank()) {
            receiveAddress = Gson().fromJson(defaultAddressJson, UserAddress::class.java)
        }else{
            receiveAddress = UserAddress("","","Vietnam",user.phoneNumber,"12345")
        }
        setReceiveInfo()
        setItemsPrice()
    }

    fun callCreateOrder(view: android.view.View) {

        if (txtHouseAdrress.text.toString().isBlank()
            ||txtTown.text.toString().isBlank()){
            AlertDialog.Builder(this)
                .setTitle("Chưa thiết lập địa chỉ nhận hàng")
                .setMessage("Địa chỉ trống, hãy thêm địa chỉ nhận hàng")
                .setPositiveButton("OK",null)
                .show()
        }

        val url = getString(R.string.postCreateOrder)
        val sharePref = this.getSharedPreferences("userData", MODE_PRIVATE)
        val userToken = sharePref.getString(getString(R.string.token), null)
        val params = JSONObject().put("userToken", userToken)
        val data = parseJsonData()
        var jsonSend = JSONObject()
        jsonSend.put("params", params)
        jsonSend.put("data", data)

        val jsonType = getString(R.string.mediaTypeJSON).toMediaTypeOrNull()
        val reqBody = RequestBody.create(jsonType, jsonSend.toString())
        val request = Request.Builder().post(reqBody).url(url).build()
        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                this@CreateOrderActivity.runOnUiThread {
                    Toast.makeText(
                        this@CreateOrderActivity,
                        "Lỗi kết nối internet",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val resBody = JSONObject(response.body?.string())
                if (resBody.has("success")) {
                    this@CreateOrderActivity.runOnUiThread {
                        Toast.makeText(
                            this@CreateOrderActivity,
                            "Đặt hàng thành công",
                            Toast.LENGTH_SHORT
                        ).show()
                        val intent = Intent(this@CreateOrderActivity, MyOrdersActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
            }
        })
    }

    fun goBack(view: android.view.View) {
        onBackPressed()
    }

    fun justifyListViewHeightBasedOnChildren(listView: ListView) {
        val adapter: Adapter = listView.getAdapter() ?: return
        val vg: ViewGroup = listView
        var totalHeight = 0
        for (i in 0 until adapter.getCount()) {
            val listItem: View = adapter.getView(i, null, vg)
            listItem.measure(0, 0)
            totalHeight += listItem.measuredHeight
        }
        val par: ViewGroup.LayoutParams = listView.getLayoutParams()
        par.height = totalHeight + listView.getDividerHeight() * (adapter.getCount() - 1)
        listView.setLayoutParams(par)
        listView.requestLayout()
    }

    fun setItemsPrice() {
        var totalPay: Double = 0.0
        for (cart in lstCartItems) {
            totalPay = totalPay + cart.total
        }
        txtTotal.setText(totalPay.toString() + " $")
        itemsPrice = totalPay.toInt()
    }

    fun setReceiveInfo() {
        txtRecNamePhone.text = user.name + " | " + user.phoneNumber
        txtHouseAdrress.text = receiveAddress.address
        txtTown.text = receiveAddress.city
    }

    fun parseJsonData(): JSONObject {
        var data = JSONObject()
        data.put("itemsPrice", itemsPrice)

        var orderItems = JSONArray()
        for (cart in lstCartItems) {
            var item = JSONObject()
            item.put("_id", cart._id)
            item.put("product", cart.product)
            item.put("checked", cart.checked)
            item.put("name", cart.name)
            item.put("image", cart.image)
            item.put("price", cart.price)
            item.put("quantity", cart.quantity)
            item.put("category", cart.category)
            item.put("total", cart.total)
            orderItems.put(item)
        }

        data.put("orderItems", orderItems)
        var shippingInfo = JSONObject()
        shippingInfo.put("address", txtHouseAdrress.text.toString())
        shippingInfo.put("city", txtTown.text.toString())
        shippingInfo.put("country", "Vietnam")
        shippingInfo.put("phoneNo", user.phoneNumber)
        shippingInfo.put("postalCode", "12345")
        data.put("shippingInfo", shippingInfo)

        data.put("shippingPrice", 0)
        data.put("taxPrice", 0)
        data.put("totalPrice", itemsPrice)
        return data
    }

    fun actManageLocation(view: View) {
        val intent = Intent(this, ManageLocationActivity::class.java)
        intent.putExtra("purpose", 2)
        startActivityForResult(intent, SELECT_LOCATION_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == SELECT_LOCATION_CODE) {
            val address: UserAddress = data?.getSerializableExtra("address") as UserAddress
            this.receiveAddress = address
            setReceiveInfo()
        }
    }
}