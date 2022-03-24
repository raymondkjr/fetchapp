package com.fetchapp.codereview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fetchapp.codereview.databinding.RecyclerRowItemBinding

class ListDataAdapter(var data : MutableList<ListData>) : RecyclerView.Adapter<ListDataViewHolder>() {
    //Create new viewHolder using custom view holder class
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListDataViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RecyclerRowItemBinding.inflate(inflater, parent, false)
        return ListDataViewHolder(binding)
    }
    //Bind data to view holder. Calls view holder class' bind() method
    override fun onBindViewHolder(holder: ListDataViewHolder, position: Int) {
        val item = data[position]
        item.name?.let { holder.bind(item.listId, it, item.itemID) }
    }

    //Item count is data.size
    override fun getItemCount(): Int {
        return data.size
    }

}