package com.example.swipe

import ProductRepository
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val repository = ProductRepository()

    private val _productsList = MutableLiveData<List<ProductClassItem>>()
    val productsList: LiveData<List<ProductClassItem>> = _productsList
    fun fetchProducts(context:Context) {
        viewModelScope.launch {
            try {
                val cards = repository.getProductsList()
                _productsList.value = cards
            } catch (e: Exception) {
               Toast.makeText(context,"INTERNET NOT AVAILABLE",Toast.LENGTH_LONG).show()
            }
        }
    }
}