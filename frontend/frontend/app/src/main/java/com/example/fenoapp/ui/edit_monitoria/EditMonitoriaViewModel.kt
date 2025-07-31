package com.example.fenoapp.ui.editar_monitoria

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class EditarMonitoriaViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Aqui você vai editar a monitoria"
    }
    val text: LiveData<String> = _text

   
}
