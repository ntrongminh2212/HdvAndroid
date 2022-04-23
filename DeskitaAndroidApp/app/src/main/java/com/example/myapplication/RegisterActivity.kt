package com.example.myapplication

import android.os.Bundle
import android.view.Gravity
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.act_register.*
import kotlinx.android.synthetic.main.act_login.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.IOException

class RegisterActivity() : AppCompatActivity(){

    var username: String = ""
    var lastName: String = ""
    var firstname: String = ""
    var password: String = ""
    var confPassword: String = ""
    val emailRegex:Regex = Regex("\\w+@gmail\\.com")
    var registryUrl: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_register)
        registryUrl = getString(R.string.serverUrl)+"/user"
        setSupportActionBar(tbRegister)
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
        bttDangKy.setOnClickListener{
            if (checkLoginEx()) registry()
        }
    }

    private fun checkLoginEx(): Boolean{
        username = txtEmailReg.text.toString()
        lastName = txtLastnameReg.text.toString()
        firstname = txtFirstnameReg.text.toString()
        password = txtPasswordReg.text.toString()
        confPassword = txtPasswordConf.text.toString()

        var check =true
        if (lastName.isBlank()){
            txtLastnameReg.setError("Họ không được để trống")
            check = false
        }
        if (firstname.isBlank()){
            txtFirstnameReg.setError("Tên không được để trống")
            check = false
        }
        if (username.matches(emailRegex)==false){
            txtEmailReg.setError("Địa chỉ email không đúng định dạng. VD: abc@gmail.com")
            check = false
        }
        if (password.isBlank()){
            txtPasswordReg.setError("Mật khẩu không được để trống")
            check = false
        }
        if (confPassword.isBlank()){
            txtPasswordConf.setError("Mật khẩu xác nhận không được để trống")
            check = false
        }
        if (password.compareTo(confPassword)!=0){
            txtPasswordConf.setError("Mật khẩu không khớp")
            check = false
        }
        return check
    }

    private fun registry() {
        val newAccountJson = JSONObject()
        newAccountJson.put("userName", username)
        newAccountJson.put("password", password)
        newAccountJson.put("firstName", firstname)
        newAccountJson.put("lastname", lastName)

        val client = OkHttpClient()
        val JSONType = "application/json; charset=utf-8".toMediaTypeOrNull()
        val reqBody = RequestBody.create(JSONType, newAccountJson.toString())
        val req: Request = Request.Builder().post(reqBody).url(registryUrl).build()

        client.newCall(req).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Toast.makeText(this@RegisterActivity, "Lỗi kết nối!", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call, response: Response) {
                val toast = Toast.makeText(
                    this@RegisterActivity,
                    "Đăng ký tài khoản mới thành công",
                    Toast.LENGTH_SHORT
                )
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()
            }
        })
    }
}