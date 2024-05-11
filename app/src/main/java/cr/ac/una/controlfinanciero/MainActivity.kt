package cr.ac.una.controlfinanciero

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import cr.ac.una.controlfinanciero.adapter.MovimientoAdapter
import cr.ac.una.controlfinanciero.controller.MovimientoController
import cr.ac.una.controlfinanciero.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    var movimientoController: MovimientoController = MovimientoController()

    lateinit var adapter: MovimientoAdapter
    lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

//        setContentView(R.layout.activity_main)

        setContentView(binding.root)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
//        appBarConfiguration = AppBarConfiguration(navController.graph)
//        setupActionBarWithNavController(navController, appBarConfiguration)

//        val botonNuevo = findViewById<Button>(R.id.botonNuevo)
//        botonNuevo.setOnClickListener{
//            insertEntity()
//        }

//        binding.botonNuevo.setOnClickListener{
//            navController.navigate(R.id.action_FirstFragment_to_SecondFragment)
//        }


//        adapter = MovimientoAdapter(this, movimientoController.movimientos)
//        val list = findViewById<ListView>(R.id.listaMovimientos)
//        list.adapter  = adapter


//        activityResultLauncher =
//            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
//                if (it.resultCode == RESULT_OK) {
//                    val data: Intent? = it.data
//                    if (data != null) {
//                        movimientoController.insertMovimiento(
//                            data.getSerializableExtra("resultado")
//                                    as Movimiento
//                        )
//                        adapter.notifyDataSetChanged()
//                    }
//                }
//            }
    }

    private fun insertEntity() {
        var intent = Intent(this, CrearMovimiento::class.java)
        activityResultLauncher.launch(intent)
    }


}