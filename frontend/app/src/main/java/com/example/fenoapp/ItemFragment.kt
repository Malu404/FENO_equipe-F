package com.example.fenoapp

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.*
import com.example.fenoapp.databinding.FragmentItemListBinding
import com.example.fenoapp.model.Monitoria
import com.example.fenoapp.network.RetrofitClient
import kotlinx.coroutines.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ItemFragment : Fragment() {

    private var columnCount = 1
    private var _binding: FragmentItemListBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: MonitoriaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentItemListBinding.inflate(inflater, container, false)
        val view = binding.root

        val layoutManager = if (columnCount <= 1)
            LinearLayoutManager(context)
        else
            GridLayoutManager(context, columnCount)

        binding.list.layoutManager = layoutManager

        // Chamada à API com token recuperado via TokenManager
        CoroutineScope(Dispatchers.Main).launch {
            val tokenManager = TokenManager(requireContext())
            val token = tokenManager.getToken()

            if (token != null) {
                val monitorias = withContext(Dispatchers.IO) {
                    val api = RetrofitClient.criarApiServiceComToken("Bearer $token")
                    val response = api.getTodasMonitorias("Bearer $token")
                    response.sortedBy {
                        LocalDateTime.parse(it.data_hora, DateTimeFormatter.ISO_DATE_TIME)
                    }
                }

                adapter = MonitoriaAdapter(monitorias)
                binding.list.adapter = adapter
            } else {
                // Você pode exibir uma mensagem caso o token esteja nulo
            }
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val ARG_COLUMN_COUNT = "column-count"

        @JvmStatic
        fun newInstance(columnCount: Int) =
            ItemFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}
