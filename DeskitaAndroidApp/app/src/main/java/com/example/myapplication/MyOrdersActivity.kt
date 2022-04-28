package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.myapplication.entities.MyOrder
import com.example.myapplication.entities.Order
import com.example.myapplication.entities.OrderStatus
import com.example.myapplication.fragment.orderstatus.CompletedFragment
import com.example.myapplication.fragment.orderstatus.ConfirmedFragment
import com.example.myapplication.fragment.orderstatus.DeliveredFragment
import com.example.myapplication.fragment.orderstatus.ProcessingFragment
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.act_my_orders.*
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrl
import java.io.IOException

class OrderStatusPagerAdapter(fragmentManager: FragmentManager): FragmentPagerAdapter(fragmentManager) {

    val lstOrderStatusFrags = ArrayList<Fragment>()
    override fun getCount(): Int {
        return lstOrderStatusFrags.size
    }

    override fun getItem(position: Int): Fragment {
        return  lstOrderStatusFrags.get(position)
    }

    fun addFragment(fragment: Fragment){
        lstOrderStatusFrags.add(fragment)
    }
}

class MyOrdersActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_my_orders)
        callGetMyOrder()
    }

    fun callGetMyOrder(){
        val sharePref = getSharedPreferences("userData", MODE_PRIVATE)
        val userToken = sharePref.getString(getString(R.string.token),null)
        val url = getString(R.string.getMyOrders).toHttpUrl().newBuilder()
            .addQueryParameter("userToken",userToken).build()

        val request = Request.Builder().get().url(url).build()
        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback{
            override fun onFailure(call: Call, e: IOException) {
                this@MyOrdersActivity.runOnUiThread {
                    Toast.makeText(this@MyOrdersActivity, "Lỗi kết nối internet", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val resBody = response.body?.string()
                val gson = GsonBuilder().create()
                val myOrder = gson.fromJson(resBody,MyOrder::class.java)
                this@MyOrdersActivity.runOnUiThread{
                    classifyOrdersByStatus(myOrder.orders)
                }
            }
        })
    }

    private fun classifyOrdersByStatus(orders: ArrayList<Order>) {
        val lstProcessOrders: ArrayList<Order> = arrayListOf()
        val lstConfirmedOrders: ArrayList<Order> = arrayListOf()
        val lstDeliveredOrders: ArrayList<Order> = arrayListOf()
        val lstCompletedOrders: ArrayList<Order> = arrayListOf()
        for (order in orders){
            for (item in order.orderItems){
                item.total = item.price*item.quantity
            }
            if (order.orderStatus==OrderStatus.Processing){
                lstProcessOrders.add(order)
                continue
            }else if (order.orderStatus==OrderStatus.Confirmed){
                lstConfirmedOrders.add(order)
                continue
            }else if (order.orderStatus==OrderStatus.Delivered){
                lstDeliveredOrders.add(order)
                continue
            }else if (order.orderStatus==OrderStatus.Complete){
                lstCompletedOrders.add(order)
                continue
            }
        }
        setTabPager(lstProcessOrders,lstConfirmedOrders,lstDeliveredOrders,lstCompletedOrders)
    }

    fun setTabPager(lstProcessOrders: ArrayList<Order>,
                    lstConfirmedOrders: ArrayList<Order>,
                    lstDeliveredOrders: ArrayList<Order>,
                    lstCompletedOrders: ArrayList<Order>,){
        val adapter = OrderStatusPagerAdapter(supportFragmentManager)
        adapter.addFragment(ProcessingFragment(lstProcessOrders,this))
        adapter.addFragment(ConfirmedFragment(lstConfirmedOrders,this))
        adapter.addFragment(DeliveredFragment(lstDeliveredOrders,this))
        adapter.addFragment(CompletedFragment(lstCompletedOrders,this))
        vpOrderStatus.adapter = adapter
        tabOrderStatus.setupWithViewPager(vpOrderStatus)

        tabOrderStatus.getTabAt(0)!!.setText("Chờ xử lý")
        tabOrderStatus.getTabAt(1)!!.setText("Xác nhận")
        tabOrderStatus.getTabAt(2)!!.setText("Đang giao")
        tabOrderStatus.getTabAt(3)!!.setText("Đã nhận")
    }

    fun goBack(view: android.view.View) {
        onBackPressed()
    }
}