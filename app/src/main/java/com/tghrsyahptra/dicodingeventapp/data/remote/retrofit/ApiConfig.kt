package com.tghrsyahptra.dicodingeventapp.data.remote.retrofit

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Kelas konfigurasi untuk mengatur Retrofit dan ApiService.
 */
class ApiConfig {
    companion object {
        /**
         * Membuat dan mengembalikan instance ApiService.
         *
         * @return ApiService instance untuk berkomunikasi dengan API.
         */
        fun getApiService(): ApiService {
            // Membuat interceptor untuk logging HTTP request dan response
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            // Membuat OkHttpClient dengan interceptor
            val client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build()

            // Membangun Retrofit instance
            val retrofit = Retrofit.Builder()
                .baseUrl("https://event-api.dicoding.dev/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()

            // Mengembalikan ApiService
            return retrofit.create(ApiService::class.java)
        }
    }
}