package com.saitejajanjirala.reminder.di

import android.content.Context
import androidx.room.Room
import com.saitejajanjirala.reminder.db.DatabaseService
import com.saitejajanjirala.reminder.db.ReminderDao
import com.saitejajanjirala.reminder.utils.Keys
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun localDb(@ApplicationContext context: Context) : DatabaseService {
        return DatabaseService.getInstance(context)
    }

}