package com.fetchapp.codereview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.fetchapp.codereview.databinding.ActivityMainBinding
import com.fetchapp.codereview.databinding.RecyclerRowItemBinding
import org.json.JSONArray


class MainActivity : AppCompatActivity() {
    //Create binding backing store (can be null)
    private var _binding: ActivityMainBinding? = null
    //Create binding to be called (provides null check)
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view viewable?"
        }

    //When activity is created
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //inflate layout using viewBinding and get binding backing store
        _binding = ActivityMainBinding.inflate(layoutInflater)
        //bind the root view
        setContentView(binding.root)
        //set recycler layout manager to be linear (shows in a vertical list)
        binding.listDataRecycler.layoutManager = LinearLayoutManager(this)

        //Setup network queue to send GET request
        val queue = Volley.newRequestQueue(this)

        //Sent request and retrieve JSON Array from URI
        val url = "https://fetch-hiring.s3.amazonaws.com/hiring.json"
        val jsonArray = JsonArrayRequest(Request.Method.GET, url, null,
            { response ->
                //Response came back with JSON Array, parse the array into a MutableList<ListData>
                val dataList = parseJsonArray(response)
                //Sort the list using custom comparator (defined below)
                dataList.sortWith(ListDataComparator)
                //Create the custom list adapter that uses the retrieved data
                val adapter = ListDataAdapter(dataList)
                //Set the recycler view adapter to the custom adapter
                binding.listDataRecycler.adapter = adapter
            },
            { error ->
                Log.e("MAINACTIVITY - NETWORK ERROR: ", error.message.toString())
            })
        //Add request to the queue and wait for response
        queue.add(jsonArray)
    }

    //Parse the response and produce a MutableList<ListData>
    private fun parseJsonArray(jsonArray: JSONArray) : MutableList<ListData>{
        var dataList = mutableListOf<ListData>()
        //Iterate through the JSONArray (not an iterable so we can't use .forEach{}
        for (ind in 0 until jsonArray.length()) {
            //Get the JSON object
            val obj = jsonArray.getJSONObject(ind)
            //Get the "name" value
            val name = obj.getString("name")
            //Exclude all data that has no name, a null name, or a blank name
            if (name == "" || name == null || name == "null") continue
            //Parse the rest of the data from the JSON object
            val listId = obj.getInt("listId")
            val id = obj.getInt("id")
            //Create ListData and add to the MutableList
            var dataObj = ListData(listId, name, id)
            dataList.add(dataObj)
        }
        return dataList

    }

    //View Holder class that uses the recycler_row_item layout for binding
    private inner class ListDataViewHolder(private val binding: RecyclerRowItemBinding): RecyclerView.ViewHolder(binding.root) {
        //Bind the data to the view
        fun bind(listID : Int, name: String, itemID : Int) {
            //Set text view text data based on inputs
            binding.listId.text = listID.toString()
            binding.name.text = name
            binding.itemId.text = itemID.toString()
        }
    }
    //Recycler Adapter custom class that uses the MutableList<ListData>
    private inner class ListDataAdapter(var data : MutableList<ListData>) : RecyclerView.Adapter<ListDataViewHolder>() {
        //Create new viewHolder using custom view holder class
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListDataViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = RecyclerRowItemBinding.inflate(inflater, parent, false)
            return ListDataViewHolder(binding)
        }
        //Bind data to view holder. Calls view holder class' bind() method
        override fun onBindViewHolder(holder: ListDataViewHolder, position: Int) {
            val item = data[position]
            holder.bind(item.listId, item.name, item.itemID)
        }

        //Item count is data.size
        override fun getItemCount(): Int {
            return data.size
        }

    }
}

//Custom comparator class to sort the ListData
internal class ListDataComparator {
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