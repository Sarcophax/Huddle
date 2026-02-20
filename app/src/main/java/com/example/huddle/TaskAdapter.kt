package com.example.huddle

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import com.example.huddle.models.TaskData

class TaskAdapter
    (
    private var taskList: List<TaskData>,
    private val onCloseClicked: (TaskData) -> Unit,
    private val onReadMore: (TaskData) -> Unit,
    private val onPendingClicked: (TaskData) -> Unit,
    private val onDoingClicked: (TaskData) -> Unit,
    private val onFinishClicked: (TaskData) -> Unit,
            ) :
    RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    fun updateList(newList: List<TaskData>) {
        this.taskList = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.task_item_message, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val currentItem = taskList[position]
        val context = holder.itemView.context

        val pendingColor = androidx.core.content.ContextCompat.getColor(context, R.color.pending)
        val doingColor = androidx.core.content.ContextCompat.getColor(context, R.color.doing)
        val finishColor = androidx.core.content.ContextCompat.getColor(context, R.color.finish)
        val colorDark = androidx.core.content.ContextCompat.getColor(context, R.color.itemMessageBackground)

        val lowPrioLevelColor = androidx.core.content.ContextCompat.getColor(context, R.color.low)
        val midPrioLevelColor = androidx.core.content.ContextCompat.getColor(context, R.color.mid)
        val highPrioLevelColor = androidx.core.content.ContextCompat.getColor(context, R.color.high)


        var prioLevelText:String

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

        // Pending Reset
        holder.pendingBtn.setBackgroundResource(R.drawable.item_message_pending_button_shape)
        holder.pendingBtn.backgroundTintList = null
        holder.pendingBtn.setTextColor(pendingColor)

        // Doing Reset
        holder.doingBtn.setBackgroundResource(R.drawable.item_message_doing_button_shape)
        holder.doingBtn.backgroundTintList = null
        holder.doingBtn.setTextColor(doingColor)

        // Finish Reset
        holder.finishBtn.setBackgroundResource(R.drawable.item_message_finish_button_shape)
        holder.finishBtn.backgroundTintList = null
        holder.finishBtn.setTextColor(finishColor)

        when (currentItem.status) {
            0 -> {
                holder.pendingBtn.backgroundTintList = android.content.res.ColorStateList.valueOf(pendingColor)
                holder.pendingBtn.setTextColor(colorDark)
            }
            1 -> {
                holder.doingBtn.backgroundTintList = android.content.res.ColorStateList.valueOf(doingColor)
                holder.doingBtn.setTextColor(colorDark)
            }
            2 -> {
                holder.finishBtn.backgroundTintList = android.content.res.ColorStateList.valueOf(finishColor)
                holder.finishBtn.setTextColor(colorDark)
            }
        }

        holder.prioTxt.text = prioLevelText
        holder.mentorName.text = currentItem.mentorName
        holder.titleTxt.text = currentItem.title
        holder.detailsTxt.text = currentItem.details

        holder.closeBtn.setOnClickListener {
            onCloseClicked(currentItem)
        }

        holder.pendingBtn.setOnClickListener {
            onPendingClicked(currentItem)
        }

        holder.doingBtn.setOnClickListener {
            onDoingClicked(currentItem)
        }

        holder.finishBtn.setOnClickListener {
            onFinishClicked(currentItem)
        }

        holder.readMoreBtn.setOnClickListener {
            onReadMore(currentItem)
        }
    }

    override fun getItemCount(): Int {
        return taskList.size
    }

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val prioTxt: TextView = itemView.findViewById(R.id.prioTxtView)
        val closeBtn: ImageButton = itemView.findViewById(R.id.imageButton)
        val readMoreBtn: LinearLayout = itemView.findViewById(R.id.item_message_layout)
        val mentorName: TextView = itemView.findViewById(R.id.mentorNameTxt)
        val titleTxt: TextView = itemView.findViewById(R.id.titleTxtView)
        val detailsTxt: TextView = itemView.findViewById(R.id.detailsTxtView)
        val pendingBtn: Button = itemView.findViewById(R.id.pengdingBtn)
        val doingBtn: Button = itemView.findViewById(R.id.doingBtn)
        val finishBtn: Button = itemView.findViewById(R.id.finishBtn)

    }

}
