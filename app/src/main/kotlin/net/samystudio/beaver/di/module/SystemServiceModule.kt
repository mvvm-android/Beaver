package net.samystudio.beaver.di.module

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import dagger.Module
import dagger.Provides
import net.samystudio.beaver.di.qualifier.ApplicationContext
import javax.inject.Singleton

@Module
object SystemServiceModule
{
    @Provides
    @Singleton
    @JvmStatic
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(context)
}
