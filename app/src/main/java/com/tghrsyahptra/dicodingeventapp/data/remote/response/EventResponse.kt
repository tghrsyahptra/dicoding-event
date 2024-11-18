package com.tghrsyahptra.dicodingeventapp.data.remote.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import com.google.gson.annotations.SerializedName

/**
 * Data class untuk menerima respon event dari API.
 *
 * @property listEvents Daftar item event.
 * @property error Status error dari respon.
 * @property message Pesan dari respon.
 */
@Parcelize
data class EventResponse(
	@field:SerializedName("listEvents") val listEvents: List<ListEventsItem>? = null,
	@field:SerializedName("error") val error: Boolean? = null,
	@field:SerializedName("message") val message: String? = null
) : Parcelable

/**
 * Data class untuk setiap item event dalam respon.
 *
 * @property id ID unik event.
 * @property name Nama event.
 * @property summary Ringkasan event.
 * @property description Deskripsi lengkap event.
 * @property imageLogo URL logo event.
 * @property mediaCover URL gambar sampul media event.
 * @property category Kategori event.
 * @property ownerName Nama pemilik event.
 * @property cityName Nama kota tempat event.
 * @property quota Kuota peserta event.
 * @property registrants Jumlah peserta yang mendaftar.
 * @property beginTime Waktu mulai event.
 * @property endTime Waktu selesai event.
 * @property link Tautan ke informasi lebih lanjut tentang event.
 */
@Parcelize
data class ListEventsItem(
	@field:SerializedName("id") val id: Int? = null,
	@field:SerializedName("name") val name: String? = null,
	@field:SerializedName("summary") val summary: String? = null,
	@field:SerializedName("description") val description: String? = null,
	@field:SerializedName("imageLogo") val imageLogo: String? = null,
	@field:SerializedName("mediaCover") val mediaCover: String? = null,
	@field:SerializedName("category") val category: String? = null,
	@field:SerializedName("ownerName") val ownerName: String? = null,
	@field:SerializedName("cityName") val cityName: String? = null,
	@field:SerializedName("quota") val quota: Int? = null,
	@field:SerializedName("registrants") val registrants: Int? = null,
	@field:SerializedName("beginTime") val beginTime: String? = null,
	@field:SerializedName("endTime") val endTime: String? = null,
	@field:SerializedName("link") val link: String? = null
) : Parcelable