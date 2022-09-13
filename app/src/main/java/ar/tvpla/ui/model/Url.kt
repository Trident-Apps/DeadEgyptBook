package ar.tvpla.ui.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Url(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val url: String = ""
)