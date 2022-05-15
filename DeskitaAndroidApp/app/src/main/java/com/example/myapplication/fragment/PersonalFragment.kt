package com.example.myapplication.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.myapplication.*
import com.example.myapplication.entities.User
import com.example.myapplication.entities.UserContainer
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.frag_personal.*
import okhttp3.*
import okhttp3.HttpUrl.Companion.get
import okhttp3.HttpUrl.Companion.toHttpUrl
import org.json.JSONObject
import java.io.IOException

class ViewHolder(row: View) {
    var imgIcon: ImageView
    var txtPersonalOpt: TextView

    init {
        imgIcon = row.findViewById(R.id.imgIcon)
        txtPersonalOpt = row.findViewById(R.id.txtPersonalOpt)
    }
}

class PersonalOpAdapter(
    var context: Context,
    var listPersonalOpts: List<String>,
    var icons: IntArray
) : BaseAdapter() {

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
        if (viewConvert == null) {
            var layoutInflater = LayoutInflater.from(context)
            view = layoutInflater.inflate(R.layout.personalopt_row, null)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        } else {
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
        return inflater.inflate(R.layout.frag_personal, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setContent()
    }

    fun setContent() {
        try {
            val sharePref = requireContext().getSharedPreferences("userData", Context.MODE_PRIVATE)
            val userToken: String = sharePref.getString(getString(R.string.token), "")!!
            val userMeJson = sharePref.getString(getString(R.string.userMeJson), "")
            val user: User = Gson().fromJson(userMeJson, User::class.java)

            Picasso.get().load(user.avatar.url).placeholder(R.drawable.user_avatar).into(imgAvatar)
            txtFullName1.text = user.name
            var listPersonalOption: List<String> = listOf(
                "Thông tin cá nhân", "Đơn đặt hàng của bạn", "Đăng xuất"
            )
            var icons: IntArray = intArrayOf(
                R.drawable.ic_person_info_24, R.drawable.ic_shipping_24, R.drawable.ic_logout
            )

            lstPersonalOption.adapter =
                PersonalOpAdapter(requireContext(), listPersonalOption, icons)
            lstPersonalOption.setOnItemClickListener { adapterView, view, position, id ->
                if (listPersonalOption.get(position).compareTo("Thông tin cá nhân", true) == 0) {
                    val intent = Intent(requireContext(), PersonalInfoActivity::class.java)
                    startActivity(intent)
                }
                if (listPersonalOption.get(position).compareTo("Đơn đặt hàng của bạn", true) == 0) {
                    val intent = Intent(requireContext(), MyOrdersActivity::class.java)
                    startActivity(intent)
                }
                if (listPersonalOption.get(position).compareTo("Đăng xuất", true) == 0) {
                    logout()
                }
            }
        } catch (ex:Exception) {
            bttLogin.visibility = View.VISIBLE
            bttLogin.setOnClickListener {
                (activity as MainActivity).actLogin()
            }
        }
    }

    fun logout() {
        val url = getString(R.string.getLogout).toHttpUrl()
        val request = Request.Builder().get().url(url).build()
        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                return
            }

            override fun onResponse(call: Call, response: Response) {
                val resBody = JSONObject(response.body?.string())
                if (resBody.has("success")) {
                    activity?.runOnUiThread {
                        val sharePref =
                            requireContext().getSharedPreferences("userData", Context.MODE_PRIVATE)
                        with(sharePref.edit()) {
                            putString(getString(R.string.token), "")
                                .commit()
                            putString(getString(R.string.email), "")
                                .commit()
                            putString(getString(R.string.password), "")
                                .commit()
                        }
                        (activity as MainActivity).setCurrentFragment(PersonalFragment())
                    }
                }
            }
        })
    }
}