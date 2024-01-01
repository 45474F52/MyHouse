package com.aes.myhome.storage.database.repositories

import com.aes.myhome.storage.database.daos.ScheduleEventDAO
import com.aes.myhome.storage.database.entities.ScheduleEvent
import javax.inject.Inject

class ScheduleEventRepository @Inject constructor(
    private val scheduleEventDAO: ScheduleEventDAO
) {

    /**
     * Возвращает все записи
     */
    suspend fun getAll() = scheduleEventDAO.getAll()

    /**
     * Находит запись по Id
     */
    suspend fun findById(id: Int) = scheduleEventDAO.findById(id)

    /**
     * Находит все записи, где category = param(category)
     */
    suspend fun getByCategory(category: String) = scheduleEventDAO.getByCategory(category)

    /**
     * Вставляет записи
     */
    suspend fun insertAll(vararg events: ScheduleEvent) = scheduleEventDAO.insertEvents(*events)

    /**
     * Обновляет записи
     */
    suspend fun updateAll(vararg events: ScheduleEvent) = scheduleEventDAO.updateEvents(*events)

    /**
     * Удаляет записи
     */
    suspend fun deleteAll(vararg events: ScheduleEvent) = scheduleEventDAO.deleteEvents(*events)

}