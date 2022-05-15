package com.example.myapplication

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.AbsListView
import android.widget.Toast
import com.example.myapplication.adapter.ProductAdapter
import com.example.myapplication.entities.Product
import com.example.myapplication.entities.ProductContainer
import com.example.myapplication.entities.ProductsFeed
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.act_register.*
import kotlinx.android.synthetic.main.act_result_search.*
import kotlinx.android.synthetic.main.act_result_search.loadingBar
import kotlinx.android.synthetic.main.act_search_products.*
import kotlinx.android.synthetic.main.frag_home.*
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import java.io.IOException

class SearchProductsActivity : AppCompatActivity() {
    var strSearch: String = ""
    var lstProductResult: ArrayList<Product> = ArrayList()
    lateinit var userToken:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_search_products)
        val sharedPref = this.getPreferences(Context.MODE_PRIVATE) ?: return
        userToken = sharedPref.getString(getString(R.string.token), "")!!
        strSearch = sharedPref.getString(getString(R.string.save_search_str),"").toString()
        txtSearcher.setText(strSearch)
        txtSearcher.requestFocus()

        txtSearcher.setOnEditorActionListener({ v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                (event.keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)
            ) {
                callSearchProducts(txtSearcher.text.toString())
                setContentView(R.layout.act_result_search)
                setResultViewEventListener()
                txtSearcherResult.text = txtSearcher.text
                true
            } else {
                false
            }
        })
    }

    fun goBack(view: android.view.View) {
        val sharedPref = this.getPreferences(Context.MODE_PRIVATE) ?: return
        with (sharedPref.edit()) {
            putString(getString(R.string.save_search_str),"")
            apply()
        }
        onBackPressed()
    }

    fun Restart(view: android.view.View) {
        val sharedPref = this.getPreferences(Context.MODE_PRIVATE) ?: return
        with (sharedPref.edit()) {
            putString(getString(R.string.save_search_str), txtSearcherResult.text.toString())
            apply()
        }
        this.recreate()
    }

    fun callSearchProducts(str: String){
        var url: HttpUrl =
            getString(R.string.getSearchProducts).toHttpUrlOrNull()!!.newBuilder()
                .addQueryParameter("keyword" ,str).build()

        val request = Request.Builder().url(url).build()
        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                TODO("Not yet implemented")
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                val gson = GsonBuilder().create()
                val productsFeed = gson.fromJson(body, ProductsFeed::class.java)
                lstProductResult.addAll(productsFeed.products)
                this@SearchProductsActivity.runOnUiThread {
                    Toast.makeText(this@SearchProductsActivity,lstProductResult.size.toString(),Toast.LENGTH_SHORT).show()
                    lstSearchResult.adapter = ProductAdapter(
                        this@SearchProductsActivity,
                        ArrayList()
                    )
                    loadMoreResult()
                }
                Thread.sleep(3000)
            }
        })
    }

    fun setResultViewEventListener(){
        lstSearchResult.setOnScrollListener(object :AbsListView.OnScrollListener{
            override fun onScrollStateChanged(view: AbsListView?, scrollState: Int) {
                return
            }

            override fun onScroll(
                view: AbsListView?,
                firstVisibleItem: Int,
                visibleItemCount: Int,
                totalItemCount: Int
            ) {
                if (lstSearchResult.lastVisiblePosition == totalItemCount - 1 && lstSearchResult.adapter!=null) {
                    var adapter = lstSearchResult.adapter as ProductAdapter
                    if (!adapter.isLoading) {
                        loadMoreResult()
                    }
                }
            }
        })

        lstSearchResult.setOnItemClickListener { parent, view, position, id ->
            val clickProduct: Product = parent.getItemAtPosition(position) as Product
            actProductDetail(clickProduct._id)
        }

    }

    fun loadMoreResult(){
        if (lstProductResult.isEmpty()) return

        var adapter = lstSearchResult.adapter as ProductAdapter
        if (!adapter.isLoading) {
            adapter.isLoading = true
            loadingBar.visibility = View.VISIBLE

            if (lstProductResult.size > 10) {
                adapter.addMoreProducts(lstProductResult.subList(0, 10).toList())
                lstProductResult.removeAll(lstProductResult.subList(0, 10))
            }else{
                adapter.addMoreProducts(lstProductResult)
                lstProductResult.clear()
            }

            adapter.isLoading = false
            loadingBar.visibility = View.GONE
        }
    }

    fun actProductDetail(_id:String) {
        val url: String = getString(R.string.getProductDetail)+_id
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
                val intent: Intent = Intent(this@SearchProductsActivity, ProductDetailActivity::class.java)
                intent.putExtra("product",productContainer.product)
                startActivity(intent)
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

    fun actLogin() {
        val intent = Intent(this,LoginActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_up,R.anim.slide_out_down)
    }
}
