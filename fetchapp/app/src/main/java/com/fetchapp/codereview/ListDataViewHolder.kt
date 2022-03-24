package com.fetchapp.codereview

import androidx.recyclerview.widget.RecyclerView
import com.fetchapp.codereview.databinding.RecyclerRowItemBinding

class ListDataViewHolder(private val binding: RecyclerRowItemBinding) : RecyclerView.ViewHolder(binding.root){
    //View Holder class that uses the recycler_row_item layout for binding

        //Bind the data to the view
        fun bind(listID : Int, name: String, itemID : Int) {
            //Set text view text data based on inputs
            binding.listId.text = listID.toString()
            binding.name.text = name
            binding.itemId.text = itemID.toString()
        }

}