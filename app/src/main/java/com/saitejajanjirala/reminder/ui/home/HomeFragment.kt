package com.saitejajanjirala.reminder.ui.home

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.saitejajanjirala.reminder.databinding.FragmentHomeBinding
import com.saitejajanjirala.reminder.db.Reminder
import com.saitejajanjirala.reminder.models.Status
import com.saitejajanjirala.reminder.receivers.NotificationReceiver
import com.saitejajanjirala.reminder.utils.Helper
import com.saitejajanjirala.reminder.utils.Keys
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private lateinit var viewBinding: FragmentHomeBinding
    private lateinit var calendar: Calendar
    private  var title = ""
    private var dateAndTime = ""
    private val viewModel : HomeViewModel by viewModels()
    private lateinit var alarmManager: AlarmManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    private val broadcastReceiver =object : BroadcastReceiver(){

        override fun onReceive(p0: Context?, p1: Intent?) {
            if(p1 != null && p1.action.equals(AlarmManager.ACTION_SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED)){
                scheduleReminder()
            }
        }

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragmentHomeBinding.inflate(inflater, container, false)
        initView()
        setUpObservers()
        alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        registerBroadCastReceiver()
        return viewBinding.root
    }

    private fun setUpObservers() {
        viewBinding.title.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                if(p0 != null){
                    title = p0.toString()
                }
            }

        })
        viewModel.result.observe(viewLifecycleOwner) {
            when(it.status){
                Status.SUCCESS->{
                    viewBinding.progressBar.visibility = View.GONE
                    calendar = Calendar.getInstance()
                    dateAndTime = ""
                    viewBinding.time.text = ""
                    Toast.makeText(context, "Reminder $title is successfully set", Toast.LENGTH_LONG).show()
                    title = ""
                    viewBinding.title.setText("")
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

    private fun registerBroadCastReceiver() {
        context?.registerReceiver(broadcastReceiver, IntentFilter(AlarmManager.ACTION_SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED))
    }

    private fun initView() {
        viewBinding.pickTime.setOnClickListener {
            onClickPickUpDate()
        }
        viewBinding.setReminder.setOnClickListener {
            if(this::calendar.isInitialized){
                if(calendar.timeInMillis < Calendar.getInstance().timeInMillis || TextUtils.isEmpty(title)){
                    if(TextUtils.isEmpty(title)){
                        Toast.makeText(
                            context,
                            "please enter the title for the reminder",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                    else {
                        Toast.makeText(
                            context,
                            "select proper date and time in future",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                }
                else{
                    scheduleReminder()
                }
            }
            else{
                Toast.makeText(
                    context,
                    "Please select date and time",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }

        }
        viewBinding.viewReminders.setOnClickListener {
            val action: NavDirections = HomeFragmentDirections.actionHomeFragmentToRemindersFragment()
            Navigation.findNavController(viewBinding.root).navigate(action)
        }
    }

    private fun scheduleReminder(){
        if(isGreaterThanTwelve()){
            if(alarmManager.canScheduleExactAlarms()){
                scheduleWithAlarmManager()
            }
            else {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                startActivity(intent)
            }
        }
        else{
            scheduleWithAlarmManager()
        }
    }

    private fun scheduleWithAlarmManager() {
        val time = calendar.timeInMillis
        val reminder = Reminder(
            id = System.currentTimeMillis(),
            name = title,
            time = time,
            shown = false
        )
        alarmManager.setExact(AlarmManager.RTC_WAKEUP,time,getPendingIntent(reminder))
        viewModel.saveNotification(reminder)
    }

    private fun getPendingIntent(reminder: Reminder): PendingIntent{
        val intent = Intent(context,NotificationReceiver::class.java)
        intent.putExtra(Keys.REMINDER_EXTRA,Helper.reminderToString(reminder))
        var flag = PendingIntent.FLAG_UPDATE_CURRENT
        if(isGreaterThanTwelve()){
            flag = PendingIntent.FLAG_IMMUTABLE
        }
        return PendingIntent.getBroadcast(context,0,intent,flag)
    }

    private fun onClickPickUpDate() {
        val builder = MaterialDatePicker.Builder.datePicker()
        builder.setTitleText("Select Reminder Date")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
        val picker = builder.build()
        picker.show(activity!!.supportFragmentManager, "saiteja")

        picker.addOnPositiveButtonClickListener {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val calendarTemp = Calendar.getInstance()
            calendarTemp.timeInMillis = it
            sdf.calendar = calendarTemp
            if(!this::calendar.isInitialized){
                calendar = Calendar.getInstance()
            }
            calendar.set(Calendar.DAY_OF_YEAR,sdf.calendar.get(Calendar.DAY_OF_YEAR))
            calendar.set(Calendar.MONTH,sdf.calendar.get(Calendar.MONTH))
            calendar.set(Calendar.YEAR,sdf.calendar.get(Calendar.YEAR))
            dateAndTime = sdf.format(it)
            onClickPickUpTime()
        }
    }

    private fun onClickPickUpTime() {
        var timeFormat = TimeFormat.CLOCK_12H
        if(DateFormat.is24HourFormat(context)){
            timeFormat = TimeFormat.CLOCK_24H
        }
        val picker = MaterialTimePicker.Builder().setTimeFormat(timeFormat)
            .setHour(Calendar.getInstance().get(Calendar.HOUR_OF_DAY))
            .setMinute(Calendar.getInstance().get(Calendar.MINUTE))
            .setTitleText("Select Reminder Time")
            .build();

        picker.show(activity!!.supportFragmentManager, "saiteja")

        picker.addOnPositiveButtonClickListener {

            if(picker.minute >=10) {
                dateAndTime+=" "+picker.hour.toString() + " : " + picker.minute
            }
            else{
               dateAndTime+= " "+picker.hour.toString() + " : 0" + picker.minute
            }
            viewBinding.time.text = dateAndTime
            if(!this::calendar.isInitialized){
                calendar = Calendar.getInstance()
            }
            calendar.set(Calendar.HOUR_OF_DAY, picker.hour)
            calendar.set(Calendar.MINUTE, picker.minute)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
        }
    }

    override fun onDestroyView() {
        context?.unregisterReceiver(broadcastReceiver)
        super.onDestroyView()
    }

    private fun isGreaterThanTwelve() : Boolean
    {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    }
}