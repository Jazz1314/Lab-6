package cr.ac.una.controlfinanciero

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import cr.ac.una.controlfinanciero.controller.MovimientoController
import cr.ac.una.controlfinanciero.databinding.FragmentFormularioBinding
import cr.ac.una.controlfinanciero.entity.Movimiento
import cr.ac.una.roommovimiento.dao.UbicacionDao
import cr.ac.una.roommovimiento.db.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.Date


class FormularioFragment : Fragment() {

    var movimientoController: MovimientoController = MovimientoController()

    private var _binding: FragmentFormularioBinding? = null
    lateinit var captureButton: Button
    lateinit var imageView: ImageView

    private lateinit var movimientoDao: UbicacionDao
    private val binding get() = _binding!!

    private lateinit var vista: movimientoVista

    private val args: FormularioFragmentArgs by navArgs()

    private val requestCameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            dispatchTakePictureIntent()
        } else {
// Permiso denegado, manejar la situación aquí si es necesario
        }
    }
    private val takePictureLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val imageBitmap = result.data?.extras?.get("data") as? Bitmap
            imageView.setImageBitmap(imageBitmap)
        } else {
            // Manejar el caso en el que no se haya podido capturar la imagen
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //movimientoDao = AppDatabase.getInstance(this).ubicacionDao()
        movimientoDao = AppDatabase.getInstance(requireContext()).ubicacionDao()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        _binding = FragmentFormularioBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        captureButton = view.findViewById(R.id.captureButton)
        imageView = view.findViewById(R.id.imageView)

        vista = ViewModelProvider(requireActivity()).get(movimientoVista::class.java)

        val movimiento = args.movimiento


        //mover a metodos independientes

        if (movimiento != null) {

            // Mostrar alerta de edición
            //mostrarAlertaEdicion()

            val calendar = Calendar.getInstance().apply {
                time = movimiento.fecha
            }
            binding.datePicker.updateDate(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )

            binding.spinnerPaymentMethod.setSelection(movimiento.tipo - 1)

            binding.editTextAmount.setText(movimiento.monto.toString())

            captureButton.setOnClickListener {
                if (checkCameraPermission()) {
                    dispatchTakePictureIntent()
                } else {
                    requestCameraPermission()
                }
            }

            binding.button.setOnClickListener {
                val movimientoEditado = createMovimiento()

                if (movimientoEditado != null) {
                    vista.updateMovimiento(movimiento, movimientoEditado) //UPDATE
                    updateEntity(movimientoEditado)
                    findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
                } else {
                    Toast.makeText(context, "ERROR", Toast.LENGTH_LONG).show()
                }
            }


        } else {
            binding.button.setOnClickListener {
                // Mostrar el diálogo de confirmación antes de agregar el movimiento
                mostrarDialogoConfirmacion()
            }

            captureButton.setOnClickListener {
                if (checkCameraPermission()) {
                    dispatchTakePictureIntent()
                } else {
                    requestCameraPermission()
                }
            }

            binding.button2.setOnClickListener {
                findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
            }
        }

    }
    private fun checkCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }
    private fun requestCameraPermission() {
        requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
    }
    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(requireContext().packageManager)?.also {
                takePictureLauncher.launch(takePictureIntent)
            }
        }
    }
    private fun mostrarDialogoConfirmacion() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Confirmar")
        builder.setMessage("¿Desea agregar este movimiento?")
        builder.setPositiveButton("Sí") { _, _ ->
            // El usuario ha seleccionado "Sí", proceder con la creación del movimiento
            agregarMovimiento()
        }
        builder.setNegativeButton("No") { _, _ ->
            // El usuario ha seleccionado "No", no hacer nada
        }
        builder.show()
    }

    /*private fun mostrarAlertaEdicion() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Alerta de Edición")
        builder.setMessage("Está editando una entrada existente.")
        builder.setPositiveButton("Aceptar") { _, _ ->
            // El usuario ha aceptado la alerta
        }
        builder.show()
    }*/

    private fun agregarMovimiento() {
        val movimiento = createMovimiento()
        if (movimiento != null) {
            insertEntity(movimiento)
            vista.addMovimiento(movimiento)
            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
        } else {
            Toast.makeText(context, "ERROR", Toast.LENGTH_LONG).show()
        }
    }

    private fun insertEntity(entity: Movimiento) {
        lifecycleScope.launch {
            withContext(Dispatchers.Default) {
                movimientoDao.insert(entity)
            }
        }

    }




    private fun createMovimiento(): Movimiento? {
        val datePicker = binding.datePicker
        val spinnerPaymentMethod = binding.spinnerPaymentMethod
        val editTextAmount = binding.editTextAmount
        val submitButton = binding.button

        try {

            val calendar = Calendar.getInstance()
            calendar.set(datePicker.year, datePicker.month, datePicker.dayOfMonth)
            val selectedDate: Date = calendar.time
            val selectedPaymentMethod = spinnerPaymentMethod.selectedItem.toString()
            val amount = editTextAmount.text.toString()

            var tipo = 0

            if (selectedPaymentMethod == "Credito") {
                tipo = 1
            } else if (selectedPaymentMethod == "Debito") {
                tipo = 2
            }


            if (movimientoController.validarDecimales(amount)) {
                Log.i("FormularioFragment", "TODO GUD")

                Log.i("FormularioFragment", "Selected Date: $selectedDate")
                Log.i("FormularioFragment", "Payment Method: $selectedPaymentMethod")
                Log.i("FormularioFragment", "Amount: $amount")

                val move = Movimiento(
                    id = null,
                    amount.toDouble(),
                    tipo,
                    selectedDate
                )

                return move


            } else {
                Log.i("FormularioFragment", "Amount: $amount")
                Log.i("FormularioFragment", "TODO MAL")
                Toast.makeText(context, "Maximo dos decimales", Toast.LENGTH_SHORT).show()
            }

        } catch (e: Exception) {
            Log.i("FormularioFragment", "Error: ${e.message}")
        }



        return null
    }


    private fun updateEntity(entity: Movimiento) {
        lifecycleScope.launch {
            withContext(Dispatchers.Default) {
                movimientoDao.update(entity)
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}
