package com.example.fenoapp

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fenoapp.databinding.FragmentItemBinding
import com.example.fenoapp.model.Monitoria // importe o modelo real

class MonitoriaAdapter(
    private val monitorias: List<Monitoria>
) : RecyclerView.Adapter<MonitoriaAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = FragmentItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val monitoria = monitorias[position]
        holder.disciplinaView.text = (monitoria.disciplina ?: "Sem disciplina").toString()
        holder.horarioView.text = monitoria.data_hora?.substring(11, 16) ?: "Horário indefinido"
    }

    override fun getItemCount(): Int = monitorias.size

    inner class ViewHolder(binding: FragmentItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val disciplinaView: TextView = binding.itemNumber
        val horarioView: TextView = binding.content

        override fun toString(): String {
            return super.toString() + " - ${disciplinaView.text} @ ${horarioView.text}"
        }
    }
}
