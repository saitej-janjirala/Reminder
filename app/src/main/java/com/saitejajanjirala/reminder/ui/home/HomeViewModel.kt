package com.saitejajanjirala.reminder.ui.home

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saitejajanjirala.reminder.db.DatabaseService
import com.saitejajanjirala.reminder.db.Reminder
import com.saitejajanjirala.reminder.db.ReminderDao
import com.saitejajanjirala.reminder.models.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.*
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val databaseService: DatabaseService
): ViewModel() {

    private val TAG = HomeViewModel::class.simpleName
    val result = MutableLiveData<Result<Long>>()
    private val reminderDao : ReminderDao = databaseService.reminderDao()

    fun saveNotification(reminder: Reminder){
        insertData(reminder)
    }

    private fun insertData(reminder: Reminder) {
       viewModelScope.launch(Dispatchers.IO) {
           try {
               coroutineScope {
                   try {
                       result.postValue(Result.loading(null))
                       val transaction = async {
                           reminderDao.insert(reminder)
                       }
                       transaction.await().let {
                           result.postValue(Result.success(it))
                       }
                   }catch (e1 : Exception){
                       result.postValue(Result.error(e1.message!!,null))
                   }
               }
           }
           catch (e :Exception){
               result.postValue(Result.error(e.message!!,null))
               Log.e(TAG,e.message!!)
           }
       }
    }
}