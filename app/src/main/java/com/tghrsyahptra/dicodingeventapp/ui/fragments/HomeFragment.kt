package com.tghrsyahptra.dicodingeventapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.tghrsyahptra.dicodingeventapp.data.local.entity.EventEntity
import com.tghrsyahptra.dicodingeventapp.databinding.FragmentHomeBinding
import com.tghrsyahptra.dicodingeventapp.ui.adapters.HorizontalAdapter
import com.tghrsyahptra.dicodingeventapp.ui.adapters.VerticalAdapter
import com.tghrsyahptra.dicodingeventapp.ui.viewmodels.MainViewModel
import com.tghrsyahptra.dicodingeventapp.ui.viewmodels.ViewModelFactory
import com.tghrsyahptra.dicodingeventapp.utils.Result

class HomeFragment : Fragment() {

    // Binding untuk menghubungkan fragment dengan layout
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    // Adapter untuk menampilkan daftar acara vertikal dan horizontal
    private lateinit var verticalAdapter: VerticalAdapter
    private lateinit var horizontalAdapter: HorizontalAdapter

    // Inisialisasi ViewModel untuk mengelola data acara
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Menginflate layout fragment dan mengembalikan root view
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAdapters()
        observeEventData()
        setupRecyclerViews()
    }

    private fun setupAdapters() {
        // Menginisialisasi adapter untuk acara horizontal dan vertikal
        horizontalAdapter = HorizontalAdapter { event ->
            toggleFavoriteStatus(event)
        }

        verticalAdapter = VerticalAdapter { event ->
            toggleFavoriteStatus(event)
        }

        // Set status loading untuk menunjukkan efek loading
        horizontalAdapter.setLoadingState(true)
        verticalAdapter.setLoadingState(true)
    }

    private fun toggleFavoriteStatus(event: EventEntity) {
        // Menyimpan atau menghapus acara dari favorit
        if (event.isFavorite == true) {
            viewModel.deleteEvents(event) // Hapus acara dari favorit
        } else {
            viewModel.saveEvents(event) // Simpan acara sebagai favorit
        }
    }

    private fun observeEventData() {
        // Mengamati hasil acara yang akan datang
        viewModel.getUpcomingEvents().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE // Tampilkan progress bar saat loading
                    horizontalAdapter.setLoadingState(true)
                }
                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE // Sembunyikan progress bar saat data berhasil dimuat
                    horizontalAdapter.setLoadingState(false)
                    horizontalAdapter.submitList(result.data)
                }
                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE // Sembunyikan progress bar saat terjadi kesalahan
                    // Pesan kesalahan dapat ditambahkan di sini
                }
            }
        }

        // Mengamati hasil acara yang telah selesai
        viewModel.getFinishedEvents().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE // Tampilkan progress bar saat loading
                    verticalAdapter.setLoadingState(true)
                }
                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE // Sembunyikan progress bar saat data berhasil dimuat
                    verticalAdapter.setLoadingState(false)
                    verticalAdapter.submitList(result.data) // Tampilkan semua acara yang telah selesai
                }
                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE // Sembunyikan progress bar saat terjadi kesalahan
                    // Pesan kesalahan dapat ditambahkan di sini
                }
            }
        }
    }

    private fun setupRecyclerViews() {
        // Mengatur RecyclerView untuk acara yang akan datang
        binding.upcomingRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = horizontalAdapter
        }

        // Mengatur RecyclerView untuk acara yang telah selesai
        binding.finishedRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = verticalAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Menghapus binding untuk mencegah kebocoran memori
        _binding = null
    }
}