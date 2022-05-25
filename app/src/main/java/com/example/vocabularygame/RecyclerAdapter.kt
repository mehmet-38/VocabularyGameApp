package com.example.vocabularygame

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RecyclerAdapter(items: List<ScoreItem>):RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {



    private var toplistFriends: List<ScoreItem> = items

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.card_layout, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: RecyclerAdapter.ViewHolder, position: Int) {

        val sortedToplist = toplistFriends.sortedWith(compareByDescending<ScoreItem> { it.score.score }.thenBy { it.score.timestamp }.thenBy { it.score.name })

        holder.itemPos.text = (position + 1).toString()
        holder.itemName.text = sortedToplist[position].score.name
        holder.itemScore.text = sortedToplist[position].score.score.toString()
    }

    override fun getItemCount(): Int {
        return toplistFriends.size
    }
    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var itemPos: TextView
        var itemName: TextView
        var itemScore: TextView

        init{
            itemPos = itemView.findViewById(R.id.item_toplistPosition)
            itemName = itemView.findViewById(R.id.item_toplistName)
            itemScore = itemView.findViewById(R.id.item_toplistScore)
        }
    }
}