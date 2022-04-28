package com.example.myapplication.fragment.orderstatus

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.myapplication.MyOrdersActivity
import com.example.myapplication.R
import com.example.myapplication.adapter.OrderAdapter
import com.example.myapplication.entities.Order
import kotlinx.android.synthetic.main.frag_delivered.*
import kotlinx.android.synthetic.main.frag_processing.*

class DeliveredFragment(var lstDeliveredOrders: ArrayList<Order>,var activity: MyOrdersActivity) : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.frag_delivered, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lstvDeliverOrders.adapter = OrderAdapter(activity,lstDeliveredOrders)
    }
}