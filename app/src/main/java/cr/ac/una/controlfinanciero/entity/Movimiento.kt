package cr.ac.una.controlfinanciero.entity
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.util.Date

@Entity
data class Movimiento(
    @PrimaryKey(autoGenerate = true) val id: Long?, var monto : Double, var tipo: Int, var fecha :Date) : Serializable
