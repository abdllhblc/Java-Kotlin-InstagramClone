package com.abdullah.kotlinjavainstagram.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.abdullah.kotlinjavainstagram.databinding.ActivityMainBinding
import com.abdullah.kotlinjavainstagram.databinding.RecyclerRowBinding
import com.abdullah.kotlinjavainstagram.model.Post
import com.squareup.picasso.Picasso

class FeedAdapter(val postList: List<Post>): RecyclerView.Adapter<FeedAdapter.FeedHolder>() {

    class FeedHolder(val binding: RecyclerRowBinding) : RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedHolder {
        val binding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return FeedHolder(binding)
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    override fun onBindViewHolder(holder: FeedHolder, position: Int) {
        holder.binding.recyclerViewEmail.text = postList.get(position).email
        holder.binding.recyclerViewComment.text = postList.get(position).comment
        holder.binding.recyclerViewTime.text = postList.get(position).time.toString()
        Picasso.get().load(postList.get(position).downloadUrl).into(holder.binding.recyclerImage)
    }
}