package com.example.stash

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.util.DebugLogger
import com.example.stash.data.SettingsStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

class StashApplication : Application(), ImageLoaderFactory {
    override fun newImageLoader(): ImageLoader {
        val settingsStore = SettingsStore(this)
        
        // Get API key from settings synchronously (this is called during app initialization)
        val apiKey = runBlocking {
            settingsStore.apiKey.first()
        }
        
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.HEADERS
        }
        
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .apply {
                        if (!apiKey.isNullOrEmpty()) {
                            addHeader("ApiKey", apiKey)
                        }
                    }
                    .build()
                chain.proceed(request)
            }
            .addInterceptor(loggingInterceptor)
            .build()
        
        return ImageLoader.Builder(this)
            .okHttpClient(okHttpClient)
            .logger(DebugLogger())
            .respectCacheHeaders(false)
            .build()
    }
}
