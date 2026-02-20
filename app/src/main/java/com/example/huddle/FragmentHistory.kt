package com.example.huddle

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.huddle.viewModel.HistoryViewModel
import kotlinx.coroutines.launch

class FragmentHistory : Fragment(R.layout.fragment_history) {

    lateinit var recyclerView: RecyclerView
    lateinit var viewModel: HistoryViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(HistoryViewModel::class.java)

        recyclerView = view.findViewById<RecyclerView>(R.id.history_recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())


        val adapter = HistoryAdapter(
            emptyList(),
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
    }
}