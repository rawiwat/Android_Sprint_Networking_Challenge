package com.example.pokemonchallenge.model


import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import java.net.URL

data class Pokemon (val name:String,
                    val spriteURL: URL,
                    val id:Int,
                    val abilities:List<String>,
                    val type:String)

interface pokemonAPI {
    @GET("pokemon")
    fun getPokemon(@Query("ID") ID:String): Call<List<Pokemon>>
}