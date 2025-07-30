package com.example.fenoapp.ui.editar_monitoria

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.fenoapp.databinding.FragmentEditarMonitoriaBinding

class EditarMonitoriaFragment : Fragment() {

    private var _binding: FragmentEditarMonitoriaBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: EditarMonitoriaViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditarMonitoriaBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(EditarMonitoriaViewModel::class.java)

        // Exemplo de observação de LiveData
        viewModel.text.observe(viewLifecycleOwner) { texto ->
            binding.textEditarMonitoria.text = texto
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
