package com.saitejajanjirala.reminder.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.saitejajanjirala.reminder.R
import com.saitejajanjirala.reminder.databinding.ReminderItemBinding
import com.saitejajanjirala.reminder.db.Reminder
import java.text.SimpleDateFormat
import java.util.*

class RemindersAdapter (private val  context: Context,private val remindersList : List<Reminder>): RecyclerView.Adapter<RemindersAdapter.RemindersViewHolder>() {
    class RemindersViewHolder(private val binding: ReminderItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindData(reminder: Reminder){
            binding.apply {
                name.text = reminder.name
                dateAndTime.text = getTimeInSimpleDateFormat(reminder)
                if(reminder.shown){
                    statusIcon.setImageResource(R.drawable.ic_shown)
                    statusText.text = binding.root.context.getString(R.string.shown_reminder)
                }
                else{
                    statusIcon.setImageResource(R.drawable.ic_not_shown)
                    statusText.text = binding.root.context.getString(R.string.reminder_not_shown)
                }
            }
        }

        private fun getTimeInSimpleDateFormat(reminder: Reminder): String? {
            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            sdf.calendar = reminder.time
            return sdf.format(reminder.time.timeInMillis)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RemindersViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.reminder_item,parent,false)
        val binding = ReminderItemBinding.bind(view)
        return RemindersViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RemindersViewHolder, position: Int) {
        val data = remindersList[position]
        holder.bindData(data)
    }

    override fun getItemCount() = remindersList.size


}