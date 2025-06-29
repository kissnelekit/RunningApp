package com.example.myapplication.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow // Stelle sicher, dass du auch eine Methode hast, die Flow zurückgibt, wenn du ihn importierst.

@Dao
interface WorkoutDAO {
    // Einfügen eines einzelnen Workouts. Bei Konflikt wird das alte ersetzt.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkout(workout: Workout): Long // Dieser ist gut, gibt die rowId zurück.

    // Einfügen mehrerer Workouts
    // @Insert kann List<Long> (die rowIds) oder Unit zurückgeben.
    // Wenn du die IDs nicht brauchst, ist Unit (implizit oder explizit) okay.
    // Um sicherzugehen, explizit machen oder die IDs zurückgeben:
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllWorkouts(workouts: List<Workout>): List<Long> // Oder Unit, wenn IDs nicht benötigt

    // Aktualisieren eines Workouts
    // @Update kann int (Anzahl der aktualisierten Reihen) oder Unit zurückgeben.
    @Update
    suspend fun updateWorkout(workout: Workout): Int // Gibt die Anzahl der aktualisierten Reihen zurück

    // Löschen eines Workouts
    // @Delete kann int (Anzahl der gelöschten Reihen) oder Unit zurückgeben.
    @Delete
    suspend fun deleteWorkout(workout: Workout): Int // Gibt die Anzahl der gelöschten Reihen zurück


    @Query("SELECT * FROM workouts WHERE id = :id") // <--- ÄNDERUNG HIER, falls deine Spalte 'dbId' heißt
    suspend fun getWorkoutById(id: Long): Workout?


    // Gibt alle Workouts zurück, sortiert nach dem übergebenen Spaltennamen.
    // ACHTUNG: Room erlaubt keine dynamischen ORDER BY Klauseln direkt in @Query.
    // Dies erfordert mehrere Methoden oder eine komplexere Strategie.
    // Einfachste Lösung: separate Methoden für gängige Sortierungen.
    @Query("SELECT * FROM workouts")
    suspend fun getAllWorkouts(): List<Workout>

    @Query("SELECT * FROM workouts ORDER BY name ASC")
    suspend fun getAllWorkoutsSortedByName(): List<Workout>

    @Query("SELECT * FROM workouts ORDER BY date DESC")
    suspend fun getAllWorkoutsSortedByDate(): List<Workout>


    // Alle Workouts löschen
    // @Query, die Daten modifiziert, sollte normalerweise Unit zurückgeben,
    // aber explizit ist manchmal besser für KSP.
    @Query("DELETE FROM workouts")
    suspend fun deleteAllWorkouts(): Unit // Explizit Unit gemacht

}