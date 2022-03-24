package com.fetchapp.codereview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.toolbox.Volley
import com.fetchapp.codereview.databinding.ActivityMainBinding
import androidx.activity.viewModels
import androidx.lifecycle.Observer


class MainActivity : AppCompatActivity() {
    //Create binding backing store (can be null)
    private var _binding: ActivityMainBinding? = null
    //Create binding to be called (provides null check)
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view viewable?"
        }
    private val model : ListDataViewModel by viewModels()
    //When activity is created
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //inflate layout using viewBinding and get binding backing store
        _binding = ActivityMainBinding.inflate(layoutInflater)
        //bind the root view
        setContentView(binding.root)
        //set recycler layout manager to be linear (shows in a vertical list)
        binding.listDataRecycler.layoutManager = LinearLayoutManager(this)

        //Setup observer for model view data
        val dataObserver = Observer<MutableList<ListData>> {
            //Data has been changed, so we add the new data to the custom adapter to bind the viewholders and change the item counts
            val adapter = model.data.value?.let { it1 -> ListDataAdapter(it1) }
            binding.listDataRecycler.adapter = adapter
        }
        //Setup network queue to send GET request
        val queue = Volley.newRequestQueue(this)
        //Start observing with the dataObserver to update the adapter
        model.data.observe(this, dataObserver)
        //Sent request and retrieve JSON Array from URI
        val url = "https://fetch-hiring.s3.amazonaws.com/hiring.json"
        //Invoke model to make the request
        model.makeRequest(url, queue)

    }


}

