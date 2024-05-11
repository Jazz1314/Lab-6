package cr.ac.una.roommovimiento.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import cr.ac.una.controlfinanciero.entity.Movimiento



@Dao
interface UbicacionDao {
    @Insert
    fun insert(entity: Movimiento)
    @Query("SELECT * FROM movimiento")
    fun getAll(): List<Movimiento?>?
    @Delete
    fun delete(entity: Movimiento)
    @Update
    fun update(entity: Movimiento)
}