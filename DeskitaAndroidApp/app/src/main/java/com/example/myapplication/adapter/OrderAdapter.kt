package com.example.myapplication.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.myapplication.R
import com.example.myapplication.entities.Order
import com.example.myapplication.entities.OrderStatus
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
class OrderAdapter(var context: Context,var lstOrders:ArrayList<Order>):BaseAdapter() {

    val dateFormat : SimpleDateFormat = SimpleDateFormat("dd/MM/yy HH:mm a")
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
        viewHolder.lstOrderItem.adapter = OrderItemAdapter(context,lstOrders.get(position).orderItems)
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

            }
            OrderStatus.Confirmed->{
                button.visibility=View.GONE
            }
            OrderStatus.Delivered->{
                button.setText("Tôi đã nhận hàng!")
            }
            OrderStatus.Complete->{
                button.setText("Đánh giá")
                button.setBackgroundColor(context.resources.getColor(R.color.star))
            }
        }
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