package com.example.giftcardsite

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.Button
import com.google.android.material.appbar.CollapsingToolbarLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.giftcardsite.api.model.*
import com.example.giftcardsite.api.service.ProductInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ProductScrollingActivity : AppCompatActivity() {
    var loggedInUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scrolling)
        setSupportActionBar(findViewById(R.id.toolbar))
        findViewById<CollapsingToolbarLayout>(R.id.toolbar_layout).title = title
        loggedInUser = intent.getParcelableExtra<User>("User")
        findViewById<Button>(R.id.view_cards_button).setOnClickListener{
            val intent = Intent(this, CardScrollingActivity::class.java).apply {
                putExtra("User", loggedInUser)
            }
            startActivity(intent)
        }
        //var productList: List<Product?>? = null
        var builder: Retrofit.Builder = Retrofit.Builder().baseUrl("https://appsecclass.report").addConverterFactory(
                GsonConverterFactory.create())
        var retrofit: Retrofit = builder.build()
        var client: ProductInterface = retrofit.create(ProductInterface::class.java)
        val outerContext = this
        var manager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        var recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = manager
        client.getAllProducts()?.enqueue(object :
                Callback<List<Product?>?> {
            override fun onFailure(call: Call<List<Product?>?>, t: Throwable) {
                Log.d("Product Failure", "Product Failure in onFailure")
                Log.d("Product Failure", t.message.toString())

            }

            override fun onResponse(call: Call<List<Product?>?>, response: Response<List<Product?>?>) {
                if (!response.isSuccessful) {
                    Log.d("Product Failure", "Product failure. Yay.")
                }
                var productListInternal = response.body()
                Log.d("Product Success", "Product Success. Boo.")
                if (productListInternal == null) {
                    Log.d("Product Failure", "Parsed null product list")
                    Log.d("Product Failure", response.toString())
                } else {
                    recyclerView.adapter = RecyclerViewAdapter(outerContext, productListInternal, loggedInUser)
                }
            }
        })

    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }
}
