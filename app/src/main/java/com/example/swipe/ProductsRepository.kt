import com.example.swipe.AddProductResponse
import com.example.swipe.ProductClassItem
import com.example.swipe.RetrofitInstance

class ProductRepository {
    private val productService = RetrofitInstance.productService

    suspend fun getProductsList(): List<ProductClassItem> {
        return productService.getProducts()
    }

    suspend fun addProductToList(product:ProductClassItem): AddProductResponse {
        return productService.addProduct(product = product)
    }
}

