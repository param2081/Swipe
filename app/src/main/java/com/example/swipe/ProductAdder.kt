package com.example.swipe

import android.os.Looper
import com.google.gson.Gson
import okhttp3.*
import android.os.Handler
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.IOException

class ProductAdder {

    fun addProduct(
        productName: String,
        productType: String,
        price: String,
        tax: String,
        imgFile: File,
        callback: (Boolean, AddProductResponse?) -> Unit
    ) {
        val client = OkHttpClient()
        val mediaType = "image/png".toMediaTypeOrNull()

        val imageRequestBody = imgFile.asRequestBody(mediaType)

        val requestBodyBuilder = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("product_name", productName)
            .addFormDataPart("product_type", productType)
            .addFormDataPart("price", price)
            .addFormDataPart("tax", tax)
            .addFormDataPart("image", imgFile.name, imageRequestBody)

        val requestBody = requestBodyBuilder.build()

        val request = Request.Builder()
            .url("https://app.getswipe.in/api/public/add")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Handler(Looper.getMainLooper()).post {
                    callback(false, null)
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                if (response.isSuccessful && responseBody != null) {
                    val gson = Gson()
                    val addProductResponse = gson.fromJson(responseBody, AddProductResponse::class.java)
                    Handler(Looper.getMainLooper()).post {
                        callback(true, addProductResponse)
                    }
                } else {
                    Handler(Looper.getMainLooper()).post {
                        callback(false, null)
                    }
                }
            }
        })
    }
}