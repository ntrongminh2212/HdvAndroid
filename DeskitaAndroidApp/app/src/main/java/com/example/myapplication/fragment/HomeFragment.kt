package com.example.myapplication.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.myapplication.R
import com.example.myapplication.adapter.ProductAdapter
import com.example.myapplication.entities.Product
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.frag_home.*
import okhttp3.*
import java.io.IOException
import com.example.myapplication.ProductDetailActivity
import com.example.myapplication.entities.ProductContainer
import com.example.myapplication.entities.ProductsFeed


class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        callGetRandomProducts()
        return inflater.inflate(R.layout.frag_home,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lstHomeProducts.setOnItemClickListener { parent, view, position, id ->
            val clickProduct: Product = parent.getItemAtPosition(position) as Product
            actProductDetail(clickProduct._id)
        }

        lstHomeProducts.setOnScrollListener(object :AbsListView.OnScrollListener{
            override fun onScrollStateChanged(view: AbsListView?, scrollState: Int) {
                return
            }

            override fun onScroll(
                view: AbsListView?,
                firstVisibleItem: Int,
                visibleItemCount: Int,
                totalItemCount: Int
            ) {
                if (lstHomeProducts.lastVisiblePosition == totalItemCount - 1 && lstHomeProducts.adapter!=null) {
                    var adapter = lstHomeProducts.adapter as ProductAdapter
                    if (!adapter.isLoading) {
                        adapter.isLoading = true
                        loadingBar.visibility = View.VISIBLE
                        callGetRandomProducts()
                    }
                }
            }
        })
    }

    fun callGetRandomProducts(){
        val url: String = getString(R.string.serverUrl)+"/random-products"
        val request = Request.Builder().url(url).build()
        val client = OkHttpClient()
        client.newCall(request).enqueue(object: Callback{
            override fun onFailure(call: Call, e: IOException) {
                activity?.runOnUiThread({
                    Toast.makeText(requireContext(), "Lỗi kết nối mạng!", Toast.LENGTH_SHORT).show()
                })
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                val gson = GsonBuilder().create()
                val productsFeed = gson.fromJson(body,ProductsFeed::class.java)

                activity?.runOnUiThread({
                    if (lstHomeProducts.adapter == null) {
                        lstHomeProducts.adapter = ProductAdapter(
                            requireContext(),
                            productsFeed.products as ArrayList<Product>
                        )
                    }else{
                        var adapterHome = lstHomeProducts.adapter as ProductAdapter
                        adapterHome.addMoreProducts(productsFeed.products)
                        adapterHome.isLoading = false
                        loadingBar.visibility = View.GONE
                    }
                })
                Thread.sleep(3000)
            }
        })
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
                val intent: Intent = Intent(context, ProductDetailActivity::class.java)
                intent.putExtra("product",productContainer.product)
                startActivity(intent)
            }
        })
    }
}
