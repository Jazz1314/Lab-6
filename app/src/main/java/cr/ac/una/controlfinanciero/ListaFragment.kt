package cr.ac.una.controlfinanciero

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import cr.ac.una.controlfinanciero.adapter.MovimientoAdapter
import cr.ac.una.controlfinanciero.databinding.FragmentListaBinding
import cr.ac.una.controlfinanciero.entity.Movimiento
import cr.ac.una.roommovimiento.dao.UbicacionDao
import cr.ac.una.roommovimiento.db.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ListaFragment : Fragment() {

//    var movimientoController: MovimientoController = MovimientoController()


    private var _binding: FragmentListaBinding? = null
    private val binding get() = _binding!!

    private lateinit var movimientos: List<Movimiento>

    private lateinit var vista: movimientoVista
    private lateinit var adapter: MovimientoAdapter

    private lateinit var movimientoDao: UbicacionDao
//editar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        movimientoDao = AppDatabase.getInstance(requireContext()).ubicacionDao()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        _binding = FragmentListaBinding.inflate(inflater, container, false)
        return binding.root

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vista = ViewModelProvider(requireActivity()).get(movimientoVista::class.java)

//        val listView = view.findViewById<ListView>(R.id.listaMovimientos)
        val listView = binding.listaMovimientos

        movimientos = mutableListOf<Movimiento>()

//        adapter = MovimientoAdapter(requireContext(), vista.movimientos.value ?: mutableListOf())

        adapter = MovimientoAdapter(
            requireContext(),
            vista.movimientos.value ?: mutableListOf(), { movimiento ->
                vista.deleteMovimiento(movimiento)
                deleteEntity(movimiento)
            }, { movimiento ->
                val action = ListaFragmentDirections.actionFirstFragmentToSecondFragment(movimiento)
                findNavController().navigate(action)
            }
        )



        listView.adapter = adapter


//        listView.layoutManager = LinearLayoutManager(requireContext())

        vista.movimientos.observe(viewLifecycleOwner) { movimientos ->
            adapter.updateList(movimientos)
        }

        binding.botonNuevo.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }

    }
    private fun deleteEntity(entity: Movimiento) {
        lifecycleScope.launch {
            withContext(Dispatchers.Default) {
                movimientoDao.delete(entity)
            }
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}