package com.example.myapplication.data
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.myapplication.data.interval.IntervalConverters
import com.example.myapplication.data.runningtype.RunningTypeConverters

@Database(entities = [Workout::class], version = 1, exportSchema = false)
@TypeConverters(IntervalConverters::class, RunningTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun workoutDao(): WorkoutDAO

    companion object {
        // Das Volatile sorgt dafür, dass der Wert von INSTANCE immer aktuell ist und
        // für alle Threads gleich (wichtig für Singleton).
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            // synchronized stellt sicher, dass nur ein Thread gleichzeitig diese Instanz erstellt,
            // falls sie noch nicht existiert.
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "workout_database" // Name der Datenbankdatei
                )
                    // Hier könntest du Migrationen hinzufügen, falls sich dein Schema ändert:
                    // .addMigrations(MIGRATION_1_2)
                    .fallbackToDestructiveMigration() // NUR FÜR ENTWICKLUNG: Löscht die DB bei Schemaänderung ohne Migration
                    .build()
                INSTANCE = instance
                instance // Gib die neu erstellte Instanz zurück
            }
        }
    }



}