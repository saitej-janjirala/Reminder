package com.saitejajanjirala.reminder.ui.reminders

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saitejajanjirala.reminder.db.DatabaseService
import com.saitejajanjirala.reminder.db.Reminder
import com.saitejajanjirala.reminder.models.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class RemindersViewModel @Inject constructor(
    private val databaseService: DatabaseService
): ViewModel() {

    private val TAG = RemindersViewModel::class.simpleName
    private val reminderDao = databaseService.reminderDao()
    val result = MutableLiveData<Result<List<Reminder>>>()

    fun fetchReminders() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                coroutineScope {
                    try {
                        result.postValue(Result.loading(null))
                        val transaction = async {
                            reminderDao.retrieveAllReminders()
                        }
                        transaction.await().let {
                            result.postValue(Result.success(it))
                        }
                    }catch (e1 : Exception){
                        result.postValue(Result.error(e1.message!!,null))
                    }
                }
            }
            catch (e : Exception){
                result.postValue(Result.error(e.message!!,null))
                Log.e(TAG,e.message!!)
            }
        }
    }

}