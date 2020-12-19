package com.example.giftcardsite

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.giftcardsite.api.model.*
import com.example.giftcardsite.api.service.CardInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CardScrollingActivity : AppCompatActivity() {
    private var loggedInUser : User? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scrolling)
        setSupportActionBar(findViewById(R.id.toolbar))
        loggedInUser = intent.getParcelableExtra<User>("User")
        var button : Button = findViewById<Button>(R.id.view_cards_button)
        button.text = "View Products"
        button.setOnClickListener {
            val intent = Intent(this, ProductScrollingActivity::class.java).apply{
                putExtra("User", loggedInUser)
            }
            startActivity(intent)
        }
        var builder: Retrofit.Builder = Retrofit.Builder().baseUrl("https://appsecclass.report").addConverterFactory(
            GsonConverterFactory.create())
        var retrofit: Retrofit = builder.build()
        var client: CardInterface = retrofit.create(CardInterface::class.java)
        val outerContext = this
        var manager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        var recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val token = "Token ${loggedInUser?.token}"
        recyclerView.layoutManager = manager
        client.getCards(token)?.enqueue(object :
            Callback<List<Card?>?> {
            override fun onFailure(call: Call<List<Card?>?>, t: Throwable) {
                Log.d("Product Failure", "Product Failure in onFailure")
                Log.d("Product Failure", t.message.toString())

            }

            override fun onResponse(call: Call<List<Card?>?>, response: Response<List<Card?>?>) {
                if (!response.isSuccessful) {
                    Log.d("Product Failure", "Product failure. Yay.")
                }
                var cardListInternal = response.body()
                Log.d("Product Success", "Product Success. Boo.")
                if (cardListInternal == null) {
                    Log.d("Product Failure", "Parsed null product list")
                    Log.d("Product Failure", response.toString())
                } else {
                    recyclerView.adapter = CardRecyclerViewAdapter(outerContext, cardListInternal, loggedInUser)
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
