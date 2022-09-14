package ar.tvpla.ui.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Url::class], version = 1)
abstract class UrlDatabase : RoomDatabase() {

    abstract fun getDao(): UrlDao

    companion object {
        @Volatile
        private var instance: UrlDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also { instance = it }
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context.applicationContext, UrlDatabase::class.java, "urlDatabase")
                .allowMainThreadQueries()
                .build()
    }
}