package com.example.huddle

import android.os.Bundle
import android.os.Environment
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.huddle.models.HistoryData
import com.example.huddle.models.TaskData
import com.example.huddle.viewModel.HistoryViewModel
import kotlinx.coroutines.launch
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File

class FragmentHistory : Fragment(R.layout.fragment_history) {

    lateinit var recyclerView: RecyclerView
    lateinit var viewModel: HistoryViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val downloadHistory: ImageButton = view.findViewById(R.id.historyDownloadBtn)

        viewModel = ViewModelProvider(this).get(HistoryViewModel::class.java)

        recyclerView = view.findViewById<RecyclerView>(R.id.history_recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())


        val adapter = HistoryAdapter(
            emptyList(),
            onCloseClicked = { clickedHistory ->
                showDeleteConfirmationDialog(clickedHistory)
            },
            onReadMore = { clickedHistory ->
                val dialog = ReadMoreDialogHistory(clickedHistory)
                dialog.show(parentFragmentManager, "ReadMoreDialogHistory")
            })


        recyclerView.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModel.historyList.collect { histories ->

                    adapter.updateList(histories)
                    recyclerView.adapter = adapter

                }
            }
        }

        downloadHistory.setOnClickListener {
            viewModel.exportDataToExcel()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.exportStatus.collect { statusMessage ->

                if (statusMessage != null) {
                    Toast.makeText(requireContext(), statusMessage, Toast.LENGTH_SHORT).show()

                    viewModel.clearExportStatus()
                }
            }
        }
    }

    private fun showDeleteConfirmationDialog(history: HistoryData) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_delete, null)

        val builder = AlertDialog.Builder(requireContext())
        builder.setView(dialogView)
        val alertDialog = builder.create()

        alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val title = dialogView.findViewById<TextView>(R.id.deleteTitleTxt)
        val yesBtn = dialogView.findViewById<Button>(R.id.yesBtn)
        val noBtn = dialogView.findViewById<Button>(R.id.noBtn)

        title.text = "${history.title}"

        yesBtn.setOnClickListener {
            viewModel.deleteHistory(history.historyId)
            alertDialog.dismiss()
        }

        noBtn.setOnClickListener {
            alertDialog.dismiss()
        }
        alertDialog.show()
    }

}