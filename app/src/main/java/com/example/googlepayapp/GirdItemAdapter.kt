package com.example.googlepayapp

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.googlepayapp.GridItem

class GirdItemAdapter(private val itemList: ArrayList<GridItem>) :
    RecyclerView.Adapter<GirdItemAdapter.GridItemViewHolder>() {
    class GridItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemImage: ImageView = itemView.findViewById(R.id.img_item);
        val itemText: TextView = itemView.findViewById(R.id.txt_item);
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GridItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_grid, parent, false)
        return GridItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: GridItemViewHolder, position: Int) {
        val item = itemList[position]
        holder.itemImage.setImageResource(item.image)
        holder.itemText.text = item.text
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}