package com.example.fenoapp.ui.edit_monitoria

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class EditMonitoriaViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Aqui você vai editar a monitoria"
    }
    val text: LiveData<String> = _text

   
}
