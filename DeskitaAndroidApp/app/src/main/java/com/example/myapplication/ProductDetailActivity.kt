package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.*
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.example.myapplication.adapter.ReviewAdapter
import com.example.myapplication.entities.Product
import com.example.myapplication.entities.ProductImage
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.act_product_detail.*
import kotlinx.android.synthetic.main.act_result_search.*
import kotlinx.android.synthetic.main.review_row.view.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.IOException

class ProductDetailActivity : AppCompatActivity() {
    lateinit var product: Product

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_product_detail)
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
        product = intent.getSerializableExtra("product") as Product
        setSliderProductImgs(product.images)
        ratingProduct.rating = product.rating.toFloat()
        txtRatingPoint.text = product.rating.toString()
        txtProductName.text = product.name
        txtPrice.text = product.price.toString()+"$"

        if (product.reviews.size>0) {
            lstReviews.adapter = ReviewAdapter(this, product.reviews)
        }else{
            val noReview:List<String> = listOf("Sản phẩm hiện chưa có đánh giá. Hãy mua và trở thành người đầu" +
                    " tiên đánh giá sản phẩm này!")
            lstReviews.adapter = ArrayAdapter(this,android.R.layout.simple_list_item_1,noReview)
        }
    }

    fun setSliderProductImgs(imgsList: List<ProductImage>){
        val lstSlideModel: ArrayList<SlideModel> = ArrayList()
        var i = 1
        for (img in imgsList){
            lstSlideModel.add(SlideModel(img.url,i.toString()))
            i++
        }
        lstProductImgs.setImageList(lstSlideModel,ScaleTypes.CENTER_INSIDE)
    }

    fun goBack(view: android.view.View) {
        onBackPressed()
    }

    fun dialogAddToCart(view: android.view.View) {
        val dialogAddToCart = BottomSheetDialog(this,R.style.Theme_Design_BottomSheetDialog)
        val viewAddToCart = LayoutInflater.from(this).inflate(R.layout.dialog_add_to_cart,null)

        Picasso.get().load(product.images[0].url).into(viewAddToCart.findViewById<ImageView>(R.id.imgAddProduct))
        viewAddToCart.findViewById<TextView>(R.id.txtAddProductName).setText(product.name)
        viewAddToCart.findViewById<TextView>(R.id.txtAddProductPrice).setText(product.price.toString()+"$")

        val txtQuan = viewAddToCart.findViewById<EditText>(R.id.txtQuantities)

        txtQuan.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                return
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                 return
            }

            override fun afterTextChanged(s: Editable?) {
                if (Integer.parseInt(txtQuan.text.toString())>30){
                    txtQuan.setText("0")
                }
            }
        })

        viewAddToCart.findViewById<ImageButton>(R.id.bttDecreaseQuan).setOnClickListener({
            var quan:Int = Integer.parseInt(txtQuan.text.toString())
            if (quan>0) quan--;
            txtQuan.setText(quan.toString())
        })

        viewAddToCart.findViewById<ImageButton>(R.id.bttIncreaseQuan).setOnClickListener({
            var quan:Int = Integer.parseInt(txtQuan.text.toString())
            if (quan<30) quan++;
            txtQuan.setText(quan.toString())
        })

        viewAddToCart.findViewById<Button>(R.id.bttConfirmCart).setOnClickListener({
            val quan: Int = Integer.parseInt(txtQuan.text.toString())
            val sharePref = getSharedPreferences("userData", MODE_PRIVATE)
            val userToken = sharePref.getString(getString(R.string.token),null)
            callAddToCart(quan,userToken,dialogAddToCart)
        })
        dialogAddToCart.setContentView(viewAddToCart)
        dialogAddToCart.show()
    }

    private fun callAddToCart(quan: Int, userToken: String?, dialogAddToCart: BottomSheetDialog) {
        var url= getString(R.string.putAddToCart)

        var data = JSONObject()
        data.put("data",JSONObject().put("product",JSONObject().put("_id",product._id)))
        data.getJSONObject("data").put("quantity",quan)
        data.put("params",
            JSONObject().put("userToken",userToken))

        Log.d("json123",data.toString())

        val JSON = getString(R.string.mediaTypeJSON).toMediaTypeOrNull()
        val rqBody = RequestBody.create(JSON,data.toString())
        val request = Request.Builder().url(url).put(rqBody).build()
        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                dialogAddToCart.dismiss()
                return
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                val resJson = JSONObject(body)
                this@ProductDetailActivity.runOnUiThread {
                    if (resJson.has("success")) {
                        val toast = Toast.makeText(
                            this@ProductDetailActivity,
                            "Đã thêm " + quan + " sản phẩm vào giỏ hàng",
                            Toast.LENGTH_SHORT
                        )
                        toast.setGravity(Gravity.CENTER, 0, 0)
                        toast.show()
                        dialogAddToCart.dismiss()
                    }
                }
            }
        })
    }

    fun startActMyCart(item: android.view.MenuItem) {
        var intent: Intent = Intent(this,MyCartActivity::class.java)
        startActivity(intent)
    }
}