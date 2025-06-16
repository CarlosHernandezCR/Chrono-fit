package com.example.chrono_fit_app.di


import android.content.Context
import com.example.chrono_fit_app.domain.audio.MusicPlayerI
import com.example.chrono_fit_app.infrastructure.audio.MusicPlayer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AudioModule {
    @Provides
    @Singleton
    fun provideMusicPlayer(@ApplicationContext context: Context): MusicPlayerI {
        return MusicPlayer(context)
    }
}