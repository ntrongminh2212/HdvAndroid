package com.example.myapplication

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.myapplication.entities.User
import com.example.myapplication.entities.UserContainer
import com.google.gson.GsonBuilder
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.act_personal_info.*
import kotlinx.android.synthetic.main.frag_personal.*
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrl
import java.io.IOException
import android.os.Build
import androidx.annotation.RequiresApi
import java.util.*
import okhttp3.MediaType.Companion.toMediaType
import org.json.JSONObject

import android.graphics.BitmapFactory
import android.util.Log
import java.io.ByteArrayOutputStream

import java.io.InputStream
import com.example.myapplication.R
import com.example.myapplication.entities.UserAddress
import com.google.gson.Gson


class PersonalInfoActivity : AppCompatActivity() {
    private val REQUEST_EXTERNAL_STORAGE = 1
    private val PERMISSIONS_STORAGE = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    private val CHANGE_PERSONAL_CODE = 2
    private val CHANGE_DEFAULT_LOCATION_CODE = 3

    lateinit var user: User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_personal_info)
        val sharePref = getSharedPreferences("userData", MODE_PRIVATE)
        val userMeJson = sharePref.getString(getString(R.string.userMeJson), "")
        user = Gson().fromJson(userMeJson, User::class.java)
        setContent()
    }

    private fun setContent() {
        Picasso.get().load(user.avatar.url).placeholder(R.drawable.user_avatar).into(imgMyAvatar)
        txtFullName.text = user.name
        txtDateOfBirth.text = user.dateOfBirth
        txtPhoneNumber.text = user.phoneNumber
        txtEmail.text = user.emailUser
        this.user = user
        var sharePref = getSharedPreferences("userData", MODE_PRIVATE)
        sharePref.edit()
            .putString(getString(R.string.avatar), user.avatar.url)

        val defaultAddressJson = sharePref.getString(getString(R.string.defaultAddress), " ")
        if (!defaultAddressJson!!.isBlank()) {
            var defaultAdress = Gson().fromJson(defaultAddressJson, UserAddress::class.java)
            txtHouseAdrress.setText(defaultAdress.address)
            txtTown.setText(defaultAdress.city)
        }
    }

    fun goBack(view: android.view.View) {
        onBackPressed()
    }

    fun changeAvatar(view: android.view.View) {
        verifyStoragePermissions(this)
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 100)
    }

    fun verifyStoragePermissions(activity: Activity?) {
        // Check if we have write permission
        val permission = ActivityCompat.checkSelfPermission(
            activity!!,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                activity,
                PERMISSIONS_STORAGE,
                REQUEST_EXTERNAL_STORAGE
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == 100) {
            val fileUri = data!!.data
            val imageStream: InputStream? = contentResolver.openInputStream(fileUri!!)
            val selectedImage = BitmapFactory.decodeStream(imageStream)

            val encodedImage: String = "data:image/jpeg;base64," + encodeImage(selectedImage)
            putUpdateProfile(encodedImage)
        }
        if (resultCode == RESULT_OK && requestCode == CHANGE_PERSONAL_CODE) {
            var sharePref = getSharedPreferences("userData", MODE_PRIVATE)
            val token = sharePref.getString(getString(R.string.token), null)
            getPersonalInfo(token!!)
        }
        if (resultCode == RESULT_OK && requestCode == CHANGE_DEFAULT_LOCATION_CODE) {
            val sharePref = getSharedPreferences("userData", MODE_PRIVATE)
            val defaultAddressJson = sharePref.getString(getString(R.string.defaultAddress), null)
            var defaultAddress: UserAddress
            if (defaultAddressJson != null && !defaultAddressJson.isBlank()) {
                defaultAddress = Gson().fromJson(defaultAddressJson, UserAddress::class.java)
            } else {
                defaultAddress = UserAddress("","","VietNam",user.phoneNumber,"12345")
            }
            txtTown.text = defaultAddress.city
            txtHouseAdrress.text = defaultAddress.address
        }
    }

    fun putUpdateProfile(avatarPr: String) {
        var sharePref = getSharedPreferences("userData", MODE_PRIVATE)
        val token = sharePref.getString(getString(R.string.token), null)
        var params = JSONObject().put("userToken", token)
        var data = JSONObject()
        data.put("name", user.name)
        data.put("placeOfBirth", user.placeOfBirth)
        data.put("dateOfBirth", user.dateOfBirth)
        data.put("phoneNumber", user.phoneNumber)
        data.put("emailUser", user.emailUser)
        data.put("role", user.role)
        var json = JSONObject()
        json.put("params", params)
        json.put("data", data)
        json.put("avatarPr", avatarPr)

        val url = getString(R.string.putUpdateProfile)
        val jsonType = getString(R.string.mediaTypeJSON).toMediaType()
        val reqBody = RequestBody.create(jsonType, json.toString())
        val request = Request.Builder().put(reqBody).url(url).build()
        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                this@PersonalInfoActivity.runOnUiThread {
                    Toast.makeText(
                        this@PersonalInfoActivity,
                        "Lỗi kết nối internet",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                val resJson = JSONObject(body)
                if (resJson.has("success")) {
                    this@PersonalInfoActivity.runOnUiThread {
                        sharePref.edit()
                            .putString(getString(R.string.avatarPr), avatarPr)
                            .commit()
                        getPersonalInfo(token!!)
                    }
                }
            }
        })
    }

    fun getPersonalInfo(userToken: String) {
        val url: HttpUrl = getString(R.string.getPersonalInfo).toHttpUrl()
            .newBuilder().addQueryParameter("userToken", userToken).build()

        val request = Request.Builder().get().url(url).build()
        val client: OkHttpClient = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                this@PersonalInfoActivity.runOnUiThread {
                    Toast.makeText(
                        this@PersonalInfoActivity,
                        "Lỗi kết nối mạng", Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val resBody = response.body?.string()
                val gson = GsonBuilder().create()
                val userContainer = gson.fromJson(resBody, UserContainer::class.java)
                this@PersonalInfoActivity.runOnUiThread {
                    val userMeJson = gson.toJson(userContainer.user)
                    val sharedPref = getSharedPreferences("userData", MODE_PRIVATE)

                    sharedPref.edit()
                        .putString(getString(R.string.userMeJson), userMeJson)
                        .commit()
                    finish()
                    startActivity(getIntent())
                }
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun encodeImage(bm: Bitmap): String {
        val baos = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val b = baos.toByteArray()
        return Base64.getEncoder().encodeToString(b)
    }

    fun changePersonalData(view: android.view.View) {
        val intent = Intent(this, ChangePersonalDataAct::class.java)
        intent.putExtra("user", this.user)
        startActivityForResult(intent, CHANGE_PERSONAL_CODE)
    }

    fun changeDefaultLocation(view: android.view.View) {
        val intent = Intent(this, ManageLocationActivity::class.java)
        startActivityForResult(intent, CHANGE_DEFAULT_LOCATION_CODE)
    }
}