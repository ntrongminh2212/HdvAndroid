package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.DatePicker
import android.widget.Toast
import com.example.myapplication.entities.User
import com.example.myapplication.entities.UserContainer
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.act_change_personal_data.*
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaType
import org.json.JSONObject
import java.io.IOException

class ChangePersonalDataAct : AppCompatActivity() {
    lateinit var user:User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_change_personal_data)
        user = intent.getSerializableExtra("user") as User
        setControl()
        setEvent()
    }

    fun setControl(){
        editFullName.setText(user.name)
        editPhoneNumber.setText(user.phoneNumber)
        txtDateOfBirth.setText(user.dateOfBirth)
    }

    fun setEvent(){
        bttPickDate.setOnClickListener {
            showDialogDatePicker()
        }

        bttConfirmChange.setOnClickListener {
            putUpdateProfile()
        }
    }

    fun showDialogDatePicker(){
        val dialog: BottomSheetDialog = BottomSheetDialog(this,R.style.Theme_Design_BottomSheetDialog)
        val viewDatePicker: View = LayoutInflater.from(this).inflate(R.layout.dialog_date_picker,null)

        val dpDateOfBirth = viewDatePicker.findViewById<DatePicker>(R.id.dpDateOfBirth)
        val bttConfirmDate = viewDatePicker.findViewById<Button>(R.id.bttConfirmDate)

        bttConfirmDate.setOnClickListener {
            val day = dpDateOfBirth.dayOfMonth.toString()
            val month = (dpDateOfBirth.month+1).toString()
            val year = dpDateOfBirth.year.toString()
            val birthDay:String = day +"/"+ month+"/"+year

            txtDateOfBirth.setText(birthDay)
            dialog.dismiss()
        }
        dialog.setContentView(viewDatePicker)
        dialog.show()
    }

    fun putUpdateProfile() {
        var sharePref = getSharedPreferences("userData", MODE_PRIVATE)
        val token = sharePref.getString(getString(R.string.token), null)
        val avatarPr = sharePref.getString(getString(R.string.avatarPr),null)
        var params = JSONObject().put("userToken", token)
        var data = JSONObject()
        data.put("name", editFullName.text.toString())
        data.put("placeOfBirth", user.placeOfBirth)
        data.put("dateOfBirth", txtDateOfBirth.text.toString())
        data.put("phoneNumber", editPhoneNumber.text.toString())
        data.put("emailUser", user.emailUser)
        data.put("role", user.role)
        var json = JSONObject()
        json.put("params", params)
        json.put("data", data)
        json.put("avatarPr",avatarPr)

        val url = getString(R.string.putUpdateProfile)
        val jsonType = getString(R.string.mediaTypeJSON).toMediaType()
        val reqBody = RequestBody.create(jsonType, json.toString())
        val request = Request.Builder().put(reqBody).url(url).build()
        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                this@ChangePersonalDataAct.runOnUiThread {
                    Toast.makeText(
                        this@ChangePersonalDataAct,
                        "Lỗi kết nối internet",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                val resJson = JSONObject(body)
                if (resJson.has("success")) {
                    this@ChangePersonalDataAct.runOnUiThread {
                        setResult(RESULT_OK)
                        finish()
                    }
                }
            }
        })
    }

    fun goBack(view: android.view.View) {
        onBackPressed()
    }
}