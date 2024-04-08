package com.example.swipe

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

object RetrofitInstance {
    private const val BASE_URL = "https://app.getswipe.in/api/public/"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val productService: ProductService by lazy {
        retrofit.create(ProductService::class.java)
    }
}


interface ProductService {
    @GET("get")
    suspend fun getProducts(): List<ProductClassItem>

    @POST("api/public/add")
    fun addProduct(@Body product: ProductClassItem): AddProductResponse
}
