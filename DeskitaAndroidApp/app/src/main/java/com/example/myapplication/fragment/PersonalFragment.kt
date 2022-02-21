package com.example.myapplication.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.myapplication.R
import kotlinx.android.synthetic.main.frag_personal.*
import android.icu.text.CaseMap.Title
import android.widget.Adapter

class ViewHolder(row:View){
    var imgIcon: ImageView
    var txtPersonalOpt: TextView

    init {
        imgIcon = row.findViewById(R.id.imgIcon)
        txtPersonalOpt = row.findViewById(R.id.txtPersonalOpt)
    }
}

class PersonalOpAdapter(var context: Context, var listPersonalOpts: List<String>, var icons: IntArray): BaseAdapter(){

    override fun getCount(): Int {
        return listPersonalOpts.size
    }

    override fun getItem(p0: Int): Any {
        return listPersonalOpts.get(p0)
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getView(p0: Int, viewConvert: View?, p2: ViewGroup?): View {
        var view: View?
        var viewHolder: ViewHolder
        if (viewConvert==null){
            var layoutInflater = LayoutInflater.from(context)
            view = layoutInflater.inflate(R.layout.personalopt_row,null)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        }else{
            view = viewConvert
            viewHolder = viewConvert.tag as ViewHolder
        }
        viewHolder.txtPersonalOpt.text = listPersonalOpts.get(p0)
        viewHolder.imgIcon.setImageResource(icons.get(p0))
        return view as View
    }
}

class PersonalFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.frag_personal,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var listPersonalOption : List<String> = listOf(
            "Thông tin cá nhân"
            ,"Cửa hàng của bạn"
            ,"Lịch sử đơn hàng")

        var icons: IntArray = intArrayOf(
            R.drawable.ic_person_info_24
            ,R.drawable.ic_my_store_24
            ,R.drawable.ic_order_info_24)

        lstPersonalOption.adapter = PersonalOpAdapter(requireContext(),listPersonalOption,icons)
        lstPersonalOption.setOnItemClickListener { adapterView, view, position, id ->
            if (listPersonalOption.get(position).compareTo("Thông tin cá nhân",true)==0){

            }
            if (listPersonalOption.get(position).compareTo("Cửa hàng của bạn",true)==0){

            }
            if (listPersonalOption.get(position).compareTo("Lịch sử đơn hàng",true)==0){

            }
        }

    }
 }