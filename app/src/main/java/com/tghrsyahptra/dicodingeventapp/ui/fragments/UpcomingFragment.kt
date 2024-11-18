package com.tghrsyahptra.dicodingeventapp.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.tghrsyahptra.dicodingeventapp.databinding.FragmentUpcomingBinding
import com.tghrsyahptra.dicodingeventapp.ui.viewmodels.MainViewModel
import com.tghrsyahptra.dicodingeventapp.ui.adapters.VerticalAdapter
import com.tghrsyahptra.dicodingeventapp.utils.Result
import com.tghrsyahptra.dicodingeventapp.ui.viewmodels.ViewModelFactory

class UpcomingFragment : Fragment() {

    private var _binding: FragmentUpcomingBinding? = null
    private val binding get() = _binding!!

    private lateinit var verticalAdapter: VerticalAdapter

    // Instance ViewModel
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Menginflasi layout untuk fragment ini
        _binding = FragmentUpcomingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi adapter untuk recycler view
        verticalAdapter = VerticalAdapter { event ->
            // Menangani perubahan status favorit untuk event
            if (event.isFavorite == true) {
                viewModel.deleteEvents(event) // Hapus dari favorit
            } else {
                viewModel.saveEvents(event) // Tambahkan ke favorit
            }
        }

        // Atur status loading awal untuk menampilkan efek shimmer
        verticalAdapter.setLoadingState(true)

        // Amati upcoming events dari ViewModel
        viewModel.getUpcomingEvents().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE // Tampilkan indikator loading
                    verticalAdapter.setLoadingState(true)
                }

                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE // Sembunyikan indikator loading
                    verticalAdapter.setLoadingState(false)
                    verticalAdapter.submitList(result.data) // Kirim daftar event ke adapter
                }

                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE // Sembunyikan indikator loading saat terjadi error
                }
            }
        }

        // Siapkan RecyclerView
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = verticalAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Bersihkan binding saat view dihancurkan
    }
}