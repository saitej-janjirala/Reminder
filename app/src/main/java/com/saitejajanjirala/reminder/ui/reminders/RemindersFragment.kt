package com.saitejajanjirala.reminder.ui.reminders

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.saitejajanjirala.reminder.R
import com.saitejajanjirala.reminder.adapters.RemindersAdapter
import com.saitejajanjirala.reminder.databinding.FragmentRemindersBinding
import com.saitejajanjirala.reminder.db.Reminder
import com.saitejajanjirala.reminder.models.Status
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class RemindersFragment : Fragment() {

    private val remindersViewModel: RemindersViewModel by viewModels()
    private lateinit var viewBinding : FragmentRemindersBinding
    private lateinit var listOfReminders : ArrayList<Reminder>
    private lateinit var adapter: RemindersAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragmentRemindersBinding.inflate(inflater, container, false)
        initViews()
        return viewBinding.root
    }

    private fun initViews() {
        listOfReminders = ArrayList()
        adapter = RemindersAdapter(context!!,listOfReminders)
        viewBinding.recyclerView.adapter = adapter
        setUpObservers()
        remindersViewModel.fetchReminders()
    }

    private fun setUpObservers() {
        remindersViewModel.result.observe(viewLifecycleOwner) {
            when(it.status){
                Status.SUCCESS->{
                    viewBinding.progressBar.visibility = View.GONE
                    if(it.data != null){
                        listOfReminders.clear()
                        listOfReminders.addAll(it.data)
                        adapter.notifyDataSetChanged()
                    }

                }
                Status.ERROR->{
                    viewBinding.progressBar.visibility = View.GONE
                    Toast.makeText(context, "${it.message}", Toast.LENGTH_LONG).show()
                }
                Status.LOADING->{
                    viewBinding.progressBar.visibility = View.VISIBLE
                }
            }
        }
    }

}