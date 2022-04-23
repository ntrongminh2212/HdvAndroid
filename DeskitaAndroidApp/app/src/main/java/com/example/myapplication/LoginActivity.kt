package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.act_login.*
import kotlinx.android.synthetic.main.act_result_search.*
import okhttp3.*
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
                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            startActivity(intent)
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

    fun login(view: android.view.View) {
        var email: String = txtEmail.text.toString()
        var password: String = txtPassword.text.toString()
        loginAuthen(email,password)
    }
    fun actRegistry(view: android.view.View) {
        val intentRegister: Intent = Intent(this,RegisterActivity::class.java)
        startActivity(intentRegister)
    }
}