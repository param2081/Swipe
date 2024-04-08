package com.example.swipe

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream


var ptype: String = ""
var pname: String = ""
var sprice: String = ""
var tax: String = ""


@Composable
fun AddProductsScreen(
    navController: NavHostController,
) {
    var imageUri by remember {
        mutableStateOf<Uri?>(null)
    }
    val context = LocalContext.current
    val bitmap = remember {
        mutableStateOf<Bitmap?>(null)
    }

    val launcher = rememberLauncherForActivityResult(
        contract =
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }
    Spacer(modifier = Modifier.height(40.dp))
    Column(modifier = Modifier.padding(start = 20.dp)) {
        ProductTypeTextField("Product Name", keyBoardType = KeyboardType.Text) {
            pname = it
        }
        Spacer(modifier = Modifier.height(10.dp))
        ProductTypeTextField("Selling price", keyBoardType = KeyboardType.Decimal) {
            sprice = it
        }
        Spacer(modifier = Modifier.height(10.dp))
        ProductTypeTextField("Tax", keyBoardType = KeyboardType.Decimal) {
            tax = it
        }

        ProductTypeSelectionScreen()
        Spacer(modifier = Modifier.height(30.dp))
        Column {
            Button(onClick = {
                launcher.launch("image/*")
            }) {
                Text(text = "Pick image")
            }

            Spacer(modifier = Modifier.height(12.dp))

            imageUri?.let {
                if (Build.VERSION.SDK_INT < 28) {
                    bitmap.value = MediaStore.Images
                        .Media.getBitmap(context.contentResolver, it)

                } else {
                    val source = ImageDecoder
                        .createSource(context.contentResolver, it)
                    bitmap.value = ImageDecoder.decodeBitmap(source)
                }

                bitmap.value?.let { btm ->
                    Image(
                        bitmap = btm.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier.size(100.dp)
                    )
                }
            }

        }
        CustomButton("Submit", Color.LightGray, Color.Black) {
            if (pname.isEmpty()) {
                Toast.makeText(context, "Please fill the Product Name", Toast.LENGTH_SHORT).show()
            }
            if (sprice.isEmpty()) {
                Toast.makeText(context, "Please fill the Selling price", Toast.LENGTH_SHORT).show()
            }
            if (tax.isEmpty()) {
                Toast.makeText(context, "Please fill the Tax", Toast.LENGTH_SHORT).show()
            }
            val file = uriToFile(context, imageUri.toString())
            if (pname.isNotEmpty() && sprice.isNotEmpty() && tax.isNotEmpty()) {
                proceedToAdd(
                    pname,
                    sprice,
                    tax,
                    ptype,
                    file,
                    context = context,
                    navController.navigate("main_screen")
                )
            }
        }
    }
    BackHandler {
        navController.navigate("main_screen")
    }
}

@Throws(IOException::class)
fun uriToFile(context: Context, uriString: String?): File {
    val uri = Uri.parse(uriString)
    var inputStream:InputStream?=null
    inputStream = try {
        context.contentResolver.openInputStream(uri)

    }
    catch (e:Exception){
        null
    }
    val file = File(context.cacheDir, "temp_file")
    val outputStream = FileOutputStream(file)
    try {
        inputStream?.copyTo(outputStream)
    } finally {
        inputStream?.close()
        outputStream.close()
    }
    return file
}

fun proceedToAdd(
    pname: String,
    sprice: String,
    tax: String,
    ptype: String,
    imgFile: File,
    context: Context,
    navController: Unit,
) {

    val productAdder = ProductAdder()

    val productName = pname
    val productType = ptype
    val price = sprice
    val tax = tax
    productAdder.addProduct(productName, productType, price, tax, imgFile) { success, _ ->
        if (success) {
            Toast.makeText(context, "SUCCESS", Toast.LENGTH_SHORT).show()
            navController
        } else {
            Toast.makeText(context, "FAIL", Toast.LENGTH_SHORT).show()
        }
    }

}

@Composable
fun CustomButton(
    text: String,
    backgroundColor: Color,
    textColor: Color,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .padding(8.dp)
            .background(
                color = backgroundColor,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick)
            .width(270.dp)
            .height(50.dp)
    ) {
        Text(
            text = text,
            color = textColor,
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.Center)
        )
    }
}

@Composable
fun ProductTypeDropdown(
    productTypes: List<String>,
    selectedType: String,
    onTypeSelected: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .border(1.dp, Color.Gray, shape = MaterialTheme.shapes.small)
                .clickable(onClick = { expanded = true })
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = selectedType.ifEmpty { "Select Type" },
                style = if (selectedType.isNotEmpty()) MaterialTheme.typography.bodyMedium else MaterialTheme.typography.bodyLarge,
                color = if (selectedType.isNotEmpty()) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(
                    alpha = 0.6f
                )
            )
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = null,
                modifier = Modifier.align(Alignment.CenterEnd)
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            productTypes.forEach { type ->
                DropdownMenuItem(
                    onClick = {
                        onTypeSelected(type)
                        expanded = false
                    },
                    text = {
                        Text(
                            text = type,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun ProductTypeSelectionScreen() {
    val productTypes = listOf("Product", "Service", "Special", "xyz")
    var selectedType by remember { mutableStateOf(productTypes[0]) }
    ptype = selectedType
    Column(
        modifier = Modifier
            .width(270.dp)
            .height(120.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        ProductTypeDropdown(
            productTypes = productTypes,
            selectedType = selectedType,
            onTypeSelected = { newType ->
                selectedType = newType
            }
        )
    }
}

@Composable
fun ProductTypeTextField(
    hintText: String,
    keyBoardType: KeyboardType,
    onValueChanged: (String) -> Unit,
) {
    var text by remember { mutableStateOf("") }

    OutlinedTextField(
        value = text,
        onValueChange = {
            text = it
            onValueChanged(it)
        },
        label = { Text(text = hintText) },
        keyboardOptions = KeyboardOptions(
            keyboardType = keyBoardType,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = { /* Handle the done action if needed */ }
        ),
        modifier = Modifier.width(270.dp)
    )
}
