package com.tghrsyahptra.dicodingeventapp.data.remote

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import com.tghrsyahptra.dicodingeventapp.data.local.entity.EventEntity
import com.tghrsyahptra.dicodingeventapp.data.local.room.EventDao
import com.tghrsyahptra.dicodingeventapp.data.remote.response.EventResponse
import com.tghrsyahptra.dicodingeventapp.data.remote.retrofit.ApiService
import com.tghrsyahptra.dicodingeventapp.utils.Result

/**
 * Kelas repositori untuk mengelola data acara dari API dan basis data lokal.
 */
class EventRepository(
    private val apiService: ApiService,
    private val eventDao: EventDao,
) {

    /**
     * Mengambil acara berdasarkan status aktif.
     * @param active Status acara: 1 untuk acara yang akan datang, 0 untuk acara selesai.
     * @return LiveData yang berisi hasil acara.
     */
    fun getEvents(active: Int): LiveData<Result<List<EventEntity>>> = liveData {
        emit(Result.Loading) // Emit loading state

        try {
            // Mengambil data acara dari API
            val response = apiService.getEvents(active)
            val events = response.listEvents

            // Mengubah daftar acara menjadi daftar Entity dan menyimpan ke database
            val eventList = events?.map { event ->
                val isFavorite = event.name?.let { eventDao.isEventFavorite(it) }
                val isUpcoming = active == 1
                val isFinished = active == 0
                EventEntity(
                    event.id,
                    event.name,
                    event.summary,
                    event.description,
                    event.imageLogo,
                    event.mediaCover,
                    event.category,
                    event.ownerName,
                    event.cityName,
                    event.quota,
                    event.registrants,
                    event.beginTime,
                    event.endTime,
                    event.link,
                    isFavorite,
                    isUpcoming,
                    isFinished
                )
            }
            eventDao.insertEvents(eventList) // Simpan daftar acara ke Room
        } catch (e: Exception) {
            Log.d("EventRepository", "getEvents: ${e.message}")
        }

        // Ambil data dari Room berdasarkan status acara
        val localData: LiveData<Result<List<EventEntity>>> = if (active == 1) {
            eventDao.getUpcomingEvents().map { Result.Success(it) }
        } else {
            eventDao.getFinishedEvents().map { Result.Success(it) }
        }
        emitSource(localData) // Emit data dari sumber lokal
    }

    /**
     * Mengambil daftar acara yang disukai.
     * @return LiveData yang berisi daftar acara favorit.
     */
    fun getFavoriteEvents(): LiveData<List<EventEntity>> {
        return eventDao.getFavoriteEvents()
    }

    /**
     * Menandai atau menghapus tanda acara sebagai favorit.
     * @param events Acara yang akan diupdate.
     * @param favorite Status favorit yang baru.
     */
    suspend fun setFavoriteEvents(events: EventEntity, favorite: Boolean) {
        events.isFavorite = favorite
        eventDao.updateEvents(events) // Update acara di Room
    }

    /**
     * Mencari acara berdasarkan status aktif dan query pencarian.
     * @param active Status acara: 1 untuk acara yang akan datang, 0 untuk acara selesai, -1 untuk favorit.
     * @param query Kata kunci pencarian.
     * @return LiveData yang berisi hasil pencarian acara.
     */
    fun searchEvents(active: Int, query: String): LiveData<Result<List<EventEntity>>> = liveData {
        emit(Result.Loading) // Emit loading state
        val localData: LiveData<Result<List<EventEntity>>>

        try {
            localData = when (active) {
                1 -> eventDao.searchUpcomingEvents(query).map { Result.Success(it) }
                0 -> eventDao.searchFinishedEvents(query).map { Result.Success(it) }
                else -> eventDao.searchFavoriteEvents(query).map { Result.Success(it) }
            }
            emitSource(localData) // Emit data dari sumber lokal
        } catch (e: Exception) {
            Log.e("EventRepository", "Error searching events: ${e.message}")
        }
    }

    /**
     * Mengambil acara terbaru.
     * @return Respons acara terbaru atau null jika terjadi kesalahan.
     */
    suspend fun getLatestEvent(): EventResponse? {
        return try {
            apiService.getEvents(active = -1, limit = 1) // Mengambil acara terbaru
        } catch (e: Exception) {
            Log.e("EventRepository", "Error fetching latest event: ${e.message}")
            null
        }
    }

    companion object {
        @Volatile
        private var instance: EventRepository? = null

        /**
         * Mengambil instansi EventRepository.
         * @param apiService Layanan API untuk mengambil data acara.
         * @param eventDao DAO untuk akses basis data lokal.
         * @return Instansi EventRepository.
         */
        fun getInstance(
            apiService: ApiService,
            eventDao: EventDao,
        ): EventRepository = instance ?: synchronized(this) {
            instance ?: EventRepository(apiService, eventDao).also { instance = it }
        }
    }
}