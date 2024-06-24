package com.example.fetchandroidtakehome

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fetchandroidtakehome.ui.theme.FetchAndroidTakehomeTheme
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

// using retrofit to grab data from url
val retrofit: Retrofit = Retrofit.Builder()
    .baseUrl("https://fetch-hiring.s3.amazonaws.com/")
    .addConverterFactory(GsonConverterFactory.create())
    .build()

val apiService: ApiService = retrofit.create(ApiService::class.java)

// data models for cards and individual items
data class Item(
    val id: Int,
    val listId: Int,
    val name: String
)

data class GroupedItem(
    val listId: Int,
    val items: List<Item>,
    var isExpanded: Boolean = false
)

// API service interface
interface ApiService {
    @GET("hiring.json")
    fun getItems(): Call<List<Item>>
}
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FetchAndroidTakehomeTheme {
                var groupedItems by remember { mutableStateOf<List<GroupedItem>>(emptyList()) }
                var errorMessage by remember { mutableStateOf<String?>(null) }

                // Fetch data from the API
                LaunchedEffect(Unit) {
                    apiService.getItems().enqueue(object : Callback<List<Item>> {
                        override fun onResponse(call: Call<List<Item>>, response: Response<List<Item>>) {
                            if (response.isSuccessful) {
                                groupedItems = filterData(response.body() ?: emptyList())
                            } else {
                                errorMessage = "Failed to load data: ${response.message()}"
                                Log.e("MainActivity", "Failed to load data: ${response.message()}")
                            }
                        }

                        override fun onFailure(call: Call<List<Item>>, t: Throwable) {
                            errorMessage = t.message
                            Log.e("MainActivity", "Network request failed", t)
                        }
                    })
                }

                // Scaffold changed to not interfere with camera notch
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .windowInsetsPadding(
                            WindowInsets.safeContent
                                .only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top)
                        )
                ) { innerPadding ->
                    if (errorMessage != null) {
                        ErrorScreen(errorMessage = errorMessage!!, modifier = Modifier.padding(innerPadding))
                    } else {
                        GroupedItemList(groupedItems = groupedItems, modifier = Modifier.padding(innerPadding))
                    }
                }
            }
        }
    }
    private fun filterData(items: List<Item>): List<GroupedItem> {
        // filter out empty names, group by listID,
        // sort map (sorted by listId), sort by int in name after grouping
        return items
            .filter { !it.name.isNullOrEmpty() }
            .groupBy {it.listId}
            .toSortedMap()
            .map { entry ->
                GroupedItem(
                    listId = entry.key,
                    items = entry.value.sortedWith( compareBy {getInt(it.name) })
                )
            }
    }

    // Function to extract number from name for sorting by Item number
    private fun getInt(name: String?): Int {
        return name?.substringAfter("Item ")?.toIntOrNull() ?: Int.MAX_VALUE
    }
}

// group items
@Composable
fun GroupedItemList(groupedItems: List<GroupedItem>, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        items(groupedItems) { groupedItem ->
            GroupedItemCard(groupedItem = groupedItem)
        }
    }
}

@Composable
fun GroupedItemCard(groupedItem: GroupedItem) {
    var isExpanded by remember { mutableStateOf(groupedItem.isExpanded) }

    // expand card if clicked to show all items
    // card title is list id
    // card content is the items, sorted by name
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { isExpanded = !isExpanded }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "List ID: ${groupedItem.listId}")
            // expanded column, show show all items in group
            if (isExpanded) {
                groupedItem.items.forEach { item ->
                    Text(text = "ID: ${item.id}, Name: ${item.name}")
                }
            }
        }
    }
}

@Composable
fun ErrorScreen(errorMessage: String, modifier: Modifier = Modifier) {
    Text(text = "Error: $errorMessage", modifier = modifier)
}
