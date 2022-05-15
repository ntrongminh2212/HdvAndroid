package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.entities.UserContainer
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.act_login.*
import kotlinx.android.synthetic.main.act_result_search.*
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.IOException

class LoginActivity : AppCompatActivity() {

    var loginUrl: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_login)
    }

    fun loginAuthen(username: String, password: String){
        loginUrl = getString(R.string.postLogin)

        val accountJson: JSONObject = JSONObject()
        accountJson.put("email", username)
        accountJson.put("password", password)

        val JSON: MediaType? = getString(R.string.mediaTypeJSON).toMediaTypeOrNull()
        val rqBody: RequestBody = RequestBody.create(JSON, accountJson.toString())
        val request: Request = Request.Builder().post(rqBody).url(loginUrl).build()
        val client = OkHttpClient()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                this@LoginActivity.runOnUiThread({
                    val res = response.body?.string()

                    var resJson: JSONObject
                    try {
                        resJson = JSONObject(res)
                        if (resJson.getBoolean("success") == true) {
                            val userDataJson = resJson.getJSONObject("user")
                            val sharedPref = getSharedPreferences("userData", MODE_PRIVATE)
                            with(sharedPref.edit()) {
                                putString(getString(R.string.email), userDataJson.getString("email").toString()).commit()
                                putString(getString(R.string._id), userDataJson.getString("_id").toString()).commit()
                                putString(getString(R.string.password), password).commit()
                                putString(getString(R.string.token), resJson.getString("token").toString()).commit()
                            }
                            getPersonalInfo(resJson.getString("token").toString())
                            val data = Intent()
                            data.putExtra("token",resJson.getString("token").toString())
                            setResult(RESULT_OK,data)
                            finish()
                        } else {
                            val toast = Toast.makeText(
                                this@LoginActivity,
                                "Sai tên đăng nhập hoặc mật khẩu",
                                Toast.LENGTH_SHORT
                            )
                            toast.setGravity(Gravity.CENTER, 0, 0)
                            toast.show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(this@LoginActivity, e.message, Toast.LENGTH_SHORT)
                            .show()
                    }
                })
            }

            override fun onFailure(call: Call, e: IOException) {
                this@LoginActivity.runOnUiThread(Runnable {
                    val toast = Toast.makeText(
                        this@LoginActivity,
                        "Lỗi kết nối tới máy chủ",
                        Toast.LENGTH_SHORT
                    )
                    toast.setGravity(Gravity.CENTER, 0, 0)
                    toast.show()
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
                this@LoginActivity.runOnUiThread {
                    Toast.makeText(
                        this@LoginActivity,
                        "Lỗi kết nối mạng", Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val resBody = response.body?.string()
                val gson = GsonBuilder().create()
                val userContainer = gson.fromJson(resBody, UserContainer::class.java)
                this@LoginActivity.runOnUiThread {
                    val userMeJson = gson.toJson(userContainer.user)
                    val sharedPref = getSharedPreferences("userData", MODE_PRIVATE)

                    sharedPref.edit()
                        .putString(getString(R.string.userMeJson),userMeJson)
                        .commit()
                }
            }
        })
    }

    fun login(view: android.view.View) {
        var email: String = txtEmail.text.toString()
        var password: String = txtPassword.text.toString()
        loginAuthen(email,password)
    }

    fun actRegistry(view: android.view.View) {
        val intentRegister: Intent = Intent(this,RegisterActivity::class.java)
        startActivity(intentRegister)
    }

    fun goBack(view: View) {
        finish()
    }
}