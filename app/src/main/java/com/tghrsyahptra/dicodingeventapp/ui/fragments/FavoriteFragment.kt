package com.tghrsyahptra.dicodingeventapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.tghrsyahptra.dicodingeventapp.databinding.FragmentFavoriteBinding
import com.tghrsyahptra.dicodingeventapp.ui.adapters.VerticalAdapter
import com.tghrsyahptra.dicodingeventapp.ui.viewmodels.MainViewModel
import com.tghrsyahptra.dicodingeventapp.ui.viewmodels.ViewModelFactory

class FavoriteFragment : Fragment() {

    // Binding untuk menghubungkan layout dengan fragment
    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!

    // Inisialisasi ViewModel untuk mengelola data
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }

    // Adapter untuk menampilkan daftar acara
    private lateinit var verticalAdapter: VerticalAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Menginflate layout fragment dan mengembalikan root view
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Mengatur adapter untuk RecyclerView
        setupVerticalAdapter()
        // Mengamati data acara favorit dari ViewModel
        observeFavoriteEvents()
        // Mengatur RecyclerView
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
        // Mengatur status loading pada adapter
        verticalAdapter.setLoadingState(true)
    }

    private fun observeFavoriteEvents() {
        // Mengamati perubahan pada daftar acara favorit
        viewModel.getFavoriteEvents().observe(viewLifecycleOwner) { favoriteEvents ->
            binding.progressBar.visibility = View.GONE // Menyembunyikan progress bar saat data siap
            verticalAdapter.setLoadingState(false) // Menghapus status loading pada adapter
            verticalAdapter.submitList(favoriteEvents) // Menampilkan daftar acara favorit
            updateUI(favoriteEvents.isEmpty()) // Memperbarui UI jika tidak ada acara
        }
    }

    private fun setupRecyclerView() {
        // Mengatur RecyclerView dengan LinearLayoutManager dan adapter
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true) // Mengoptimalkan ukuran RecyclerView
            adapter = verticalAdapter
        }
    }


    private fun updateUI(isEmpty: Boolean) {
        // Memperbarui tampilan berdasarkan status isEmpty
        binding.apply {
            progressBar.visibility = View.GONE // Menyembunyikan progress bar
            noDataFoundLottie.visibility = if (isEmpty) View.VISIBLE else View.GONE // Menampilkan animasi jika tidak ada data
            recyclerView.visibility = if (isEmpty) View.GONE else View.VISIBLE // Menampilkan RecyclerView jika ada data
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Menghapus binding saat view dihancurkan untuk mencegah kebocoran memori
        _binding = null
    }
}