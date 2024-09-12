package com.example.googlepayapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView

class BillsAdapter(
    private val context: Context,
    private val billList: ArrayList<BillItem>,
) :
    BaseAdapter() {
    override fun getCount(): Int {
        return billList.size
    }

    override fun getItem(position: Int): Any {
        return billList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {

        var view = convertView
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_grid_recycler, parent, false)
        }
        val logo: ImageView = view!!.findViewById(R.id.bill_logo)
        val text: TextView = view.findViewById(R.id.txt_bill)

        val billItem: BillItem = billList[position]

        logo.setImageResource(billItem.logo)
        text.text = billItem.text

        return view
    }


}