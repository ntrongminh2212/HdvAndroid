package com.example.myapplication.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.MyOrdersActivity
import com.example.myapplication.R
import com.example.myapplication.entities.Order
import com.example.myapplication.entities.OrderStatus
import com.google.android.material.internal.ContextUtils.getActivity
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat

class OrderViewHolder(val view:View){
    var lstOrderItem :ListView
    var txtTotalOrder: TextView
    var txtOrderId: TextView
    var bttAction: Button
    var txtOrderDate: TextView
    init {
        lstOrderItem = view.findViewById(R.id.lstOrderItems)
        txtTotalOrder = view.findViewById(R.id.txtTotalOrder)
        txtOrderId = view.findViewById(R.id.txtOrderId)
        bttAction = view.findViewById(R.id.bttAction)
        txtOrderDate = view.findViewById(R.id.txtOrderDate)
    }
}
class OrderAdapter(var context: Activity,var lstOrders:ArrayList<Order>):BaseAdapter() {

    val dateFormat : SimpleDateFormat = SimpleDateFormat(context.getString(R.string.dateFormat))
    override fun getCount(): Int {
        return lstOrders.size
    }

    override fun getItem(position: Int): Any {
        return lstOrders.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view: View
        var viewHolder: OrderViewHolder
        if (convertView==null){
            val layoutInflater = LayoutInflater.from(context)
            view = layoutInflater.inflate(R.layout.order_row,null)
            viewHolder = OrderViewHolder(view)
            view.tag = viewHolder
        }else{
            view = convertView
            viewHolder = view.tag as OrderViewHolder
        }
        viewHolder.txtOrderId.text = lstOrders.get(position)._id
        viewHolder.txtTotalOrder.text = lstOrders.get(position).totalPrice.toString()+" $"
        viewHolder.lstOrderItem.adapter = OrderItemAdapter(context,lstOrders.get(position).orderItems, lstOrders.get(position).orderStatus)
        viewHolder.txtOrderDate.text = dateFormat.format(lstOrders.get(position).createAt)
        justifyListViewHeightBasedOnChildren(viewHolder.lstOrderItem)
        setViewListener(viewHolder,lstOrders.get(position))
        return view
    }

    fun setViewListener(viewHolder: OrderViewHolder,order:Order) {
        var button = viewHolder.bttAction
        when(order.orderStatus){
            OrderStatus.Processing ->{
                button.setText("Huỷ đơn hàng")
                button.setOnClickListener {
                    AlertDialog.Builder(context)
                        .setTitle("Hủy đơn hàng")
                        .setMessage("Bạn có chắc muốn hủy đơn hàng này không?")
                        .setPositiveButton("Hãy hủy đơn hàng", { dialog, which ->
                            deleteOrder(order._id)
                        }).setNegativeButton("Bỏ",null)
                        .show()
                }
            }
            OrderStatus.Confirmed->{
                button.setText("Huỷ đơn hàng")
                button.setOnClickListener {
                    AlertDialog.Builder(context)
                        .setTitle("Hủy đơn hàng")
                        .setMessage("Bạn có chắc muốn hủy đơn hàng này không?")
                        .setPositiveButton("Hãy hủy đơn hàng", { dialog, which ->
                            deleteOrder(order._id)
                        }).setNegativeButton("Bỏ",null)
                        .show()
                }
            }
            OrderStatus.Delivered->{
                button.setText("Tôi đã nhận hàng!")
                button.setOnClickListener{
                    receiveProduct(order._id)
                }
            }
            OrderStatus.Complete->{
                button.visibility=View.GONE
            }
        }
    }

    fun receiveProduct(orderId:String){
        val url = context.getString(R.string.putUpdateOrder)+orderId
        val sharePref = context.getSharedPreferences("userData", AppCompatActivity.MODE_PRIVATE)
        val userToken = sharePref.getString(context.getString(R.string.token),null)
        val params = JSONObject().put("userToken",userToken)
        var jsonSend = JSONObject()
        jsonSend.put("params",params)
        jsonSend.put("orderStatus",OrderStatus.Complete.name)

        val jsonType = context.getString(R.string.mediaTypeJSON).toMediaTypeOrNull()
        val reqBody = RequestBody.create(jsonType,jsonSend.toString())
        val request = Request.Builder().put(reqBody).url(url).build()
        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            @SuppressLint("RestrictedApi")
            override fun onFailure(call: Call, e: IOException) {
                getActivity(context)?.runOnUiThread(){
                    Toast.makeText(
                        context,
                        "Lỗi kết nối internet",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            @SuppressLint("RestrictedApi")
            override fun onResponse(call: Call, response: Response) {
                val resBody = JSONObject(response.body?.string())
                if (resBody.has("success")) {
                    getActivity(context)?.runOnUiThread {
                        Toast.makeText(
                            context,
                            "Cảm ơn bạn, mong bạn sẽ hài lòng với sản phẩm",
                            Toast.LENGTH_SHORT
                        ).show()
                        context.finish()
                    }
                }
            }
        })
    }

    fun deleteOrder(orderId:String){
        val url = context.getString(R.string.deleteOrder)+orderId
        val sharePref = context.getSharedPreferences("userData", AppCompatActivity.MODE_PRIVATE)
        val userToken = sharePref.getString(context.getString(R.string.token),null)
        val params = JSONObject().put("userToken",userToken)
        var jsonSend = JSONObject()
        jsonSend.put("params",params)

        val jsonType = context.getString(R.string.mediaTypeJSON).toMediaTypeOrNull()
        val reqBody = RequestBody.create(jsonType,jsonSend.toString())
        val request = Request.Builder().delete(reqBody).url(url).build()
        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            @SuppressLint("RestrictedApi")
            override fun onFailure(call: Call, e: IOException) {
                getActivity(context)?.runOnUiThread(){
                    Toast.makeText(
                        context,
                        "Lỗi kết nối internet",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            @SuppressLint("RestrictedApi")
            override fun onResponse(call: Call, response: Response) {
                val resBody = JSONObject(response.body?.string())
                if (resBody.has("success")) {
                    getActivity(context)?.runOnUiThread {
                        Toast.makeText(
                            context,
                            "Đã hủy đơn hàng",
                            Toast.LENGTH_SHORT
                        ).show()
                        context.finish()
                    }
                }
            }
        })
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
}