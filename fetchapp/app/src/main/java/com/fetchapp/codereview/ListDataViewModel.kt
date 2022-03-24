package com.fetchapp.codereview

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.volley.RequestQueue
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi

//Separate concerns
//Create a list data view model to survive lifecycle changes
class ListDataViewModel : ViewModel() {
    //Mutable Live Data to broadcast changes to activity/fragment
    //Useful since we are making a network call and we want the main activity to observe these changes when they are made
    val data : MutableLiveData<MutableList<ListData>> by lazy {
        MutableLiveData<MutableList<ListData>>()
    }
    lateinit var request : JsonArrayRequest

    //Make the request. We have to borrow the queue from the main activity because the queue requires a context
    fun makeRequest(url: String, queue : RequestQueue) {
        request = JsonArrayRequest(Request.Method.GET, url, null,
            {
            response ->
                //Get all the data and parse it with Moshi JSON parser
                var allData = mutableListOf<ListData>()
                val moshi : Moshi = Moshi.Builder().build()
                //Take the string and parse to JSON using the ListData class as a template
                val adapter : JsonAdapter<ListData> = moshi.adapter(ListData::class.java)
                for (ind in 0 until response.length()) {
                    //Parse ListData class from JSON String
                    val listItem = adapter.fromJson(response[ind].toString())
                    if (listItem != null) { //Null protection
                        if (listItem.name != "" && listItem.name != null){ //exclude items with name = null or name = ""
                            allData.add(listItem)
                        }
                    }
                }
                //change live data. this automatically notifies any observers that the data has changed
                allData.sortWith(ListDataComparator)
                data.value = allData
            },
            { error ->
                Log.e("NETWORK ERROR: ", error.message.toString())
            })
        queue.add(request)
    }

}

//Custom comparator class to sort the ListData
class ListDataComparator {
    companion object : Comparator<ListData> {
        override fun compare(x: ListData, y: ListData): Int {
            //Check if listID is the same, if so, sort by itemId which will essentially sort by itemName
            if (x.listId == y.listId) {
                return x.itemID - y.itemID
            }
            else { //listID is not the same, so sort by listId
                return x.listId - y.listId
            }
            //listID and itemID are the same (not likely to happen in this data set)
            return 0
        }
    }
}