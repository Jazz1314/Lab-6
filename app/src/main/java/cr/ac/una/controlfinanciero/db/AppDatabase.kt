package cr.ac.una.roommovimiento.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import cr.ac.una.controlfinanciero.FormularioFragment
import cr.ac.una.controlfinanciero.entity.Movimiento
import cr.ac.una.roommovimiento.converter.Converters
import cr.ac.una.roommovimiento.dao.UbicacionDao



@Database(entities = [Movimiento::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun ubicacionDao(): UbicacionDao

    companion object {
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            if (instance == null) {
                synchronized(AppDatabase::class) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "movimiento-database"
                    ).build()
                }
            }
            return instance!!
        }
    }
}