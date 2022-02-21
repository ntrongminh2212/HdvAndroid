package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.login.*
import kotlinx.android.synthetic.main.register.*
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class LoginActivity : AppCompatActivity() {

    var loginUrl: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_login)
        loginUrl = getString(R.string.serverUrl) + "/login"
        bttDangNhap.setOnClickListener{
            var email: String = txtEmail.text.toString()
            var password: String = txtPassword.text.toString()
            loginAuthen(email,password)
        }
        txtRegister.setOnClickListener {
            val intentRegister: Intent = Intent(this,RegisterActivity::class.java)
            startActivity(intentRegister)
        }
    }

    fun loginAuthen(username: String, password: String){
        val accountJson: JSONObject = JSONObject()
        accountJson.put("userName", username)
        accountJson.put("password", password)

        val JSON: MediaType? = MediaType.parse("application/json; charset=utf-8")
        val rqBody: RequestBody = RequestBody.create(JSON, accountJson.toString())
        val request: Request = Request.Builder().post(rqBody).url(loginUrl).build()
        val client = OkHttpClient()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call?, response: Response?) {
                this@LoginActivity.runOnUiThread(Runnable {
                    kotlin.run {
                        if (response?.code() == 201) {
                            val res = response?.body()?.string()
                            val resJson= JSONObject(res)
                            if (resJson.has("complete")) {
                                Toast.makeText(this@LoginActivity, "Đăng nhập thành công", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        } else {
                            val toast = Toast.makeText(
                                this@LoginActivity,
                                "Sai tên đăng nhập hoặc mật khẩu",
                                Toast.LENGTH_SHORT
                            )
                            toast.setGravity(Gravity.CENTER, 0, 0)
                            toast.show()
                        }
                    }
                })
            }

            override fun onFailure(call: Call, e: IOException) {
                this@LoginActivity.runOnUiThread(Runnable {
                    kotlin.run {
                        val toast = Toast.makeText(this@LoginActivity, "Lỗi kết nối tới máy chủ",Toast.LENGTH_SHORT)
                        toast.setGravity(Gravity.CENTER,0,0)
                    }
                })
            }
        })
    }
}