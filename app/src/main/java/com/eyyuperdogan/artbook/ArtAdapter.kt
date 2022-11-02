package com.eyyuperdogan.artbook

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.eyyuperdogan.artbook.databinding.RecycleRowBinding

class ArtAdapter(val artlist: ArrayList<art>): RecyclerView.Adapter<ArtAdapter.artholder>() {
    class artholder(val  binding :RecycleRowBinding):RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): artholder {
        val binding=RecycleRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return artholder(binding)
    }

    override fun onBindViewHolder(holder: artholder, position: Int) {
        holder.binding.recyclerViewTextView.text=artlist.get(position).name
        holder.itemView.setOnClickListener{
            val intent=Intent(holder.itemView.context,Artactivity::class.java)
            intent.putExtra("info","old")
            intent.putExtra("id", artlist.get(position).id)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return artlist.size
    }
}