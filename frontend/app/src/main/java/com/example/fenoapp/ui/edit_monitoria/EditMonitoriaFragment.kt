package com.example.fenoapp.ui.edit_monitoria

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.fenoapp.databinding.FragmentEditMonitoriaBinding

class EditMonitoriaFragment : Fragment() {

    private var _binding: FragmentEditMonitoriaBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: EditMonitoriaViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditMonitoriaBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(EditMonitoriaViewModel::class.java)

        // Exemplo de observação de LiveData
        viewModel.text.observe(viewLifecycleOwner) { texto ->
            binding.textEditMonitoria.text = texto
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
