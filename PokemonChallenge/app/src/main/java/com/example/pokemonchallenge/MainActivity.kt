package com.example.pokemonchallenge

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.TypedValue
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pokemonchallenge.model.Pokemon
import com.example.pokemonchallenge.model.SearchHistory
import com.example.pokemonchallenge.model.pokemonAPI
import com.google.android.material.internal.ViewUtils.dpToPx
import com.google.gson.GsonBuilder
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity(), Callback<List<Pokemon>> {

    lateinit var history: RecyclerView
    //lateinit var historyList:MutableList<String>

    //val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    val historyData = mutableListOf<SearchHistory>()

    private fun dpToPx(context: Context, dp: Float): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics).toInt()
    }
    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //val configuration = resources.configuration
        //val keyboardHidden = configuration.keyboardHidden
        history = findViewById<RecyclerView>(R.id.historyMain)

        val pokemonImage = findViewById<ImageView>(R.id.displayImage)

        val root = findViewById<ConstraintLayout>(R.id.root)

            root.viewTreeObserver.addOnGlobalLayoutListener {
                val heightDiff = root.rootView.height - root.height
                if(heightDiff > dpToPx(this,200f)){
                    history.visibility = VISIBLE
                } else {
                    history.visibility = INVISIBLE
                }

            }
        //when (keyboardHidden){
        //    Configuration.KEYBOARDHIDDEN_NO -> history.visibility = VISIBLE
        //    else -> history.visibility = INVISIBLE
        //}

        /*
        tried this,doesn't work
        fun myEnter(){
            enterID.setOnKeyListener(View.OnKeyListener{view, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP){
                    println("enter pressed")
                    val idEntered = enterID.text
                    Toast.makeText(this, "$idEntered was entered", Toast.LENGTH_LONG).show()

                    return@OnKeyListener true
                }
                false
            })
        }
        myEnter()*/
        }
        override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
            return when (keyCode) {
                KeyEvent.KEYCODE_ENTER -> {
                    val enterID = findViewById<EditText>(R.id.enterID)
                    val idEntered:String = enterID.text.toString()
                    Toast.makeText(this, "$idEntered was entered", Toast.LENGTH_LONG).show()

                    val view: View? = this.currentFocus
                    val inputMethodManager =
                        getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(view?.getWindowToken(),0)
                    //apiRetriever().getPokemon().enqueue(this)
                    //ApiReceiver().getPokemon().enqueue(this)

                    historyData.add(SearchHistory(idEntered))

                    //val history = inflater.inflate(R.layout.history,null)
                    //val historyListText = history.findViewById<TextView>(R.id.historyText)
                    history = findViewById(R.id.historyMain)
                    history.layoutManager = LinearLayoutManager(this)
                    val historyAdapter = HistoryAdapter(historyData)
                    history.adapter = historyAdapter

                    ApiReceiver().getPokemon().enqueue(this)

                    val imageURL = ApiReceiver().getPokemon().toString()
                    println(imageURL)
                    Picasso.get().load(imageURL).into(object : Target {
                        override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                            // set the bitmap to the ImageView
                            imageView.setImageBitmap(bitmap)
                        }

                        override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                            // handle error
                        }

                        override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                            // set a placeholder drawable if needed
                            imageView.setImageDrawable(placeHolderDrawable)
                        }
                    })
                    true
                }
                else -> super.onKeyUp(keyCode, event)
            }
        }

    override fun onResponse(call: Call<List<Pokemon>>, response: Response<List<Pokemon>>) {

    }

    override fun onFailure(call: Call<List<Pokemon>>, t: Throwable) {
        Toast.makeText(this,"ID Invalid",Toast.LENGTH_SHORT).show()
    }

    class ApiReceiver : AppCompatActivity() {

        companion object {
            const val BASE_URL = "https://pokeapi.co/api/v2/"
        }

        fun getPokemon(): Call<List<Pokemon>> {
            val gson = GsonBuilder()
                .setLenient()
                .create()

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()

            val pokemonAPI = retrofit.create(pokemonAPI::class.java)
            val enterID = findViewById<EditText>(R.id.enterID)
            return pokemonAPI.getPokemon(ID = enterID.toString())
        }
    }
}