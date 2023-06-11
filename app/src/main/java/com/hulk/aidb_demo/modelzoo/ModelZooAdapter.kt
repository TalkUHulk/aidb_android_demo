package com.hulk.aidb_demo.modelzoo
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hulk.aidb_demo.R as AR

class ModelZooAdapter(modelList: List<Model>): RecyclerView.Adapter<ModelZooAdapter.ViewHolder>() {
    private var modelZoo: List<Model> = modelList

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = itemView.findViewById(AR.id.image)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(AR.layout.item_shop_card, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = modelZoo.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(holder.itemView.context)
            .load(modelZoo[position].image)
            .into(holder.image)
    }

}