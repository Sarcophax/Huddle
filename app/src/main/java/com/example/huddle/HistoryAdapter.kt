package com.example.huddle

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.huddle.models.HistoryData
import com.example.huddle.models.TaskData

class HistoryAdapter (
    private var historyList: List<HistoryData>,
    private val onCloseClicked: (HistoryData) -> Unit,
    private val onReadMore: (HistoryData) -> Unit) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    fun updateList(newList: List<HistoryData>) {
        this.historyList = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.history_item_message, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val currentItem = historyList[position]
        val context = holder.itemView.context

        val lowPrioLevelColor = androidx.core.content.ContextCompat.getColor(context, R.color.low)
        val midPrioLevelColor = androidx.core.content.ContextCompat.getColor(context, R.color.mid)
        val highPrioLevelColor = androidx.core.content.ContextCompat.getColor(context, R.color.high)

        var prioLevelText:String

        val formatter = java.text.SimpleDateFormat("MM/dd/yyyy", java.util.Locale.getDefault())


        when(currentItem.prioLevel){
            0 -> {
                prioLevelText = "LOW"
                holder.prioTxt.backgroundTintList = android.content.res.ColorStateList.valueOf(lowPrioLevelColor)
            }
            1 -> {
                prioLevelText = "MID"
                holder.prioTxt.backgroundTintList = android.content.res.ColorStateList.valueOf(midPrioLevelColor)
            }
            2 -> {
                prioLevelText = "HIGH"
                holder.prioTxt.backgroundTintList = android.content.res.ColorStateList.valueOf(highPrioLevelColor)
            }
            else -> prioLevelText = "UNKNOWN"
        }

        holder.prioTxt.text = prioLevelText
        holder.mentorName.text = currentItem.mentorName
        holder.titleTxt.text = currentItem.title
        holder.detailsTxt.text = currentItem.details
        holder.dateFinishedTxt.text = currentItem.createdAt?.let { formatter.format(it) } ?: "Pending..."

        holder.closeBtn.setOnClickListener {
            onCloseClicked(currentItem)
        }

        holder.readMoreBtn.setOnClickListener {
            onReadMore(currentItem)
        }

    }


    override fun getItemCount(): Int {
        return historyList.size
    }


    class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val prioTxt: TextView = itemView.findViewById(R.id.prioTxtView)
        val closeBtn: ImageButton = itemView.findViewById(R.id.imageButton)
        val readMoreBtn: LinearLayout = itemView.findViewById(R.id.history_item_message)
        val mentorName: TextView = itemView.findViewById(R.id.mentorNameTxt)
        val titleTxt: TextView = itemView.findViewById(R.id.titleTxtView)
        val detailsTxt: TextView = itemView.findViewById(R.id.detailsTxtView)
        val dateFinishedTxt: TextView = itemView.findViewById(R.id.dateFinishedTxt)
    }
}