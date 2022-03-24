package com.fetchapp.codereview

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

//Create data class to hold information
@JsonClass(generateAdapter = true)
data class ListData(var listId: Int, var name: String? = "", @Json(name="id") var itemID: Int) {
}