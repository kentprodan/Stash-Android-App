package com.example.stash.network

import android.util.Log
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.cache.normalized.api.NormalizedCacheFactory
import com.apollographql.apollo3.cache.normalized.normalizedCache
import com.apollographql.apollo3.network.okHttpClient
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

class GraphqlClient {
    fun create(baseUrl: String, apiKey: String, cacheFactory: NormalizedCacheFactory? = null): ApolloClient {
        val loggingInterceptor = HttpLoggingInterceptor { message ->
            Log.d("GraphQL", message)
        }.apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val okClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(ApiKeyInterceptor(apiKey))
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        var builder = ApolloClient.Builder()
            .serverUrl(baseUrl.trimEnd('/') + "/graphql")
            .okHttpClient(okClient)

        if (cacheFactory != null) {
            builder = builder.normalizedCache(cacheFactory)
        }

        return builder.build()
    }

    private class ApiKeyInterceptor(private val apiKey: String) : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request().newBuilder()
                .addHeader("ApiKey", apiKey)
                .addHeader("Content-Type", "application/json")
                .build()
            Log.d("GraphQL", "Request URL: ${request.url}")
            Log.d("GraphQL", "API Key: ${apiKey.take(10)}...")
            return chain.proceed(request)
        }
    }
}
