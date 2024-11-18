package com.tghrsyahptra.dicodingeventapp.data.remote.retrofit

import com.tghrsyahptra.dicodingeventapp.data.remote.response.EventResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Antarmuka untuk layanan API yang menyediakan metode untuk mendapatkan data acara.
 */
interface ApiService {
    /**
     * Mengambil daftar acara berdasarkan parameter yang ditentukan.
     *
     * @param active Menentukan status acara: 1 untuk acara yang akan datang, 0 untuk acara selesai, -1 untuk semua acara.
     * @param query Kata kunci pencarian untuk memfilter acara (opsional).
     * @param limit Jumlah maksimum acara yang akan diambil (default: 40).
     * @return Respons yang berisi daftar acara.
     */
    @GET("events")
    suspend fun getEvents(
        @Query("active") active: Int = 1, // 1 untuk acara yang akan datang, 0 untuk acara selesai, -1 untuk semua
        @Query("q") query: String? = null,
        @Query("limit") limit: Int = 40
    ): EventResponse
}