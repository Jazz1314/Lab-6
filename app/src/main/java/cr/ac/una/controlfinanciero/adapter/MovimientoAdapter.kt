package cr.ac.una.controlfinanciero.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import cr.ac.una.controlfinanciero.R
import cr.ac.una.controlfinanciero.entity.Movimiento
import cr.ac.una.roommovimiento.dao.UbicacionDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MovimientoAdapter(
    private val context: Context,
    private var movimientos: List<Movimiento>,
    private var onDeleteClickListener: ((Movimiento) -> Unit),
    private val onEditClickListener: (Movimiento) -> Unit
) : BaseAdapter() {
    private lateinit var movimientoDao: UbicacionDao
    fun updateList(newMovimientos: List<Movimiento>) {
        movimientos = newMovimientos
        notifyDataSetChanged()
    }

    override fun getCount(): Int = movimientos.size

    override fun getItem(position: Int): Movimiento = movimientos[position]

    override fun getItemId(position: Int): Long = position.toLong()


    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false)

        val monto = view.findViewById<TextView>(R.id.monto)
        val tipo = view.findViewById<TextView>(R.id.tipo)
        val fecha = view.findViewById<TextView>(R.id.fecha)

        val movimiento = getItem(position)
        monto.text = movimiento.monto.toString()
        tipo.text = movimiento.tipo.toString()
        fecha.text = movimiento.fecha.toString()

        val deleteButton = view.findViewById<ImageButton>(R.id.borrar)

        deleteButton.setOnClickListener {
            showDeleteConfirmationDialog(movimiento)
        }

        val editButton = view.findViewById<ImageButton>(R.id.editar)

        editButton.setOnClickListener {
            onEditClickListener.invoke(movimiento)
        }

        return view
    }

    private fun showDeleteConfirmationDialog(movimiento: Movimiento) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Confirmar eliminación")
        builder.setMessage("¿Está seguro que desea eliminar este movimiento?")

        builder.setPositiveButton("Eliminar") { dialog, _ ->
            onDeleteClickListener.invoke(movimiento)
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }


}
