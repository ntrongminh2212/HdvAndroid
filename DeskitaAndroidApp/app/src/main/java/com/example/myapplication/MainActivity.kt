package com.example.myapplication

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.entities.Product
import com.example.myapplication.entities.User
import com.example.myapplication.entities.UserContainer
import com.example.myapplication.fragment.HomeFragment
import com.example.myapplication.fragment.PersonalFragment
import com.example.myapplication.fragment.TopdeskitaFragment
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.act_main.*
import kotlinx.android.synthetic.main.frag_home.*
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {

    lateinit var userToken:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_main)
        val sharedPref = getSharedPreferences("userData",Context.MODE_PRIVATE)
        val email = sharedPref.getString(getString(R.string.email),"")!!
        val password = sharedPref.getString(getString(R.string.password),"")!!
        userToken = sharedPref.getString(getString(R.string.token), "")!!
        if (!email.isBlank()&&!password.isBlank()) {
            loginAuthen(email,password)
            Toast.makeText(this, "Mừng bạn trở lại", Toast.LENGTH_SHORT).show()
        }else{
            actLogin()
        }

        setCurrentFragment(HomeFragment())
        bnavMain.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.itHomeBotNav -> setCurrentFragment(HomeFragment())
                R.id.itPersonalBotNav ->setCurrentFragment(PersonalFragment())
                //R.id.itTopSellerBotNav -> setCurrentFragment(TopdeskitaFragment())
            }
            true
        }
    }

    fun setCurrentFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragContainer,fragment)
            commit()
        }
    }

    fun loginAuthen(username: String, password: String){
        val loginUrl = getString(R.string.serverUrl) + "/user/login"
        val accountJson: JSONObject = JSONObject()
        accountJson.put("email", username)
        accountJson.put("password", password)

        val JSON: MediaType? = "application/json; charset=utf-8".toMediaTypeOrNull()
        val rqBody: RequestBody = RequestBody.create(JSON, accountJson.toString())
        val request: Request = Request.Builder().post(rqBody).url(loginUrl).build()
        val client = OkHttpClient()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                this@MainActivity.runOnUiThread({
                    val res = response.body?.string()
                    try {
                        var resJson: JSONObject
                        resJson = JSONObject(res)
                        val userToken:String = resJson.getString("token")
                        val sharePref = getSharedPreferences("userData",MODE_PRIVATE)
                        with(sharePref.edit()) {
                            putString(getString(R.string.token), userToken).commit()
                        }
                        getPersonalInfo(userToken)
                    } catch (e: Exception) {
                        actLogin()
                    }
                })
            }

            override fun onFailure(call: Call, e: IOException) {
                this@MainActivity.runOnUiThread(Runnable {
                    kotlin.run {
                        val toast = Toast.makeText(this@MainActivity, "Lỗi kết nối tới máy chủ",Toast.LENGTH_SHORT)
                        toast.setGravity(Gravity.CENTER,0,0)
                    }
                })
            }
        })
    }

    fun getPersonalInfo(userToken:String){
        val url: HttpUrl = getString(R.string.getPersonalInfo).toHttpUrl()
            .newBuilder().addQueryParameter("userToken", userToken).build()

        val request = Request.Builder().get().url(url).build()
        val client: OkHttpClient = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                this@MainActivity.runOnUiThread {
                    Toast.makeText(
                        this@MainActivity,
                        "Lỗi kết nối mạng", Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val resBody = response.body?.string()
                val gson = GsonBuilder().create()
                val userContainer = gson.fromJson(resBody, UserContainer::class.java)
                this@MainActivity.runOnUiThread {
                    val userMeJson = gson.toJson(userContainer.user)
                    val sharedPref = getSharedPreferences("userData", MODE_PRIVATE)

                    sharedPref.edit()
                        .putString(getString(R.string.userMeJson),userMeJson)
                        .commit()
                }
            }
        })
    }

    fun startActMyCart(item: android.view.MenuItem) {
        val sharedPref = getSharedPreferences("userData",Context.MODE_PRIVATE)
        userToken = sharedPref.getString(getString(R.string.token), "")!!
        if (userToken.isBlank())
        {
            actLogin()
        }else {
            var intent: Intent = Intent(this, MyCartActivity::class.java)
            startActivity(intent)
        }
    }

    fun startActSearchProduct(item: android.view.MenuItem) {
        var intent: Intent = Intent(this,SearchProductsActivity::class.java)
        startActivity(intent)
    }

    fun actLogin() {
        val intent = Intent(this,LoginActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_up,R.anim.slide_out_down)
    }
}