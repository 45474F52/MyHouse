package com.aes.myhome.storage.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Entity(
    tableName = "ScheduleEvent"
)
data class ScheduleEvent(

    @ColumnInfo(name = "date") var date: String,
    @ColumnInfo(name = "description") var description: String,
    @ColumnInfo(name = "category") var category: String,
    @ColumnInfo(name = "notify") var notify: Boolean,
    @ColumnInfo(name = "notificationDateTime") var notificationDateTime: String?

) {
    @PrimaryKey(autoGenerate = true) var eventId: Int = 0

    fun parseDate(): LocalDate =
        LocalDate.parse(date, DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT))
    fun parseNotificationDateTime(): LocalDateTime =
        LocalDateTime.parse(notificationDateTime, DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT))

    val dayOfWeek: String
        get() = parseDate().dayOfWeek.name

    val dayNumber: Int
        get() = parseDate().dayOfMonth

    override fun toString(): String {
        val words = description.split(' ', ignoreCase = true, limit = 3)
        words.forEach { x -> x.trim() }
        return words.joinToString(" ")
    }
}
