package com.tghrsyahptra.dicodingeventapp.utils

// Kelas tertutup untuk representasi hasil operasi
sealed class Result<out T> {
    object Loading : Result<Nothing>() // Menunjukkan status pemrosesan yang sedang berlangsung
    data class Success<out T>(val data: T) : Result<T>() // Menunjukkan hasil yang berhasil
    data class Error(val exception: Exception) : Result<Nothing>() // Menunjukkan adanya kesalahan
}