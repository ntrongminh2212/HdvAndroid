package com.example.myapplication

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.RatingBar
import com.example.myapplication.adapter.ReviewAdapter
import com.example.myapplication.entities.Product
import com.example.myapplication.entities.ProductContainer
import com.google.gson.GsonBuilder
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.act_product_detail.*
import kotlinx.android.synthetic.main.act_product_detail.lstReviews
import kotlinx.android.synthetic.main.act_product_detail.ratingProduct
import kotlinx.android.synthetic.main.act_product_detail.txtPrice
import kotlinx.android.synthetic.main.act_product_detail.txtProductName
import kotlinx.android.synthetic.main.act_product_detail.txtRatingPoint
import kotlinx.android.synthetic.main.act_review_product.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.IOException

class ReviewProductActivity : AppCompatActivity() {

    lateinit var product: Product

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_review_product)
        val productId = intent.getStringExtra("productId")
        getProduct(productId)
    }

    private fun getProduct(productId:String?) {
        val url: String = getString(R.string.getProductDetail)+ productId
        val request = Request.Builder().url(url).build()
        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                TODO("Not yet implemented")
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                val gson = GsonBuilder().create()
                val productContainer = gson.fromJson(body, ProductContainer::class.java)
                this@ReviewProductActivity.runOnUiThread{
                    product = productContainer.product
                    setControl()
                    setEvent()
                }
            }
        })
    }

    private fun setControl() {
        Picasso.Builder(this).build().load(product.images[0].url).into(imgReviewProduct)
        txtProductName.text = product.name
        txtPrice.text = product.price.toString() + "$"
        txtMyReview.setHorizontallyScrolling(false)
        txtMyReview.maxLines = 20

        ratingMyReview.rating = 5.toFloat()
        txtMyRatingPoint.text = "5.0"

        var overallRate: Double = 0.0;
        for (review in product.reviews) {
            overallRate = overallRate + review.rating
        }
        overallRate = overallRate / product.reviews.size
        ratingProduct.rating = overallRate.toFloat()
        txtRatingPoint.text = overallRate.toString()

        if (product.reviews.size > 0) {
            lstReviews.adapter = ReviewAdapter(this, product.reviews)
            justifyListViewHeightBasedOnChildren(lstReviews)
        } else {
            val noReview: List<String> = listOf(
                "Sản phẩm hiện chưa có đánh giá. Hãy mua và trở thành người đầu" +
                        " tiên đánh giá sản phẩm này!"
            )
            lstReviews.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, noReview)
        }
    }


    private fun setEvent() {
        ratingMyReview.setOnRatingBarChangeListener { ratingBar: RatingBar, fl: Float, b: Boolean ->
            txtMyRatingPoint.setText(fl.toString())
        }

        bttSendReview.setOnClickListener {
            val comment = txtMyReview.text
            val rating = ratingMyReview.rating.toDouble()

            if (!comment.isBlank()) {
                val sharedPref = getSharedPreferences("userData", Context.MODE_PRIVATE)
                val userToken = sharedPref.getString(getString(R.string.token), null)
                var data = JSONObject().put(
                    "params",
                    JSONObject().put("userToken", userToken)
                )
                data.put("rating", rating)
                data.put("comment", comment)
                data.put("productId", product._id)

                val url: String = getString(R.string.putReview)
                val JSON = getString(R.string.mediaTypeJSON).toMediaTypeOrNull()
                val reqBody = RequestBody.create(JSON, data.toString())
                val request = Request.Builder().url(url).put(reqBody).build()
                val client = OkHttpClient()
                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        TODO("Not yet implemented")
                    }

                    override fun onResponse(call: Call, response: Response) {
                        val body = response.body?.string()
                        val resJson = JSONObject(body)
                        this@ReviewProductActivity.runOnUiThread({
                            if (resJson.has("success")) {
                                finish();
                                startActivity(getIntent().putExtra("productId",product._id));
                            }
                        })
                    }
                })
            }
        }
    }

    fun goBack(view: android.view.View) {
        onBackPressed()
    }

    fun justifyListViewHeightBasedOnChildren(listView: ListView) {
        val adapter: Adapter = listView.getAdapter() ?: return
        val vg: ViewGroup = listView
        var totalHeight = 0
        for (i in 0 until adapter.getCount()) {
            val listItem: View = adapter.getView(i, null, vg)
            listItem.measure(0, 0)
            totalHeight += listItem.measuredHeight
        }
        val par: ViewGroup.LayoutParams = listView.getLayoutParams()
        par.height = totalHeight + listView.getDividerHeight() * (adapter.getCount() - 1)
        listView.setLayoutParams(par)
        listView.requestLayout()
    }

}