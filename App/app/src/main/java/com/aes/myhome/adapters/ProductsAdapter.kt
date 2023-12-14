package com.aes.myhome.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aes.myhome.R
import com.aes.myhome.objects.Product

class ProductsAdapter(private val list: List<Product>) : RecyclerView.Adapter<ProductsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = list[position]
        holder.productNameView.text = product.name
        holder.productDescriptionView.text = product.description
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(productView: View) : RecyclerView.ViewHolder(productView) {
        val productNameView: TextView = productView.findViewById(R.id.product_name_text)
        val productDescriptionView: TextView = productView.findViewById(R.id.product_description_text)
    }
}
