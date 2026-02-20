package com.example.huddle

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.huddle.models.HistoryData
import com.example.huddle.models.TaskData
import com.example.huddle.viewModel.TaskViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class FragmentTask : Fragment(R.layout.fragment_task) {

    lateinit var recyclerView: RecyclerView
    lateinit var progress: ProgressBar
    lateinit var viewModel: TaskViewModel

    private var currentStatusFilter: Int = -1
    private var currentPriorityFilter: Int = -1
    private var fullTaskList: List<TaskData> = emptyList()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        viewModel = ViewModelProvider(this).get(TaskViewModel::class.java)
        progress = requireActivity().findViewById<ProgressBar>(R.id.main_progressBar)

        recyclerView = view.findViewById<RecyclerView>(R.id.task_recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val currentDateTxt: TextView = view.findViewById(R.id.currentDateTxt)

        val statusAllBtn:Button = view.findViewById<Button>(R.id.statusAllBtn)
        val statusPendingBtn:Button = view.findViewById<Button>(R.id.statusPendingBtn)
        val statusDoingBtn:Button = view.findViewById<Button>(R.id.statusDoingBtn)

        val prioAllBtn:Button = view.findViewById<Button>(R.id.prioAllBtn)
        val prioLowBtn:Button = view.findViewById<Button>(R.id.prioLowBtn)
        val prioMidBtn:Button = view.findViewById<Button>(R.id.prioMidBtn)
        val prioHighBtn:Button = view.findViewById<Button>(R.id.prioHighBtn)

        val current = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy")
        val formatted = current.format(formatter)

        val adapter = TaskAdapter(
            emptyList(),

            onCloseClicked = { clickedTask ->
                showDeleteConfirmationDialog(clickedTask)
            },

            onReadMore = { clickedTask ->
                val dialog = ReadMoreDialogTask(clickedTask)
                dialog.show(parentFragmentManager, "ReadMoreLayout")
            },

            onPendingClicked  = { clickedTask ->
                viewModel.updateTaskStatus(clickedTask.taskId,0)
            },

            onDoingClicked  = { clickedTask ->
                viewModel.updateTaskStatus(clickedTask.taskId,1)
            },

            onFinishClicked  = { clickedTask ->
                viewModel.updateTaskStatus(clickedTask.taskId,2)
                viewModel.addHistory(HistoryData(clickedTask.taskId, clickedTask.prioLevel, clickedTask.title, clickedTask.details, clickedTask.mentorName))
                viewModel.deleteTask(clickedTask.taskId)
            },
            )


        currentDateTxt.text = formatted



        recyclerView.adapter = adapter

        // Status Buttons
        statusAllBtn.setOnClickListener { updateFilters(-1, currentPriorityFilter) }
        statusPendingBtn.setOnClickListener { updateFilters(0, currentPriorityFilter) }
        statusDoingBtn.setOnClickListener { updateFilters(1, currentPriorityFilter) }

        // Priority Buttons
        prioAllBtn.setOnClickListener { updateFilters(currentStatusFilter, -1) }
        prioLowBtn.setOnClickListener { updateFilters(currentStatusFilter, 0) }
        prioMidBtn.setOnClickListener { updateFilters(currentStatusFilter, 1) }
        prioHighBtn.setOnClickListener { updateFilters(currentStatusFilter, 2) }


        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModel.taskList.collect { tasks ->
                    fullTaskList = tasks

                    applyFiltersToAdapter(adapter)
                    progress.visibility = View.GONE
                }
            }
        }
    }

    private fun updateFilters(status: Int, priority: Int) {
        currentStatusFilter = status
        currentPriorityFilter = priority
        applyFiltersToAdapter(recyclerView.adapter as TaskAdapter)
    }

    private fun applyFiltersToAdapter(adapter: TaskAdapter) {
        val filteredList = fullTaskList.filter { task ->
            val matchesStatus = (currentStatusFilter == -1 || task.status == currentStatusFilter)
            val matchesPriority = (currentPriorityFilter == -1 || task.prioLevel == currentPriorityFilter)
            matchesStatus && matchesPriority
        }
        adapter.updateList(filteredList)
    }

    private fun showDeleteConfirmationDialog(task: TaskData) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_delete, null)

        val builder = AlertDialog.Builder(requireContext())
        builder.setView(dialogView)
        val alertDialog = builder.create()

        alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val title = dialogView.findViewById<TextView>(R.id.deleteTitleTxt)
        val yesBtn = dialogView.findViewById<Button>(R.id.yesBtn)
        val noBtn = dialogView.findViewById<Button>(R.id.noBtn)

        title.text = "${task.title}"

        yesBtn.setOnClickListener {
            viewModel.deleteTask(task.taskId)
            alertDialog.dismiss()
        }

        noBtn.setOnClickListener {
            alertDialog.dismiss()
        }
        alertDialog.show()
    }
}


