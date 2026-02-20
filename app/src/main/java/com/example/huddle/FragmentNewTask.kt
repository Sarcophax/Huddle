package com.example.huddle

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.example.huddle.models.TaskData
import com.example.huddle.viewModel.TaskViewModel

class FragmentNewTask : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_new_task, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModel: TaskViewModel = ViewModelProvider(this).get(TaskViewModel::class.java)

        val titleTxt: EditText = view.findViewById<EditText>(R.id.titleEditTxt)
        val mentorTxt: EditText = view.findViewById<EditText>(R.id.mentorEditTxt)
        val detailsTxt: EditText = view.findViewById<EditText>(R.id.detailsMultiEditTxt)
        val prioGrp: RadioGroup = view.findViewById<RadioGroup>(R.id.priorityRadioGrp)

        val saveBtn : Button = view.findViewById<Button>(R.id.saveTaskBtn)
        val cancelBtn : Button = view.findViewById<Button>(R.id.cancelTaskBtn)

        fun clearInputs() {
            titleTxt.text.clear()
            mentorTxt.text.clear()
            detailsTxt.text.clear()
            prioGrp.clearCheck() // SIGNPOST: Unselects all radio buttons
            titleTxt.requestFocus() // Moves cursor back to the first field
        }

        saveBtn.setOnClickListener {
            val title = titleTxt.text.toString().trim()
            val details = detailsTxt.text.toString().trim()
            val mentor = mentorTxt.text.toString().trim()

            val currentPrioId = prioGrp.checkedRadioButtonId
            val priorityValue = when (currentPrioId) {
                R.id.radioLow -> 0 // Low
                R.id.radioMid -> 1 // Mid
                R.id.radioHigh -> 2// High
                else -> -1 // Default to 0
            }


            if (title.isEmpty()) {
                titleTxt.error = "Title cannot be empty"
                titleTxt.requestFocus()
                return@setOnClickListener
            }

            if (mentor.isEmpty()) {
                mentorTxt.error = "Please provide your Mentor's name"
                mentorTxt.requestFocus()
                return@setOnClickListener
            }

            if (priorityValue == -1) {
                Toast.makeText(requireContext(), "Please select a priority level", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (details.isEmpty()) {
                detailsTxt.error = "Please provide some details"
                detailsTxt.requestFocus()
                return@setOnClickListener
            }

            val newTask = TaskData(
                prioLevel = priorityValue,
                status = 0, // Initial status
                title = title,
                details = details,
                mentorName = mentor,
            )

            viewModel.addTask(newTask)
            Toast.makeText(requireContext(), "Task Saved Successfully!", Toast.LENGTH_SHORT).show()

            clearInputs()
        }
        cancelBtn.setOnClickListener{
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, FragmentTask())
                .addToBackStack(null) // Allows the user to go back when they press the back button
                .commit()

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