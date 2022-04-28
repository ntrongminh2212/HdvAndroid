package com.example.myapplication.fragment.orderstatus

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.myapplication.MyOrdersActivity
import com.example.myapplication.R
import com.example.myapplication.adapter.OrderAdapter
import com.example.myapplication.entities.Order
import kotlinx.android.synthetic.main.frag_completed.*

class CompletedFragment(var lstCompletedOrders: ArrayList<Order>,var activity: MyOrdersActivity) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.frag_completed, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lstvComplOrders.adapter = OrderAdapter(activity,lstCompletedOrders)
    }
}