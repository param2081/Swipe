package com.example.swipe

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition

val viewModelFactory = ViewModelHelper.viewModelFactory {
    MainViewModel()
}

@Composable
fun ProductsMainScreen(
    viewModel: MainViewModel = viewModel(factory = viewModelFactory),
    context: Context = LocalContext.current,
    navController: NavHostController
) {

    val productsList by viewModel.productsList.observeAsState(emptyList())

    LaunchedEffect(Unit) {
        viewModel.fetchProducts(context)
    }

    Column {
        if (productsList.isEmpty()) {
            Column (modifier=Modifier.align(CenterHorizontally)){
                LoadingView()
            }
        } else {
            ProductFullScreen(productsList,navController)
        }
    }
    val activity = LocalContext.current as? Activity
    BackHandler {
        activity?.finish()
    }
}

@Composable
private fun LoadingView() {
    val lottieComposition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(R.raw.loading)
    )
    LottieAnimation(
        composition = lottieComposition,
        modifier = Modifier.size(600.dp),
        iterations = LottieConstants.IterateForever
    )
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ProductFullScreen(products: List<ProductClassItem>,   navController: NavHostController) {
    var searchText by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(top = 10.dp, start = 5.dp, end = 5.dp)) {
        Button(
            onClick = {
                searchText = ""
            }, modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
        ) {
            IconButton(onClick = {
                searchText = ""
            }) {
                Icon(Icons.Filled.Refresh, contentDescription = "Refresh")
                Spacer(modifier = Modifier.height(30.dp))
            }
        }


        Button(
            onClick = {
                navController.navigate("add_product_screen")
            }, modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
                .height(60.dp)
        ) {
            Text(text = "Add product", modifier = Modifier.padding(12.dp))
        }

    }
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        SearchField(searchText = searchText) { newText ->
            searchText = newText
        }

        Spacer(modifier = Modifier.height(16.dp))

        ProductList(products = products.filter {
            it.product_name.contains(searchText, ignoreCase = true)
        })
    }

}


@Composable
fun ProductList(products: List<ProductClassItem>) {
    LazyColumn {
        items(products.size) { product ->
            ProductListItem(product = products[product])
        }
    }
}

@Composable
fun ProductListItem(product: ProductClassItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
    ) {
        Row {
            Image(
                painter = if (!product.image.isNullOrEmpty()) rememberAsyncImagePainter(product.image) else painterResource(
                    id = R.drawable.brand_logo
                ),
                contentDescription = "image",
                modifier = Modifier
                    .padding(16.dp)
                    .width(40.dp)
                    .height(40.dp)
            )
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(text = "NAME: "+ product.product_name)
                Text(text = "TYPE: "+ product.product_type)
                Text(text = "PRICE: "+ product.price)
                Text(text = "TAX: "+ product.tax)

            }

        }
    }
}

@Composable
fun SearchField(
    searchText: String,
    onSearchTextChange: (String) -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    OutlinedTextField(
        value = searchText,
        onValueChange = onSearchTextChange,
        label = { Text("Search") },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = {
            keyboardController?.hide()
        }),
        modifier = Modifier.fillMaxWidth(),
    )
}

object ViewModelHelper {
    inline fun <VM : ViewModel> viewModelFactory(crossinline f: () -> VM) =
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T = f() as T
        }
}
