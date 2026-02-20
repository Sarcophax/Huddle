package com.example.huddle

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.huddle.models.HistoryData

class ReadMoreDialogHistory(history: HistoryData) : DialogFragment() {

    val currentTask = history

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.read_more_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var prioLevelText:String
        val prioTxt: TextView = view.findViewById(R.id.prioTxtView)
        val mentorName: TextView = view.findViewById(R.id.mentorNameTxt)
        val titleTxt: TextView = view.findViewById(R.id.titleTxtView)
        val detailsTxt: TextView = view.findViewById(R.id.detailsTxtView)
        val backBtn: Button = view.findViewById(R.id.backBtn)

        val lowPrioLevelColor = androidx.core.content.ContextCompat.getColor(requireContext(), R.color.low)
        val midPrioLevelColor = androidx.core.content.ContextCompat.getColor(requireContext(), R.color.mid)
        val highPrioLevelColor = androidx.core.content.ContextCompat.getColor(requireContext(), R.color.high)

        when(currentTask.prioLevel){
            0 -> {
                prioLevelText = "LOW"
                prioTxt.backgroundTintList = android.content.res.ColorStateList.valueOf(lowPrioLevelColor)
            }
            1 -> {
                prioLevelText = "MID"
                prioTxt.backgroundTintList = android.content.res.ColorStateList.valueOf(midPrioLevelColor)
            }
            2 -> {
                prioLevelText = "HIGH"
                prioTxt.backgroundTintList = android.content.res.ColorStateList.valueOf(highPrioLevelColor)
            }
            else -> prioLevelText = "UNKNOWN"
        }

        prioTxt.text = prioLevelText
        mentorName.text = currentTask.mentorName
        titleTxt.text = currentTask.title
        detailsTxt.text = currentTask.details

        backBtn.setOnClickListener {
            dismiss()
        }
    }

    override fun onStart() {
        super.onStart()

        dialog?.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

            setBackgroundDrawable(android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT))

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND)

                attributes.blurBehindRadius = 110

                setDimAmount(0.7f)
            }
        }
    }
}
