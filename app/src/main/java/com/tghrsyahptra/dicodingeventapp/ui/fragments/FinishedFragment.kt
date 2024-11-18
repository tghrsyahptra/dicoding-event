package com.tghrsyahptra.dicodingeventapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.tghrsyahptra.dicodingeventapp.databinding.FragmentFinishedBinding
import com.tghrsyahptra.dicodingeventapp.ui.adapters.VerticalAdapter
import com.tghrsyahptra.dicodingeventapp.ui.viewmodels.MainViewModel
import com.tghrsyahptra.dicodingeventapp.ui.viewmodels.ViewModelFactory
import com.tghrsyahptra.dicodingeventapp.utils.Result

class FinishedFragment : Fragment() {

    // Binding untuk menghubungkan fragment dengan layout
    private var _binding: FragmentFinishedBinding? = null
    private val binding get() = _binding!!

    // Adapter untuk menampilkan daftar acara selesai
    private lateinit var verticalAdapter: VerticalAdapter

    // Inisialisasi ViewModel untuk mengelola data acara
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Menginflate layout fragment dan mengembalikan root view
        _binding = FragmentFinishedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupVerticalAdapter()
        observeFinishedEvents()
        setupRecyclerView()
    }

    private fun setupVerticalAdapter() {
        // Menginisialisasi adapter dengan aksi untuk menyimpan atau menghapus acara favorit
        verticalAdapter = VerticalAdapter { event ->
            if (event.isFavorite == true) {
                viewModel.deleteEvents(event) // Hapus acara dari favorit
            } else {
                viewModel.saveEvents(event) // Simpan acara sebagai favorit
            }
        }
        verticalAdapter.setLoadingState(true) // Set status loading awal
    }

    private fun observeFinishedEvents() {
        // Mengamati hasil acara yang telah selesai
        viewModel.getFinishedEvents().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE // Tampilkan progress bar saat loading
                }
                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE // Sembunyikan progress bar saat data berhasil dimuat
                    verticalAdapter.setLoadingState(false) // Hapus status loading pada adapter
                    verticalAdapter.submitList(result.data) // Tampilkan data acara selesai
                }
                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE // Sembunyikan progress bar saat terjadi kesalahan
                    // Menampilkan pesan kesalahan dapat ditambahkan di sini
                }
            }
        }
    }

    private fun setupRecyclerView() {
        // Mengatur RecyclerView dengan LinearLayoutManager dan adapter
        binding.recyclerView.apply {
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