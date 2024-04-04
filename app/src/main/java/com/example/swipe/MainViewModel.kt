import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.collection.mutableFloatListOf
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.swipe.AddProductResponse
import com.example.swipe.ProductClassItem
import com.example.swipe.RetrofitInstance
import kotlinx.coroutines.launch
import retrofit2.Callback
import retrofit2.Call
import retrofit2.Response

class MainViewModel : ViewModel() {
    private val repository = ProductRepository()

    private val _productsList = MutableLiveData<List<ProductClassItem>>()
    val productsList: LiveData<List<ProductClassItem>> = _productsList

    private val _addResponse = MutableLiveData<AddProductResponse>()
    val addResponse: LiveData<AddProductResponse> = _addResponse

    var ds: Boolean = false


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

    fun addProducts(context:Context,productClassItem: ProductClassItem) {
        viewModelScope.launch {
            try {
                val cards = repository.addProductToList(productClassItem)
                _addResponse.value = cards
            } catch (e: Exception) {
                Toast.makeText(context,"INTERNET NOT AVAILABLE",Toast.LENGTH_LONG).show()
            }
        }
    }
}