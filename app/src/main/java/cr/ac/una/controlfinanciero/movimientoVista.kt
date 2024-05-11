package cr.ac.una.controlfinanciero

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cr.ac.una.controlfinanciero.entity.Movimiento

class movimientoVista : ViewModel() {
    private val _movimientos = MutableLiveData<List<Movimiento>>()
    var movimientos: LiveData<List<Movimiento>> = _movimientos

    init {
        _movimientos.value = mutableListOf()
    }

    fun getMovimientos() {
        _movimientos.postValue(movimientos as List<Movimiento>)
    }

    fun addMovimiento(movimiento: Movimiento) {
        val lista = _movimientos.value?.toMutableList() ?: mutableListOf()
        lista.add(movimiento)
        _movimientos.postValue(lista)
    }

    fun deleteMovimiento(movimiento: Movimiento) {
        val lista = _movimientos.value?.toMutableList() ?: mutableListOf()
        lista.remove(movimiento)
        _movimientos.postValue(lista)
    }

    fun updateMovimiento(oldMovimiento: Movimiento, newMovimiento: Movimiento) {
        val currentList = _movimientos.value?.toMutableList() ?: mutableListOf()
        val index = currentList.indexOfFirst { it == oldMovimiento }
        if (index != -1) {
            currentList[index] = newMovimiento
            _movimientos.value = currentList
        }
    }


}
